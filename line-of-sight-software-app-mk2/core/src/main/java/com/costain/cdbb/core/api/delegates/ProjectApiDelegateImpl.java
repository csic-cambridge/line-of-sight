/*
 * Copyright © 2022 Costain Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.costain.cdbb.core.api.delegates;


import com.costain.cdbb.api.ProjectApiDelegate;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.Project;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectWithId;
import com.costain.cdbb.model.helpers.ProjectHelper;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;





@Service
public class ProjectApiDelegateImpl implements ProjectApiDelegate {

    private ProjectRepository repository;
    private ProjectHelper projectHelper;

    @Autowired
    AssetDataDictionaryRepository assetDataDictionaryRepository;

    @Autowired
    FunctionalOutputDataDictionaryRepository foDataDictionaryRepository;

    @Autowired
    OrganisationalObjectiveRepository organisationalObjectiveRepository;

    @Autowired
    public void setRepository(ProjectRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setProjectHelper(ProjectHelper projectHelper) {
        this.projectHelper = projectHelper;
    }

    @Override
    public Mono<ResponseEntity<Flux<ProjectWithId>>> findAllProjects(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findAllByOrderByNameAsc())
                .map(dao -> projectHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<ProjectWithId>> findProjectById(UUID id, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> projectHelper.fromDao(repository.findById(id).orElse(null)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<ProjectWithId>> addProject(
        Mono<Project> project, ServerWebExchange exchange) {
        return project.map(dto ->
                                projectHelper.ddsFromDto(dto,
                                    assetDataDictionaryRepository.findById(dto.getAssetDdId()),
                                    foDataDictionaryRepository.findById(dto.getFoDdId())))
                            .flatMap(dao -> {
                                ProjectDAO projectDao = repository.save(dao);
                                Set<OrganisationalObjectiveDAO> ooDaos =
                                    organisationalObjectiveRepository.findByIsDeleted(false);
                                Set<ProjectOrganisationalObjectiveDAO> projectOrganisationalObjectives =
                                    ooDaos.stream().map(oodao -> ProjectOrganisationalObjectiveDAO.builder()
                                                .projectId(projectDao.getId())
                                                .ooVersion(oodao.getOoVersions().get(0))
                                                .frs(new HashSet<>())
                                                .build())
                                                .collect(Collectors.toSet());
                                projectDao.setProjectOrganisationalObjectiveDaos(projectOrganisationalObjectives);
                                return Mono.fromCallable(() -> repository.save(dao));
                            })
                            .map(savedDao -> projectHelper.fromDao(savedDao))
                            .map(ResponseEntity::ok)
                            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Project>> renameProject(UUID id,
                                                       Mono<String> body,
                                                       ServerWebExchange exchange) {
        return body.map(dto -> {
            try {
                return projectHelper.renameProject(dto, id);
            } catch (JSONException e) {
                return null;
            }
        })
        .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
        .map(savedDao -> projectHelper.projectFromDao(savedDao))
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteProject(UUID id, ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> repository.deleteById(id))
            .map(x -> re)
            .defaultIfEmpty(re);
    }

    @Override
    public Mono<ResponseEntity<ProjectWithId>> copyProject(
        UUID originalProjectId, Mono<String> newProjectName, ServerWebExchange exchange) {
        return newProjectName.map(dto -> {
            try {
                return projectHelper.copyFromProject(dto, originalProjectId);
            } catch (JSONException e) {
                return null;
            }
        })
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

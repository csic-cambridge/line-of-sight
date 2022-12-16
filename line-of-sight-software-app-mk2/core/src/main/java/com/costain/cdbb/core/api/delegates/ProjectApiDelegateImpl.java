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
import com.costain.cdbb.model.Project;
import com.costain.cdbb.model.ProjectWithId;
import com.costain.cdbb.model.ProjectWithImportProjectIds;
import com.costain.cdbb.model.helpers.ProjectHelper;
import com.costain.cdbb.model.helpers.ProjectImportExportHelper;
import com.costain.cdbb.repositories.ProjectRepository;
import java.util.UUID;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



/**
 * Handles the api calls from a client with root /api/project.
 */

@Service
public class ProjectApiDelegateImpl implements ProjectApiDelegate {

    @Autowired
    ProjectRepository repository;

    @Autowired
    ProjectHelper projectHelper;

    @Autowired
    ProjectImportExportHelper projectImportExportHelper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * Fetch all projects.
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;ProjectWithId&gt;&gt;&gt;
     * projects</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<ProjectWithId>>> findAllProjects(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findAllByOrderByNameAsc())
                .map(dao -> projectHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Add a project.
     * @param projectWithImportProjectIds the project to be created
     * @return Mono&lt;ResponseEntity&lt;ProjectWithId&gt;&gt; the project added
     */

    @Override
    public Mono<ResponseEntity<ProjectWithId>> addProject(Mono<ProjectWithImportProjectIds> projectWithImportProjectIds,
                                                         ServerWebExchange exchange) {
        return projectWithImportProjectIds.map(dto ->
                                 transactionTemplate.execute(transactionStatus -> projectHelper.ddsFromDto(dto)))
                            .map(savedDao -> projectHelper.fromDao(savedDao))
                            .map(ResponseEntity::ok)
                            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Rename a project.
     * @param projectId the project to be created
     * @param body json with key = name
     * @return Mono&lt;ResponseEntity&lt;Project&gt;&gt; the renamed project
     */
    @Override
    public Mono<ResponseEntity<Project>> renameProject(UUID projectId,
                                                       Mono<String> body,
                                                       ServerWebExchange exchange) {
        return body.map(dto -> {
            try {
                return projectHelper.renameProject(dto, projectId);
            } catch (JSONException e) {
                return null;
            }
        })
        .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
        .map(savedDao -> projectHelper.projectFromDao(savedDao))
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a project.
     * @param projectId the project id of the project to be deleted
     * @return Mono&lt;ResponseEntity&lt;Void&gt;&gt;
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteProject(UUID projectId, ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();

        return Mono.fromRunnable(() -> projectHelper.deleteProjectById(projectId))
            .map(x -> re)
            .defaultIfEmpty(re);
    }

    /**
     * Copy a project.
     * @param originalProjectId the project id of the project to be copied
     * @param newProjectName name for the copied project
     * @return Mono&lt;ResponseEntity&lt;ProjectWithId&gt;&gt; the copied project
     */
    @Override
    public Mono<ResponseEntity<ProjectWithId>> copyProject(
        UUID originalProjectId, Mono<String> newProjectName, ServerWebExchange exchange) {
        return newProjectName.map(dto -> transactionTemplate.execute(transactionStatus -> {
            try {
                return projectHelper.copyFromProject(dto, originalProjectId);
            } catch (JSONException e) {
                return null;
            }
        }))
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Import a project.
     * @param body json created from a previous export of the project
     * @return Mono&lt;ResponseEntity&lt;ProjectWithId&gt;&gt; the imported project
     */
    @Override
    public Mono<ResponseEntity<ProjectWithId>> importProject(Mono<String> body,
                                                             ServerWebExchange exchange) {
        return body.map(dto -> transactionTemplate.execute(transactionStatus -> {
            return projectImportExportHelper.importProjectFile(dto);
        }))
            .map(savedDao -> projectHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Export a project.
     * @param projectId the project id of the project to be exported
     * @return Mono&lt;ResponseEntity&lt;String&gt;&gt; the exported project in JSON format
     */
    @Override
    public Mono<ResponseEntity<String>> exportProject(UUID projectId,
                                                      ServerWebExchange exchange) {
        return Mono.fromCallable(() -> projectImportExportHelper.exportProject(projectId))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Fetch all projects which use the given functional output data dictionary.
     * @param foddid the id of the functional output data dictionary
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;ProjectWithId&gt;&gt;&gt;
     * projects</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<ProjectWithId>>> findProjectsUsingFoDataDictionary(UUID foddid,
                                                                                       ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findByFoDataDictionaryIdOrderByNameAsc(foddid))
                .map(dao -> projectHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Fetch all projects which use the given asset data dictionary.
     * @param assetddid the id of the aset data dictionary
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;ProjectWithId&gt;&gt;&gt;
     * projects</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<ProjectWithId>>> findProjectsUsingAssetDataDictionary(UUID assetddid,
                                                                                          ServerWebExchange exchange) {
        return Mono.fromCallable(() ->
                Flux.fromIterable(repository.findByAssetDataDictionaryIdOrderByNameAsc(assetddid))
                .map(dao -> projectHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

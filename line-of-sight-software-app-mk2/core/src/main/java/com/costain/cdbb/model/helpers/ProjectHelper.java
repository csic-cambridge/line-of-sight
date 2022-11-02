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

package com.costain.cdbb.model.helpers;

import com.costain.cdbb.core.CdbbValidationError;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.Project;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectWithId;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProjectHelper {

    @Autowired
    ProjectRepository repository;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    FunctionalRequirementRepository frRepository;

    @Autowired
    FunctionalOutputRepository foRepository;

    @Autowired
    AssetRepository assetRepository;

    public ProjectWithId fromDao(ProjectDAO dao) {
        ProjectWithId dto = new ProjectWithId();
        dto.id(dao.getId());
        dto.name(dao.getName());
        dto.foDdId(dao.getFoDataDictionary() == null ? null : dao.getFoDataDictionary().getId());
        dto.assetDdId(dao.getAssetDataDictionary() == null ? null : dao.getAssetDataDictionary().getId());
        return dto;
    }

    public Project projectFromDao(ProjectDAO dao) {
        Project dto = new Project();
        dto.name(dao.getName());
        dto.foDdId(dao.getFoDataDictionary().getId());
        dto.assetDdId(dao.getAssetDataDictionary().getId());
        return dto;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ProjectDAO ddsFromDto(Project project,
                                 Optional<AssetDataDictionaryDAO> assetDataDictionaryDao,
                                 Optional<FunctionalOutputDataDictionaryDAO> functionalOutputDataDictionaryDao) {
        checkProjectNameIsUnique(null, project.getName());
        return ProjectDAO.builder()
            .name(project.getName())
            .assetDataDictionary(assetDataDictionaryDao.orElse(null))
            .foDataDictionary(functionalOutputDataDictionaryDao.orElse(null))
            .build();
    }

    public ProjectDAO renameProject(String jsonName, UUID projectId) throws JSONException {
        final String keyName = "name";
        ProjectDAO project = repository.findById(projectId).orElse(null);
        JSONObject jsonObject = new JSONObject(jsonName);

        if (project != null && jsonObject.has(keyName)) {
            String newName = jsonObject.getString(keyName);
            checkProjectNameIsUnique(project, newName);
            project.setName(newName);
        }
        return project;
    }

    private void checkProjectNameIsUnique(ProjectDAO project, String newName) {
        List<ProjectDAO> matchingProjects = repository.findByName(newName);
        if (matchingProjects == null
            || matchingProjects.isEmpty()
            || project != null && project.getId().equals(matchingProjects.get(0).getId())) {
            return;
        }
        // this new name is already in use by another project so reject
        throw new CdbbValidationError("A project named '" + newName + "' already exists");
    }

    private  Set<ProjectOrganisationalObjectiveDAO> copyPooLinkedEntities(
        ProjectDAO sourceProject, ProjectDAO copiedProject) {
        return
            sourceProject.getProjectOrganisationalObjectiveDaos().stream().map(
                dao -> pooRepository.save(ProjectOrganisationalObjectiveDAO.builder()
                    .projectId(copiedProject.getId())
                    .ooVersion(dao.getOoVersion())
                    .frs(saveFrs(copiedProject, dao.getFrs()))
                    .build())).collect(Collectors.toSet());
    }

    private Set<FunctionalRequirementDAO> saveFrs(
        ProjectDAO copiedProject, Set<FunctionalRequirementDAO> sourceFrDaos) {
        // do not save new frs with same name as existing
        Set<FunctionalRequirementDAO> existingFrs = frRepository.findByProjectIdOrderByNameAsc(copiedProject.getId());
        return sourceFrDaos.stream().map(daoFr -> {
            List<FunctionalRequirementDAO> existingFr = existingFrs.stream().filter(
                frDao -> frDao.getName().equals(daoFr.getName())).toList();
            return
                frRepository.save(FunctionalRequirementDAO.builder()
                .id(existingFr.size() == 0 ? null : existingFr.get(0).getId())
                .projectId(copiedProject.getId())
                .name(daoFr.getName())
                .fos(saveFos(copiedProject, daoFr.getFos()))
                .build());
        }).collect(Collectors.toSet());
    }

    private Set<FunctionalOutputDAO> saveFos(ProjectDAO copiedProject, Set<FunctionalOutputDAO> sourceFoDaos) {
        // do not save new fos with same data dictionary entry as existing
        Set<FunctionalOutputDAO> existingFos = foRepository.findByProjectId(copiedProject.getId());
        return sourceFoDaos.stream().map(daoFos -> {
            List<FunctionalOutputDAO> existingFo = existingFos.stream().filter(
                foDao -> foDao.getDataDictionaryEntry().getId()
                    .equals(daoFos.getDataDictionaryEntry().getId())).toList();
            return
                foRepository.save(FunctionalOutputDAO.builder()
                    .id(existingFo.size() == 0 ? null : existingFo.get(0).getId())
                    .projectId(copiedProject.getId())
                    .dataDictionaryEntry(daoFos.getDataDictionaryEntry())
                    .firs(new HashSet<>(daoFos.getFirs()))
                    .assets(saveAssets(copiedProject, daoFos.getAssets()))
                    .build());
        }).collect(Collectors.toSet());
    }

    private Set<AssetDAO> saveAssets(ProjectDAO copiedProject, Set<AssetDAO> sourceAssetDaos) {
        // do not save new assets with same data dictionary entry as existing
        Set<AssetDAO> existingAssets = assetRepository.findByProjectId(copiedProject.getId());
        return sourceAssetDaos.stream().map(daoAssets -> {
            // 'assit' because 'asset' gives CNC_COLLECTION_NAMING_CONFUSION!
            List<AssetDAO> existingAssit = new ArrayList<>();
            for (AssetDAO assetDao : existingAssets) {
                if (Objects.equals(assetDao.getDataDictionaryEntry().getId(),
                    daoAssets.getDataDictionaryEntry().getId())) {
                    existingAssit.add(assetDao);
                }
            }
            return assetRepository.save(AssetDAO.builder()
                .id(existingAssit.size() == 0 ? null : existingAssit.get(0).getId())
                .projectId(copiedProject.getId())
                .airs(Collections.unmodifiableSet(new HashSet<>(daoAssets.getAirs())))
                .dataDictionaryEntry(daoAssets.getDataDictionaryEntry())
                .build());
        }).collect(Collectors.toSet());
    }

    private Set<FunctionalRequirementDAO> findUnlinkedSourceFunctionalRequirements(
        ProjectDAO sourceProject, Set<ProjectOrganisationalObjectiveDAO> savedPooDaos) {
        Set<FunctionalRequirementDAO> sourceFrs = frRepository.findByProjectIdOrderByNameAsc(sourceProject.getId());
        sourceFrs.removeIf(unlinkedFr ->
            savedPooDaos.stream()
                .anyMatch(pooDao -> pooDao.getFrs().stream()
                .anyMatch(pooFr -> pooFr.getName().equals(unlinkedFr.getName()))));
        return sourceFrs;
    }

    private Set<FunctionalOutputDAO> findUnlinkedSourceFunctionalOutputs(
        ProjectDAO sourceProject, Set<FunctionalRequirementDAO> savedFrDaos) {
        Set<FunctionalOutputDAO> unlinkedFos = foRepository.findByProjectId(sourceProject.getId());
        unlinkedFos.removeIf(unlinkedFo ->
            savedFrDaos.stream()
                    .anyMatch(frDao -> frDao.getFos().stream()
                    .anyMatch(foDao -> foDao.getId().equals(unlinkedFo.getId()))));
        return unlinkedFos;
    }

    private Set<AssetDAO> findUnlinkedSourceAssets(
        ProjectDAO sourceProject, Set<FunctionalOutputDAO> savedFoDaos) {
        Set<AssetDAO> unlinkedAssets = assetRepository.findByProjectId(sourceProject.getId());
        unlinkedAssets.removeIf(unlinkedAsset ->
            savedFoDaos.stream()
                .anyMatch(foDao -> foDao.getAssets().stream()
                    .anyMatch(assetDao -> foDao.getId().equals(unlinkedAsset.getId()))));
        return unlinkedAssets;
    }

    public ProjectWithId copyFromProject(String jsonName, UUID sourceProjectId) throws JSONException {
        // This is tricky as not all entities are linked, but unlinked ones may be linked to lower entities
        // The method will be to get the linked entities and copy them to the database
        // Then get the unlinked ones and save them to database
        // continue process through poo, fr, fo and assets
        final String keyName = "name";

        JSONObject jsonObject = new JSONObject(jsonName);
        final String copyName = (String) jsonObject.get(keyName);
        this.checkProjectNameIsUnique(null, copyName);
        ProjectDAO sourceProject = repository.findById(sourceProjectId).orElse(null);
        if (sourceProject != null) {
            ProjectDAO copiedProject = repository.save(ProjectDAO.builder()
                .name(copyName)
                .assetDataDictionary(sourceProject.getAssetDataDictionary())
                .foDataDictionary(sourceProject.getFoDataDictionary())
                .build());
            Set<ProjectOrganisationalObjectiveDAO> pooDaos = copyPooLinkedEntities(sourceProject, copiedProject);
            copiedProject.setProjectOrganisationalObjectiveDaos(pooDaos);

            Set<FunctionalRequirementDAO> unlinkedFrs =
                findUnlinkedSourceFunctionalRequirements(
                    sourceProject, copiedProject.getProjectOrganisationalObjectiveDaos());
            Set<FunctionalRequirementDAO> frs = saveFrs(copiedProject, unlinkedFrs);

            Set<FunctionalOutputDAO> unlinkedFos = findUnlinkedSourceFunctionalOutputs(sourceProject, frs);
            Set<FunctionalOutputDAO> fos = saveFos(copiedProject, unlinkedFos);

            Set<AssetDAO> unlinkedAssets = findUnlinkedSourceAssets(sourceProject, fos);
            saveAssets(copiedProject, unlinkedAssets);

            return fromDao(copiedProject);
        }
        throw new CdbbValidationError("Copy project: source not found - " + sourceProjectId);
    }

    public ProjectDAO fromDto(Project project) {
        return fromDto(ProjectDAO.builder(), project.getName());
    }

    public ProjectDAO fromDto(UUID id, Project project) {
        return fromDto(ProjectDAO.builder().id(id), project.getName());
    }

    private ProjectDAO fromDto(ProjectDAO.ProjectDAOBuilder builder, String name) {
        return builder.name(name).build();
    }
}

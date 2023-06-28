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
import com.costain.cdbb.core.events.ClientNotification;
import com.costain.cdbb.core.events.EventType;
import com.costain.cdbb.core.events.NotifyClientEvent;
import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.FirsDAO;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.Project;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectWithId;
import com.costain.cdbb.model.ProjectWithImportProjectIds;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides helper functions for managing and manipulating projects.
 */
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

    @Autowired
    OrganisationalObjectiveRepository organisationalObjectiveRepository;

    @Autowired
    AssetDataDictionaryRepository assetDataDictionaryRepository;

    @Autowired
    FunctionalOutputDataDictionaryRepository foDataDictionaryRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * Create a dto from a priject dao.
     * @param dao the source data
     * @return ProjectWithId the created dto
     */
    public ProjectWithId fromDao(ProjectDAO dao) {
        ProjectWithId dto = new ProjectWithId();
        dto.id(dao.getId());
        dto.name(dao.getName());
        dto.foDdId(dao.getFoDataDictionary() == null ? null : dao.getFoDataDictionary().getId());
        dto.assetDdId(dao.getAssetDataDictionary() == null ? null : dao.getAssetDataDictionary().getId());
        return dto;
    }

    /**
     * Adds all organisational objectives as Project Organisational Objectives to a project.
     * @param projectDao The projectDAO the POOs are to be added to
     */
    public void addAllOrganisationObjectivesToProject(ProjectDAO projectDao) {
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
    }

    /**
     * Create a project from a dto.
     * @param project the dto
     * @return ProjectDAO the created project dao
     */
    /*public ProjectDAO createProject(Project project) {
        checkProjectNameIsUnique(null, project.getName());
        ProjectDAO projectDao = repository.save(ProjectDAO.builder()
            .name(project.getName())
            .assetDataDictionary(assetDataDictionaryRepository.findById(project.getAssetDdId()).orElse(null))
            .foDataDictionary(foDataDictionaryRepository.findById(project.getFoDdId()).orElse(null))
            .build());
        addAllOrganisationObjctivesToproject(projectDao);
        repository.save(projectDao);
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ADDED, null, projectDao.getId())));
        return projectDao;
    }*/

    /**
     * Delete a project.
     * @param id the id of the project to be deleted
     */
    public void deleteProjectById(UUID id) {
        repository.deleteById(id);
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.PROJECT_DELETED, null, id)));
    }

    /**
     * Create dto from project dao.
     * @param dao the project dao
     * @return Project dto
     */
    public Project projectFromDao(ProjectDAO dao) {
        Project dto = new Project();
        dto.name(dao.getName());
        dto.foDdId(dao.getFoDataDictionary().getId());
        dto.assetDdId(dao.getAssetDataDictionary().getId());
        return dto;
    }

    /**
     * Create project dao from dto and data dictionaries.
     * @param project project dto without data dictionaries
     * @param assetDataDictionaryDao asset data dictionary dao for project
     * @param functionalOutputDataDictionaryDao functional output data dictionary for project
     * @return ProjectDAO
     */
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

    /**
     * Create project dao from dto and data dictionaries and
     * optional imported fos and/or assets from other projects.
     * @param project ProjectWithImportProjectIds dto with data dictionaries
     *                and optional imported fo/assets
     * @return ProjectDAO
     */
    public ProjectDAO ddsFromDto(ProjectWithImportProjectIds project) {
        Optional<AssetDataDictionaryDAO> assetDataDictionaryDao =
            assetDataDictionaryRepository.findById(project.getAssetDdId());
        Optional<FunctionalOutputDataDictionaryDAO> functionalOutputDataDictionaryDao =
            foDataDictionaryRepository.findById(project.getFoDdId());

        checkProjectNameIsUnique(null, project.getName());

        ProjectDAO projectDao = ProjectDAO.builder()
            .name(project.getName())
            .assetDataDictionary(assetDataDictionaryDao.orElse(null))
            .foDataDictionary(functionalOutputDataDictionaryDao.orElse(null))
            .build();
        projectDao = this.addPoos(projectDao);

        // must add assets first so they exist when links are required for same import file
        if (null != project.getImportAirsProjectId()) {
            ProjectDAO airsProjectDao = repository.findById(project.getImportAirsProjectId()).get();
            if (airsProjectDao.getAssetDataDictionary().equals(assetDataDictionaryDao.get())) {
                // import assets and airs from other airsProject
                Set<AssetDAO> assets = assetRepository.findByProjectId(airsProjectDao.getId());
                saveAssets(projectDao, assets);
            }
        }
        boolean importProjectsTheSame = false;
        if (null != project.getImportFirsProjectId()) {
            ProjectDAO firsProjectDao = repository.findById(project.getImportFirsProjectId()).get();
            importProjectsTheSame = null != project.getImportAirsProjectId()
                && project.getImportFirsProjectId().equals(project.getImportAirsProjectId());
            if (firsProjectDao.getFoDataDictionary().equals(functionalOutputDataDictionaryDao.get())) {
                // import fos and firs from other firsProject, exclude assets if AirsProjectId not same as FirsProjectId
                Set<FunctionalOutputDAO> fos = foRepository.findByProjectId(firsProjectDao.getId());
                saveFos(projectDao, fos, importProjectsTheSame);
            }
        }
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ADDED, null, projectDao.getId())));
        return projectDao;
    }

    private ProjectDAO addPoos(ProjectDAO dao) {
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
        return repository.save(dao);
    }

    /**
     * Rename a project.
     * @param jsonName json string with key "name" for new name of project
     * @param projectId id of project to rename
     * @return ProjectDAO renamed project dao
     * @throws JSONException exception in parsing json
     */
    public ProjectDAO renameProject(String jsonName, UUID projectId) throws JSONException {
        final String keyName = "name";
        ProjectDAO project = repository.findById(projectId).orElse(null);
        JSONObject jsonObject = new JSONObject(jsonName);

        if (project != null && jsonObject.has(keyName)) {
            String newName = jsonObject.getString(keyName);
            checkProjectNameIsUnique(project, newName);
            project.setName(newName);
            applicationEventPublisher.publishEvent(
                new NotifyClientEvent(new ClientNotification(EventType.PROJECT_RENAMED, null, project.getId())));
        }
        return project;
    }

    /**
     * Checks if a project exists with a given name, throws CdbbValidationError if so.
     * @param projectWithThisName Project, if not null, is not included in matches
     * @param newName Name to be checked
     */
    public void checkProjectNameIsUnique(ProjectDAO projectWithThisName, String newName) {
        List<ProjectDAO> matchingProjects = repository.findByName(newName);
        if (matchingProjects == null
            || matchingProjects.isEmpty()
            || projectWithThisName != null && projectWithThisName.getId().equals(matchingProjects.get(0).getId())) {
            return;
        }
        // this new name is already in use by another project so reject
        throw new CdbbValidationError("A project named '" + newName + "' already exists");
    }

    private  Set<ProjectOrganisationalObjectiveDAO> copyPooLinkedEntities(
        ProjectDAO sourceProject, ProjectDAO destinationProject) {
        return
            sourceProject.getProjectOrganisationalObjectiveDaos().stream().map(
                dao -> pooRepository.save(ProjectOrganisationalObjectiveDAO.builder()
                    .projectId(destinationProject.getId())
                    .ooVersion(dao.getOoVersion())
                    .frs(saveFrs(destinationProject, dao.getFrs()))
                    .build())).collect(Collectors.toSet());
    }

    private Set<FunctionalRequirementDAO> saveFrs(
        ProjectDAO destinationProject, Set<FunctionalRequirementDAO> sourceFrDaos) {
        // do not save new frs with same name as existing
        Set<FunctionalRequirementDAO> existingFrs =
            frRepository.findByProjectIdOrderByNameAsc(destinationProject.getId());
        return sourceFrDaos.stream().map(daoFr -> {
            List<FunctionalRequirementDAO> existingFr = existingFrs.stream().filter(
                frDao -> frDao.getName().equals(daoFr.getName())).toList();
            return
                frRepository.save(FunctionalRequirementDAO.builder()
                .id(existingFr.size() == 0 ? null : existingFr.get(0).getId())
                .projectId(destinationProject.getId())
                .name(daoFr.getName())
                .fos(saveFos(destinationProject, daoFr.getFos(), true))
                .build());
        }).collect(Collectors.toSet());
    }

    private Set<FunctionalOutputDAO> saveFos(ProjectDAO destinationProject,
                                             Set<FunctionalOutputDAO> sourceFoDaos,
                                             boolean includeAssetsInsave) {
        // do not save new fos with same data dictionary entry as existing
        // For copying a project/importing into new project
        // if there are any existing firs it's because of multiple links
        // e.g. from multiple POOs to a single fr - in this case just return the existing firs
        // Importing is not done in this method
        Set<FunctionalOutputDAO> existingFos = foRepository.findByProjectId(destinationProject.getId());
        return sourceFoDaos.stream().map(sourceFoDao -> {
            FunctionalOutputDAO existingFo = null;
            for (FunctionalOutputDAO foDao : existingFos) {
                if (foDao.getDataDictionaryEntry().getId()
                        .equals(sourceFoDao.getDataDictionaryEntry().getId())) {
                    existingFo = foDao;
                    break;
                }
            }

            if (null != existingFo) {
                return existingFo;
            }
            FunctionalOutputDAO fo = FunctionalOutputDAO.builder()
                .projectId(destinationProject.getId())
                .dataDictionaryEntry(sourceFoDao.getDataDictionaryEntry())
                .firs(this.copyFirsDaos(existingFo, sourceFoDao.getFirs()))
                .assets(includeAssetsInsave
                    ? this.saveAssets(destinationProject, sourceFoDao.getAssets()) : null)
                .build();
            return foRepository.save(fo);
        }).collect(Collectors.toSet());
    }

    private Set<AssetDAO> saveAssets(
        ProjectDAO destinationProject, Set<AssetDAO> sourceAssetDaos) {
        // do not save new assets with same data dictionary entry as existing
        // For copying a project/importing into new project
        // if there are any existing airs it's because of multiple links
        // e.g. from multiple POOs to a single asset - in this case just return the existing airs
        // Importing is not done in this method
        Set<AssetDAO> existingAssets = assetRepository.findByProjectId(destinationProject.getId());
        return sourceAssetDaos.stream().map(sourceAssetDao -> {
            AssetDAO existingAsset = null;
            for (AssetDAO existingAssetDao : existingAssets) {
                if (Objects.equals(existingAssetDao.getDataDictionaryEntry().getId(),
                    sourceAssetDao.getDataDictionaryEntry().getId())) {
                    existingAsset = existingAssetDao;
                    break;
                }
            }
            if (null != existingAsset) {
                // existing asset
                return existingAsset;
            }
            AssetDAO asset = AssetDAO.builder()
                .projectId(destinationProject.getId())
                .airs(this.copyAirsDaos(existingAsset, sourceAssetDao.getAirs()))
                .dataDictionaryEntry(sourceAssetDao.getDataDictionaryEntry())
                .build();
            return assetRepository.save(asset);
        }).collect(Collectors.toSet());
    }

    private Set<FirsDAO> copyFirsDaos(FunctionalOutputDAO existingFoDaoOrNull, Set<FirsDAO> sourceFirs) {
        Set<FirsDAO> firsCopy = new HashSet<>(sourceFirs.size());
        for (FirsDAO firsDao : sourceFirs) {
            firsCopy.add(FirsDAO.builder()
                .foDao(existingFoDaoOrNull)
                .firs(firsDao.getFirs())
                .build());
        }
        return firsCopy;
    }

    private Set<AirsDAO> copyAirsDaos(AssetDAO existingAssetDaoOrNull, Set<AirsDAO> sourceAirs) {
        Set<AirsDAO> airsCopy = new HashSet<>(sourceAirs.size());
        for (AirsDAO airsDao : sourceAirs) {
            Set<OirDAO> oirsDaos = new HashSet<>(airsDao.getOirs());
            AirsDAO copyAirsDao = AirsDAO.builder()
                .id(AirsDAO.NEW_ID)
                .assetDao(existingAssetDaoOrNull)
                .airs(airsDao.getAirs())
                .oirs(oirsDaos)
                .build();
            oirsDaos.stream().forEach(oirDao -> oirDao.getAirs().add(copyAirsDao));
            airsCopy.add(copyAirsDao);
        }
        return airsCopy;
    }

    public Set<FunctionalRequirementDAO> findUnlinkedSourceFunctionalRequirements(
        ProjectDAO sourceProject, Set<ProjectOrganisationalObjectiveDAO> savedPooDaos) {
        Set<FunctionalRequirementDAO> sourceFrs = frRepository.findByProjectIdOrderByNameAsc(sourceProject.getId());
        sourceFrs.removeIf(unlinkedFr ->
            savedPooDaos.stream()
                .anyMatch(pooDao -> pooDao.getFrs().stream()
                .anyMatch(pooFr -> pooFr.getName().equals(unlinkedFr.getName()))));
        return sourceFrs;
    }

    Set<FunctionalOutputDAO> findUnlinkedSourceFunctionalOutputs(
        ProjectDAO sourceProject, Set<FunctionalRequirementDAO> savedFrDaos) {
        Set<FunctionalOutputDAO> unlinkedFos = foRepository.findByProjectId(sourceProject.getId());
        unlinkedFos.removeIf(unlinkedFo ->
            savedFrDaos.stream()
                    .anyMatch(frDao -> frDao.getFos().stream()
                    .anyMatch(foDao -> foDao.getId().equals(unlinkedFo.getId()))));
        return unlinkedFos;
    }

    Set<AssetDAO> findUnlinkedSourceAssets(
        ProjectDAO sourceProject, Set<FunctionalOutputDAO> savedFoDaos) {
        Set<AssetDAO> unlinkedAssets = assetRepository.findByProjectId(sourceProject.getId());
        unlinkedAssets.removeIf(unlinkedAsset ->
            savedFoDaos.stream()
                .anyMatch(foDao -> foDao.getAssets().stream()
                    .anyMatch(assetDao -> foDao.getId().equals(unlinkedAsset.getId()))));
        return unlinkedAssets;
    }

    /**
     * Copy a project to a new project.
     * @param jsonName Name of new project (key="name")
     * @param sourceProjectId the project id of the project to be copied
     * @return ProjectWithId
     * @throws JSONException exception in parsing json
     */
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
            Set<FunctionalOutputDAO> fos = saveFos(copiedProject, unlinkedFos, true);

            Set<AssetDAO> unlinkedAssets = findUnlinkedSourceAssets(sourceProject, fos);
            saveAssets(copiedProject, unlinkedAssets);
            applicationEventPublisher.publishEvent(
                new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ADDED, null, copiedProject.getId())));
            return fromDao(copiedProject);
        }
        throw new CdbbValidationError("Copy project: source not found - " + sourceProjectId);
    }

    /**
     * Create a new project from a dto.
     * @param project the dto
     * @return ProjectDAOthe new dao
     */
    public ProjectDAO fromDto(Project project) {
        return fromDto(ProjectDAO.builder(), project.getName());
    }

    /**
     * Update a project from a dto.
     * @param projectId the project to be updated
     * @param project the dto
     * @return
     */
    public ProjectDAO fromDto(UUID projectId, Project project) {
        return fromDto(ProjectDAO.builder().id(projectId), project.getName());
    }

    private ProjectDAO fromDto(ProjectDAO.ProjectDAOBuilder builder, String name) {
        return builder.name(name).build();
    }
}

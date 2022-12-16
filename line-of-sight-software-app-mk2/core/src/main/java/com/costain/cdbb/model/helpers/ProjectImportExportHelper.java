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
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.OoVersionDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.OoVersionRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



/**
 * Provides helper functions for managing project imports and exports.
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProjectImportExportHelper {
    private static final Logger logger = LogManager.getLogger();

    private static final String EXPORT_VERSION_KEY = "export_version";
    //private static final String EXPORT_VERSION_1_0_0 = "1.0.0"; Problems with dde ids made this version unviable
    private static final String EXPORT_VERSION_1_0_1 = "1.0.1";
    private static final String CURRENT_EXPORT_VERSION = EXPORT_VERSION_1_0_1;
    private static final String PROJECT_KEY = "project";
    private static final String FO_DD_KEY = "fo_dd";
    private static final String ASSET_DD_KEY = "asset_dd";
    private static final String PROJECT_NAME_KEY = "project_name";
    private static final String DD_ENTRIES_KEY = "dd_entries";
    private static final String ASSET_DD_ID_KEY = "asset_dd_id";
    private static final String ASSET_DD_NAME_KEY = "asset_dd_name";
    private static final String FO_DD_ID_KEY = "fo_dd_id";
    private static final String FO_DD_NAME_KEY = "fo_dd_name";
    private static final String ID_KEY = "id";
    private static final String ENTRY_ID_KEY = "entry_id";
    private static final String NAME_KEY = "name";
    private static final String PROJECT_ID_KEY = "projectId";
    private static final String POOS_KEY = "poos";
    private static final String OO_VERSION_KEY = "ooVersion";
    private static final String DATE_CREATED_KEY = "dateCreated";
    private static final String DELETED_KEY = "deleted";
    private static final String OO_KEY = "oo";
    private static final String OIRS_KEY = "oirs";
    private static final String OIR_KEY = "oir";
    private static final String FRS_KEY = "frs";
    private static final String FOS_KEY = "fos";
    private static final String FIRS_KEY = "firs";
    private static final String AIRS_KEY = "airs";
    private static final String FO_DDE_KEY = "foDde";
    private static final String ASSET_DDE_KEY = "assetDde";
    private static final String ASSETS_KEY = "assets";
    private static final String TEXT_KEY = "text";


    private ProjectDAO sourceProjectDao;
    private String errorMsg;
    private AssetDataDictionaryDAO importAssetDataDictionaryDao;
    private FunctionalOutputDataDictionaryDAO importFoDataDictionaryDao;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    OoVersionRepository ooVersionRepository;

    @Autowired
    FunctionalRequirementRepository frRepository;

    @Autowired
    FunctionalOutputRepository foRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    AssetDataDictionaryRepository assetDdRepository;

    @Autowired
    AssetDataDictionaryEntryRepository assetDdeRepository;

    @Autowired
    FunctionalOutputDataDictionaryRepository foDdRepository;

    @Autowired
    FunctionalOutputDataDictionaryEntryRepository foDdeRepository;

    @Autowired
    ProjectHelper projectHelper;

    @Autowired
    CompressionHelper compressionHelper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * <p>Import a project from a previously exported project file.  The project name must not exists.
     * The client may modify the project name before upload to meet user naming requirement.</p>
     * @param base64CompressedProjectData base64 compressed project import file in json format
     * @return ProjectDAO the project dao
     */
    public ProjectDAO importProjectFile(String base64CompressedProjectData) {
        String importedData = null;
        try {
            importedData = compressionHelper.decompress(Base64.getDecoder().decode(base64CompressedProjectData));
            try {
                JSONObject importJsonObject = new JSONObject(importedData);
                String version = importJsonObject.getString(EXPORT_VERSION_KEY);
                if (EXPORT_VERSION_1_0_1.equals(version)) { // add any version supported by importVersion1
                    ProjectDAO importDao = this.importVersion1(importJsonObject);
                    applicationEventPublisher.publishEvent(
                        new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ADDED,
                            null, importDao.getId())));
                    return importDao;
                }
                this.errorMsg = "Unsupported version:" + version;
            } catch (JSONException e) {
                this.errorMsg = "file is not valid JSON format: " + e;
            } catch (Exception e1) {
                this.errorMsg = "- error found: " + e1;
            }
        } catch (IOException e) {
            this.errorMsg = "failed to decompress";
        }
        logger.error("Import project: " + this.errorMsg);
        throw new CdbbValidationError("Project import file: " + this.errorMsg);
    }

    /**
     * Export a project to a json format string suitable for importing to this or another system.
     * The export will contain the original project name and an 'export version id' to allow for future changes
     * in project structure or method of export
     * The export will be in base64 encoded compressed json form
     * This method will always be for the CURRENT_EXPORT_VERSION only     *
     * @param sourceProjectId  the project id of the project to be exported
     * @return String compressed and base-64 encoded JSON formatted, self-contained exported project.
     */
    public String exportProject(UUID sourceProjectId)  {
        // The methodology of hierarchical data extraction is similar to that of copying a project
        this.sourceProjectDao = projectRepository.findById(sourceProjectId).orElse(null);
        if (this.sourceProjectDao != null) {
            try {
                Map<String, Object> exportMap = new LinkedHashMap<>();
                exportMap.put(EXPORT_VERSION_KEY, CURRENT_EXPORT_VERSION);
                exportMap.put(PROJECT_KEY, getProjectMap());
                exportMap.put(FO_DD_KEY, getFoDdMap());
                exportMap.put(ASSET_DD_KEY, getAssetDdMap());
                String payload = new ObjectMapper().writeValueAsString(exportMap);
                return Base64.getEncoder().encodeToString(
                    compressionHelper.compress(payload));
            } catch (IOException e) {
                errorMsg = "failed to compress data";
            }
        } else {
            errorMsg = "source not found - " + sourceProjectId;
        }
        throw new CdbbValidationError("Export project:"  + errorMsg);
    }

    // Import methods
    private ProjectDAO importVersion1(JSONObject importJsonObject) throws JSONException {
        // this may support multiple versions depending on how different they are to the original V1.0.0
        this.errorMsg = null;
        this.importFoDataDictionaryDao = importFoDataDictionaryIfRequired(importJsonObject.getJSONObject(FO_DD_KEY));
        this.importAssetDataDictionaryDao =
            importAssetDataDictionaryIfRequired(importJsonObject.getJSONObject(ASSET_DD_KEY));
        return importProject(importJsonObject.getJSONObject(PROJECT_KEY),
            thisOrganisationIsExportOrganisation(importJsonObject));
    }

    private ProjectDAO importProject(JSONObject projectJsonObject, boolean sameOrganisation) throws JSONException {
        String projectName = projectJsonObject.getString(PROJECT_NAME_KEY);
        projectHelper.checkProjectNameIsUnique(null, projectName);
        this.sourceProjectDao = ProjectDAO.builder().name(projectName)
            .foDataDictionary(this.importFoDataDictionaryDao)
            .assetDataDictionary(this.importAssetDataDictionaryDao)
            .build();

        this.sourceProjectDao = projectRepository.save(this.sourceProjectDao);
        // for import, unless for the same organisation we do not want the POOs
        if (sameOrganisation) {
            this.sourceProjectDao.setProjectOrganisationalObjectiveDaos(
                this.importPoos(projectJsonObject.getJSONArray(POOS_KEY)));
        } else {
            // replace the original POOs with the ones from this organisation and import the linked FRs
            projectHelper.addAllOrganisationObjctivesToproject(this.sourceProjectDao);
            this.sourceProjectDao.setFunctionRequirementDaos(
                this.importFrsFromPoos(projectJsonObject.getJSONArray(POOS_KEY)));
        }
        this.importUnlinkedFunctionalRequirements(projectJsonObject);
        this.importUnlinkedFunctionalOutputs(projectJsonObject);
        this.importUnlinkedAssets(projectJsonObject);
        this.sourceProjectDao = projectRepository.save(this.sourceProjectDao);
        this.sourceProjectDao = projectRepository.findById(this.sourceProjectDao.getId()).get();
        return this.sourceProjectDao;
    }

    @Nullable
    private FunctionalOutputDataDictionaryDAO importFoDataDictionaryIfRequired(
        @NotNull JSONObject foDdJsonObject) throws JSONException {
        // if name matches an existing - use it
        // if not, is there a dd for which the import is exact or sub-set - if so use it
        // If neither of above, import complete dictionary
        FunctionalOutputDataDictionaryDAO foDao = foDdRepository.findByName(foDdJsonObject.getString(FO_DD_NAME_KEY));
        if (null != foDao) {
            logger.debug("Import project FO DD - name matched - " + foDdJsonObject.getString(FO_DD_NAME_KEY));
            return foDao;
        }
        // no exact match - look for first subset (tricky) - use ids to match
        foDao = findFoDdWithMatchingEntries(foDdJsonObject);
        if (null != foDao) {
            logger.debug("Import project FO DD - (sub)set found " + foDdJsonObject.getString(FO_DD_NAME_KEY));
            return foDao;
        }
        return this.createFoDataDictionary(foDdJsonObject);
    }

    @Nullable
    private FunctionalOutputDataDictionaryDAO findFoDdWithMatchingEntries(@NotNull JSONObject foDdJsonObject)
        throws JSONException {

        JSONArray importingEntriesJsonArray = foDdJsonObject.getJSONArray(DD_ENTRIES_KEY);
        if (importingEntriesJsonArray.length() == 0) {
            throw new CdbbValidationError("Imported FO data dictionary is empty");
        }
        JSONObject importingEntryJsonObject = importingEntriesJsonArray.getJSONObject(0);
        Set<FunctionalOutputDataDictionaryEntryDAO> matchingDdes
            = foDdeRepository.findByEntryId(importingEntryJsonObject.getString(ENTRY_ID_KEY));
        // Given that entryId may not be unique there may be multiple matching dictionaries found,
        // for each see if rest of entries are in it
        if (matchingDdes.isEmpty()) {
            return null;
        }
        for (FunctionalOutputDataDictionaryEntryDAO matchedEntryDao : matchingDdes) {
            // get dictionary for the matched entry and see if all entries from imported project are in it
            UUID potentialDictionaryId = matchedEntryDao.getFoDictionaryId();
            // try to match rest of ddes from import file
            Set<FunctionalOutputDataDictionaryEntryDAO> entriesFromPotentialDictionary =
                foDdeRepository.findByFoDictionaryIdOrderByEntryId(potentialDictionaryId);
            for (int i = 1; i < importingEntriesJsonArray.length(); i++) {
                // loop through each imported entry to see if there is a match in the current potential dictionary
                importingEntryJsonObject = importingEntriesJsonArray.getJSONObject(i);
                // find match in order to continue loop
                boolean matched = false;
                for (FunctionalOutputDataDictionaryEntryDAO entryDao : entriesFromPotentialDictionary) {
                    if (importingEntryJsonObject.getString(ENTRY_ID_KEY).equals(entryDao.getEntryId())) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    potentialDictionaryId = null;
                    break;
                }
            }
            if (null != potentialDictionaryId) {
                // use potentialDictionaryId as all imported entries present in it
                return foDdRepository.findById(potentialDictionaryId).get();
            }
        }
        return null;
    }

    private FunctionalOutputDataDictionaryDAO createFoDataDictionary(@NotNull JSONObject foDdJsonObject)
        throws JSONException {
        // Create FO Dictionary and import all dd entries
        FunctionalOutputDataDictionaryDAO foDao = foDdRepository.save(FunctionalOutputDataDictionaryDAO
            .builder()
            .name(foDdJsonObject.getString(FO_DD_NAME_KEY))
            .build());
        JSONArray entriesJsonArray = foDdJsonObject.getJSONArray(DD_ENTRIES_KEY);
        for (int i = 0; i < entriesJsonArray.length(); i++) {
            JSONObject entryJsonObject = entriesJsonArray.getJSONObject(i);
            foDdeRepository.save(FunctionalOutputDataDictionaryEntryDAO
                .builder()
                .entryId(entryJsonObject.getString(ENTRY_ID_KEY))
                .text(entryJsonObject.getString(TEXT_KEY))
                .foDictionaryId(foDao.getId())
                .build());
        }
        logger.debug("Created new FO dd " + foDdJsonObject.getString(FO_DD_NAME_KEY));
        return foDao;
    }

    @Nullable
    private AssetDataDictionaryDAO importAssetDataDictionaryIfRequired(@NotNull JSONObject assetDdJsonObject)
        throws JSONException {
        // if name matches an existing - use it
        // if not, is there a dd for which the import is exact or sub-set - if so use it
        // If neither of above, import complete dictionary
        AssetDataDictionaryDAO assetDao =
            assetDdRepository.findByName(assetDdJsonObject.getString(ASSET_DD_NAME_KEY));
        if (null != assetDao) {
            return assetDao;
        }
        // no exact match - look for first subset (tricky) - use ids to match
        assetDao = findAssetDdWithMatchingEntries(assetDdJsonObject);
        if (null != assetDao) {
            return assetDao;
        }
        return this.createAssetDataDictionary(assetDdJsonObject);
    }

    @Nullable
    private AssetDataDictionaryDAO findAssetDdWithMatchingEntries(@NotNull JSONObject assetDdJsonObject)
        throws JSONException {

        JSONArray importingEntriesJsonArray = assetDdJsonObject.getJSONArray(DD_ENTRIES_KEY);
        if (importingEntriesJsonArray.length() == 0) {
            throw new CdbbValidationError("Imported asset data dictionary is empty");
        }
        JSONObject importingEntryJsonObject = importingEntriesJsonArray.getJSONObject(0);
        Set<AssetDataDictionaryEntryDAO> matchingDdes
            = assetDdeRepository.findByEntryId(importingEntryJsonObject.getString(ENTRY_ID_KEY));
        // Given that entryId may not be unique there may be multiple matching dictionaries found,
        // for each see if rest of entries are in it
        if (matchingDdes.isEmpty()) {
            return null;
        }
        for (AssetDataDictionaryEntryDAO matchedEntryDao : matchingDdes) {
            // get dictionary for the matched entry and see if all entries from imported project are in it
            UUID potentialDictionaryId = matchedEntryDao.getAssetDictionaryId();
            // try to match rest of ddes from import file
            Set<AssetDataDictionaryEntryDAO> entriesFromPotentialDictionary =
                assetDdeRepository.findByAssetDictionaryIdOrderByEntryId(potentialDictionaryId);
            for (int i = 1; i < importingEntriesJsonArray.length(); i++) {
                // loop through each imported entry to see if there is a match in the current potential dictionary
                importingEntryJsonObject = importingEntriesJsonArray.getJSONObject(i);
                // find match in order to continue loop
                boolean matched = false;
                for (AssetDataDictionaryEntryDAO entryDao : entriesFromPotentialDictionary) {
                    if (importingEntryJsonObject.getString(ENTRY_ID_KEY).equals(entryDao.getEntryId())) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    potentialDictionaryId = null;
                    break;
                }
            }
            if (null != potentialDictionaryId) {
                // use potentialDictionaryId as all imported entries present in it
                return assetDdRepository.findById(potentialDictionaryId).get();
            }
        }
        return null;
    }

    private AssetDataDictionaryDAO createAssetDataDictionary(JSONObject assetDdJsonObject) throws JSONException {
        // Create Asset Data Dictionary and import all dd entries
        AssetDataDictionaryDAO assetDao = assetDdRepository.save(AssetDataDictionaryDAO
            .builder()
            .name(assetDdJsonObject.getString(ASSET_DD_NAME_KEY))
            .build());
        JSONArray entriesJsonArray = assetDdJsonObject.getJSONArray(DD_ENTRIES_KEY);
        for (int i = 0; i < entriesJsonArray.length(); i++) {
            JSONObject entryJsonObject = entriesJsonArray.getJSONObject(i);
            assetDdeRepository.save(AssetDataDictionaryEntryDAO
                .builder()
                .entryId(entryJsonObject.getString(ENTRY_ID_KEY))
                .text(entryJsonObject.getString(TEXT_KEY))
                .assetDictionaryId(assetDao.getId())
                .build());
        }
        return assetDao;
    }

    private boolean thisOrganisationIsExportOrganisation(JSONObject importJsonObject) throws JSONException {
        // if the ids of both ORIGINAL data dictionaries exist assume same organisation
        return null != foDdRepository.findById(
            UUID.fromString(importJsonObject.getJSONObject(FO_DD_KEY).getString(FO_DD_ID_KEY))).orElse(null)
            && null != assetDdRepository.findById(
            UUID.fromString(importJsonObject.getJSONObject(ASSET_DD_KEY).getString(ASSET_DD_ID_KEY))).orElse(null);
    }

    private  Set<ProjectOrganisationalObjectiveDAO> importPoos(JSONArray poosJson) throws JSONException {
        Set<ProjectOrganisationalObjectiveDAO> poos = new HashSet<>(poosJson.length());
        for (int i = 0; i < poosJson.length(); i++) {
            JSONObject pooJson = poosJson.getJSONObject(i);
            poos.add(pooRepository.save(ProjectOrganisationalObjectiveDAO.builder()
                .projectId(this.sourceProjectDao.getId())
                .ooVersion(this.importOoVersion(pooJson.getJSONObject(OO_VERSION_KEY)))
                .frs(this.importFrsList(pooJson.getJSONArray(FRS_KEY)))
                .build()));
        }
        return poos;
    }

    private OoVersionDAO importOoVersion(JSONObject ooVersionJson) throws JSONException {
        return ooVersionRepository.findById(UUID.fromString(ooVersionJson.getString(ID_KEY))).get();
    }

    private Set<FunctionalRequirementDAO> importFrsFromPoos(JSONArray poosJson)  throws JSONException {
        Set<FunctionalRequirementDAO> frs = new HashSet<>();
        for (int i = 0; i < poosJson.length(); i++) {
            JSONObject  pooJson = poosJson.getJSONObject(i);
            frs.addAll(this.importFrsList(pooJson.getJSONArray(FRS_KEY)));
        }
        return frs;
    }

    private  Set<FunctionalRequirementDAO> importFrsList(JSONArray frsJson) throws JSONException {
        Set<FunctionalRequirementDAO> frs = new HashSet<>(frsJson.length());
        for (int i = 0; i < frsJson.length(); i++) {
            JSONObject frJson = frsJson.getJSONObject(i);
            FunctionalRequirementDAO fr = frRepository.findByProjectIdAndName(
                this.sourceProjectDao.getId(), frJson.getString(NAME_KEY));
            if (fr == null) {
                fr = frRepository.save(FunctionalRequirementDAO.builder()
                    .projectId(this.sourceProjectDao.getId())
                    .name(frJson.getString(NAME_KEY))
                    .fos(this.importFosList(frJson.getJSONArray(FOS_KEY)))
                    .build());
            }
            frs.add(fr);
        }
        return frs;
    }

    private  Set<FunctionalOutputDAO> importFosList(JSONArray fosJson) throws JSONException {
        Set<FunctionalOutputDAO> fos = new HashSet<>(fosJson.length());
        for (int i = 0; i < fosJson.length(); i++) {
            JSONObject foJson = fosJson.getJSONObject(i);
            FunctionalOutputDataDictionaryEntryDAO foDdeDao = this.getFoDde(foJson.getJSONObject(FO_DDE_KEY));
            // see if already created
            FunctionalOutputDAO fo = foRepository.findByProjectIdAndDataDictionaryEntry_EntryId(
                this.sourceProjectDao.getId(), foDdeDao.getEntryId());
            if (fo == null) {
                fo = foRepository.save(FunctionalOutputDAO.builder()
                    .projectId(this.sourceProjectDao.getId())
                    .firs(this.importStringList(foJson.getJSONArray(FIRS_KEY)))
                    .assets(this.importAssetsList(foJson.getJSONArray(ASSETS_KEY)))
                    .dataDictionaryEntry(foDdeDao)
                    .build());
            }
            fos.add(fo);
        }
        return fos;
    }

    private FunctionalOutputDataDictionaryEntryDAO getFoDde(JSONObject foDdeJson) throws JSONException {
        return foDdeRepository.findByFoDictionaryIdAndEntryId(
            this.importFoDataDictionaryDao.getId(), foDdeJson.getString(ENTRY_ID_KEY));
    }

    private Set<AssetDAO> importAssetsList(JSONArray assetsJson) throws JSONException {
        Set<AssetDAO> assets = new HashSet<>(assetsJson.length());
        for (int i = 0; i < assetsJson.length(); i++) {
            JSONObject assetJson = assetsJson.getJSONObject(i);
            AssetDataDictionaryEntryDAO assetDdeDao = this.getAssetDde(assetJson.getJSONObject(ASSET_DDE_KEY));
            AssetDAO asset = assetRepository.findByProjectIdAndDataDictionaryEntry_EntryId(
                this.sourceProjectDao.getId(), assetDdeDao.getEntryId());
            if (asset == null) {
                asset = assetRepository.save(AssetDAO.builder()
                    .projectId(this.sourceProjectDao.getId())
                    .airs(this.importStringList(assetJson.getJSONArray(AIRS_KEY)))
                    .dataDictionaryEntry(assetDdeDao)
                    .build());
            }
            assets.add(asset);
        }
        return assets;
    }

    private AssetDataDictionaryEntryDAO getAssetDde(JSONObject assetDdeJson) throws JSONException {
        return assetDdeRepository.findByAssetDictionaryIdAndEntryId(
            this.importAssetDataDictionaryDao.getId(), assetDdeJson.getString(ENTRY_ID_KEY));
    }

    private Set<String> importStringList(JSONArray stringsJson) throws JSONException {
        Set<String> strings = new HashSet<>(stringsJson.length());
        for (int i = 0; i < stringsJson.length(); i++) {
            strings.add(stringsJson.getString(i));
        }
        return strings;
    }


    private void importUnlinkedFunctionalRequirements(JSONObject projectJsonObject) throws JSONException {
        Set<FunctionalRequirementDAO> frDaos = this.importFrsList(projectJsonObject.getJSONArray(FRS_KEY));
        if (!frDaos.isEmpty()) {
            if (null != this.sourceProjectDao.getFunctionRequirementDaos()) {
                frDaos.addAll(this.sourceProjectDao.getFunctionRequirementDaos());
            }
            this.sourceProjectDao.setFunctionRequirementDaos(frDaos);
        }
    }

    private void importUnlinkedFunctionalOutputs(JSONObject projectJsonObject) throws JSONException {
        Set<FunctionalOutputDAO> foDaos = this.importFosList(projectJsonObject.getJSONArray(FOS_KEY));
        if (!foDaos.isEmpty()) {
            for (FunctionalOutputDAO foDao : foDaos) {
                foRepository.save(foDao);
            }
        }
    }

    private void importUnlinkedAssets(JSONObject projectJsonObject) throws JSONException {
        Set<AssetDAO> assetDaos = this.importAssetsList(projectJsonObject.getJSONArray(ASSETS_KEY));
        if (!assetDaos.isEmpty()) {
            for (AssetDAO assetDao : assetDaos) {
                assetRepository.save(assetDao);
            }
        }
    }

    // Export methods

    private Map<String, Object> getProjectMap() {
        Map<String, Object> projectExportMap = new LinkedHashMap<>();
        projectExportMap.put(PROJECT_NAME_KEY, sourceProjectDao.getName());

        this.addPoosToExportMap(projectExportMap);
        this.addUnlinkedFrsToExportMap(projectExportMap);
        Set<FunctionalOutputDAO> unlinkedFos = this.addUnlinkedFosToExportMap(projectExportMap);
        this.addUnlinkedAssetsToExportMap(projectExportMap, unlinkedFos);

        return projectExportMap;
    }

    private void addUnlinkedFrsToExportMap(Map<String, Object> projectExportMap) {
        Set<FunctionalRequirementDAO> unlinkedFrs =
            projectHelper.findUnlinkedSourceFunctionalRequirements(
                this.sourceProjectDao, this.sourceProjectDao.getProjectOrganisationalObjectiveDaos());
        projectExportMap.put(FRS_KEY, this.exportFrsList(unlinkedFrs));
    }

    private Set<FunctionalOutputDAO> addUnlinkedFosToExportMap(Map<String, Object> projectExportMap) {
        Set<FunctionalOutputDAO> unlinkedFos = projectHelper.findUnlinkedSourceFunctionalOutputs(
            this.sourceProjectDao, this.sourceProjectDao.getFunctionRequirementDaos());
        projectExportMap.put(FOS_KEY, this.exportFosList(unlinkedFos));
        return unlinkedFos;
    }

    private void addUnlinkedAssetsToExportMap(
        Map<String, Object> projectExportMap, Set<FunctionalOutputDAO> unlinkedFos) {
        Set<AssetDAO> unlinkedAssets = projectHelper.findUnlinkedSourceAssets(this.sourceProjectDao, unlinkedFos);
        projectExportMap.put(ASSETS_KEY, this.exportAssetsList(unlinkedAssets));
    }

    private Map<String, Object> getFoDdMap() {
        Map<String, Object> foDdExportMap = new LinkedHashMap<>();
        foDdExportMap.put(FO_DD_NAME_KEY, sourceProjectDao.getFoDataDictionary().getName());
        foDdExportMap.put(FO_DD_ID_KEY, sourceProjectDao.getFoDataDictionary().getId().toString());
        Set<FunctionalOutputDataDictionaryEntryDAO> entries =
            foDdeRepository.findByFoDictionaryIdOrderByEntryId(sourceProjectDao.getFoDataDictionary().getId());
        List<Map<String, String>> entriesList = new ArrayList<>(entries.size());
        foDdExportMap.put(DD_ENTRIES_KEY, entriesList);
        for (FunctionalOutputDataDictionaryEntryDAO dao : entries) {
            entriesList.add(exportFoDde(dao));
        }
        return foDdExportMap;
    }

    private Map<String, Object> getAssetDdMap() {
        Map<String, Object> assetDdExportMap = new LinkedHashMap<>();
        assetDdExportMap.put(ASSET_DD_NAME_KEY, sourceProjectDao.getAssetDataDictionary().getName());
        assetDdExportMap.put(ASSET_DD_ID_KEY, sourceProjectDao.getAssetDataDictionary().getId().toString());
        Set<AssetDataDictionaryEntryDAO> entries =
            assetDdeRepository.findByAssetDictionaryIdOrderByEntryId(
                sourceProjectDao.getAssetDataDictionary().getId());
        List<Map<String, String>> entriesList = new ArrayList<>(entries.size());
        assetDdExportMap.put(DD_ENTRIES_KEY, entriesList);
        for (AssetDataDictionaryEntryDAO dao : entries) {
            entriesList.add(exportAssetDde(dao));
        }
        return assetDdExportMap;
    }

    private void addPoosToExportMap(Map<String, Object> projectExportMap) {
        // this code will always be for CURRENT_EXPORT_VERSION only
        // POOs are only saved for imports to the same organisation - they are meaningless for another one - tbc
        List<Map<String, Object>> poosList = new ArrayList<>(projectExportMap.size());
        for (ProjectOrganisationalObjectiveDAO pooDao : this.sourceProjectDao.getProjectOrganisationalObjectiveDaos()) {
            Map<String, Object> pooMap = new LinkedHashMap<>();
            pooMap.put(ID_KEY, pooDao.getId().toString());
            pooMap.put(PROJECT_ID_KEY, pooDao.getProjectId().toString());
            pooMap.put(OO_VERSION_KEY, this.exportOoVersionMap(pooDao.getOoVersion()));
            pooMap.put(FRS_KEY, this.exportFrsList(pooDao.getFrs()));
            poosList.add(pooMap);
        }
        projectExportMap.put(POOS_KEY, poosList);
    }

    private Map<String, Object> exportOoVersionMap(OoVersionDAO ooversionDao) {
        // this code will always be for CURRENT_EXPORT_VERSION only
        Map<String, Object> ooVersionMap = new LinkedHashMap<>();
        ooVersionMap.put(ID_KEY, ooversionDao.getId());
        ooVersionMap.put(DATE_CREATED_KEY, ooversionDao.getDateCreated().getTime());
        ooVersionMap.put(OO_KEY, this.exportOoMap(ooversionDao.getOo()));
        return ooVersionMap;
    }

    private Map<String, Object> exportOoMap(OrganisationalObjectiveDAO ooDao) {
        // this code will always be for CURRENT_EXPORT_VERSION only
        Map<String, Object> ooMap = new LinkedHashMap<>();
        ooMap.put(ID_KEY, ooDao.getId().toString());
        ooMap.put(NAME_KEY, ooDao.getName());
        ooMap.put(DELETED_KEY, ooDao.isDeleted());
        ooMap.put(OIRS_KEY, this.exportOirsList(ooDao.getOirDaos()));
        return ooMap;
    }

    private List<Map<String, Object>> exportOirsList(Collection<OirDAO> oirs) {
        // this code will always be for CURRENT_EXPORT_VERSION only
        List<Map<String, Object>> oirsList = new ArrayList<>(oirs.size());
        for (OirDAO oir : oirs) {
            Map<String, Object> oirMap = new LinkedHashMap<>();
            oirMap.put(ID_KEY, oir.getId());
            oirMap.put(OIR_KEY, oir.getOir());
            oirsList.add(oirMap);
        }
        return oirsList;
    }

    private List<Map<String, Object>> exportFrsList(Set<FunctionalRequirementDAO> frs) {
        // this code will always be for CURRENT_EXPORT_VERSION only
        List<Map<String, Object>> frsList = new ArrayList<>(frs.size());
        for (FunctionalRequirementDAO fr : frs) {
            Map<String, Object> frMap = new LinkedHashMap<>();
            frMap.put(ID_KEY, fr.getId());
            frMap.put(NAME_KEY, fr.getName());
            frMap.put(FOS_KEY, this.exportFosList(fr.getFos()));
            frsList.add(frMap);
        }
        return frsList;
    }

    private List<Map<String, Object>> exportFosList(Set<FunctionalOutputDAO> fos) {
        // this code will always be for CURRENT_EXPORT_VERSION only
        List<Map<String, Object>> fosList = new ArrayList<>(fos.size());
        for (FunctionalOutputDAO fo : fos) {
            Map<String, Object> foMap = new LinkedHashMap<>();
            foMap.put(ID_KEY, fo.getId());
            foMap.put(FIRS_KEY, this.exportStringList(fo.getFirs()));
            foMap.put(ASSETS_KEY, this.exportAssetsList(fo.getAssets()));
            foMap.put(FO_DDE_KEY, this.exportFoDde(fo.getDataDictionaryEntry()));
            fosList.add(foMap);
        }
        return fosList;
    }

    private List<String> exportStringList(Set<String> stringSet) {
        // this code will always be for CURRENT_EXPORT_VERSION only
        List<String> strList = new ArrayList<>(stringSet.size());
        strList.addAll(stringSet);
        return strList;
    }

    private List<Map<String, Object>> exportAssetsList(Set<AssetDAO> assets) {
        // this code will always be for CURRENT_EXPORT_VERSION only
        List<Map<String, Object>> assetsList = new ArrayList<>(assets.size());
        for (AssetDAO asset : assets) {
            Map<String, Object> assetMap = new LinkedHashMap<>();
            assetMap.put(ID_KEY, asset.getId());
            assetMap.put(AIRS_KEY, this.exportStringList(asset.getAirs()));
            assetMap.put(ASSET_DDE_KEY, this.exportAssetDde(asset.getDataDictionaryEntry()));
            assetsList.add(assetMap);
        }
        return assetsList;
    }

    private Map<String, String> exportAssetDde(AssetDataDictionaryEntryDAO assetDdeDao) {
        Map<String, String> assetDdeMap = new LinkedHashMap<>();
        assetDdeMap.put(ENTRY_ID_KEY, assetDdeDao.getEntryId());
        assetDdeMap.put(TEXT_KEY, assetDdeDao.getText());
        return assetDdeMap;
    }

    private Map<String, String> exportFoDde(FunctionalOutputDataDictionaryEntryDAO foDdeDao) {
        Map<String, String> foDdeMap = new LinkedHashMap<>();
        foDdeMap.put(ENTRY_ID_KEY, foDdeDao.getEntryId());
        foDdeMap.put(TEXT_KEY, foDdeDao.getText());
        return foDdeMap;
    }
}

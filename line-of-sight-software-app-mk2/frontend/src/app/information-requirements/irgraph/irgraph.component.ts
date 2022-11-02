import {AfterViewInit, Component, Inject, OnInit} from '@angular/core';
import {Asset} from '../../asset';
import {ProjectOrganisationalObjective} from '../../project-organisational-objective';
import {OrganisationalObjectiveVersion} from '../../organisational-objective-version';
import {FunctionalRequirement} from '../../functional-requirement';
import {FunctionalOutput} from '../../functional-output';
import {ModalDismissReasons, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AssetService} from "../../asset.service";
import {ProjectOrganisationalObjectiveService} from "../../project-organisational-objective.service";
import {FunctionalRequirementService} from "../../functional-requirement.service";
import {FunctionalOutputService} from "../../functional-output.service";
import {DOCUMENT} from "@angular/common";
import {AssetDataDictionaryEntryService} from "../../asset-data-dictionary-entry.service";
import {FunctionalOutputDataDictionaryEntryService} from "../../functional-output-data-dictionary-entry.service";
import {DataDictionaryEntry} from "../../data-dictionary-entry";
import {IMultiSelectSettings, IMultiSelectOption, IMultiSelectTexts} from "ngx-bootstrap-multiselect";
import { ProjectDataService } from 'src/app/project-data.service';
import { Project } from 'src/app/project';
import { Oir } from 'src/app/organisational-objective';

@Component({
    selector: 'app-irgraph',
    templateUrl: './irgraph.component.html',
    styleUrls: ['./irgraph.component.scss']
})
export class IRGraphComponent implements OnInit, AfterViewInit {

    project: Project = {
        id: '',
        name: '',
        fo_dd_id: '',
        asset_dd_id: ''
    };
    optionsModel: Array<number> = [];
    mySettings: IMultiSelectSettings = {buttonClasses: 'form-control element-text', enableSearch: true, dynamicTitleMaxItems: 0}
    pooFROptions: Array<IMultiSelectOption> = [];
    pooFRTexts: IMultiSelectTexts = {defaultTitle: 'Select FRs to link', searchEmptyResult: 'No FRs found ...'}
    frFOOptions: Array<IMultiSelectOption> = [];
    frFOTexts: IMultiSelectTexts = {defaultTitle: 'Select FOs to link', searchEmptyResult: 'No FOs found ...'}
    foAssetOptions: Array<IMultiSelectOption> = [];
    foAssetTexts: IMultiSelectTexts = {defaultTitle: 'Select Assets to link', searchEmptyResult: 'No Assets found ...'}

    static darkblue = '#4974a7';
    private errorMessage: ((error: any) => void) | null | undefined;

    poos: Array<ProjectOrganisationalObjective> = [];
    frs: Array<FunctionalRequirement> = [];
    frsArray: Array<FunctionalRequirement> = [];
    fos: Array<FunctionalOutput> = [];
    fosArray: Array<FunctionalOutput> = [];

    foDataDictionary: Array<DataDictionaryEntry> = [];
    assets: Array<Asset> = [];
    assetsArray: Array<Asset> = [];
    assetDataDictionary: Array<DataDictionaryEntry> = [];
    foForSelect: FunctionalOutput | undefined;
    frToSave: FunctionalRequirement | undefined;
    assetToSave: Asset | undefined;
    assetForSelect: Asset | undefined;
    selectedPOOName: string = "";

    selectedFRText: string = "";
    selectedFRArray: Array<String> = [];
    selectedFOId: string = "";
    selectedFOText: string = "";
    selectedFOArray: Array<String> = [];
    selectedAsset: string = "";
    selectedAssetDDId: string = "";
    selectedAssetDDText: string = "";
    selectedAssetArray: Array<String> = [];

    pooIsOpen : boolean = false;
    funcReqIsOpen : boolean = false;
    funcOutputIsOpen : boolean = false;
    assetIsOpen : boolean = false;

    foLinkedToAssetArray: Array<String> = [];
    frLinkedToFOArray: Array<String> = [];
    pooLinkedToFRArray: Array<String> = [];

    /* Asset Modal Variables*/
    asset_all_selected_values: string[] = [];

    /* Functional Output Modal Variables */
    fo_all_selected_asset_values: string[] = [];
    fo_all_selected_fir_values: string[] = [];

    /* Functional Requirements Modal Values */
    fr_all_selected_fo_values: string[] = [];

    /* Organisation Objectives Modal Values */
    poo_selected_oirs: Oir[] = [];
    poo_all_selected_frs: string[] = [];

    entityLinks = new Map<String, Set<String>>();

    closeResult: string | undefined;
    assetForm: any;

    constructor(@Inject(DOCUMENT) private document: any,  private assetService: AssetService,
                private pooService : ProjectOrganisationalObjectiveService, private frService: FunctionalRequirementService,
                private foService: FunctionalOutputService, private assetDdeService: AssetDataDictionaryEntryService,
                private foDdeService: FunctionalOutputDataDictionaryEntryService,
                private modalService: NgbModal,
                private projectDataService : ProjectDataService) {
    }

    getProjectOrganisationalObjectives(): void {
        this.pooService.getProjectOrganisationalObjectives(this.project.id)
            .subscribe(poos => {
                this.poos = poos;
                console.log("POOS fetched = ", poos);
                this.poos.flatMap((poo) => poo.frs.map((fr) => [poo.id, fr]))
                    .forEach(v => this.addEntityLink(v[0], v[1]));
            });
    }

    getFunctionalOutputs(): void {
        this.foService.getFunctionalOutputs(this.project.id)
            .subscribe(fos => {
                this.fos = fos;

                this.fos.flatMap((fo) => fo.assets.map((asset) => [fo.id, asset]))
                    .forEach(v => this.addEntityLink(v[0], v[1]));
            });
    }

    getFunctionalRequirements(): void {
        this.frService.getFunctionalRequirements(this.project.id)
            .subscribe(frs => {
                this.frs = frs;

                this.frs.flatMap((fr) => fr.fos.map((fo) => [fr.id, fo]))
                    .forEach(v => this.addEntityLink(v[0], v[1]));
            });
    }

    getFODataDictionaryEntries(): void {
        this.foDdeService.getFunctionalOutputDataDictionaryEntries(this.project.fo_dd_id)
            .subscribe(fo => {
                this.foDataDictionary = fo;
            });
    }

    getAssets(): void {
        this.assetService.getAssets(this.project.id)
            .subscribe(assets => {
                this.assets = assets;
            });
    }

    getAssetDataDictionaryEntries() : void {
        this.assetDdeService.getAssetDataDictionaryEntries(this.project.asset_dd_id)
            .subscribe(asset => {
                this.assetDataDictionary = asset;
            });
    }

    saveProjectOrganisationalObjective(value: any): void {

        console.log("saveProjectOrganisationalObjective: value = ", value);
        let checkExistingPOOId = 'pooMainId' in value;
        let checkExistingFRs = 'pooExistingFRs' in value;
        let checkSelectedVersions = 'pooVersions' in value;

        console.log("Existing OIRs = ", this.poo_selected_oirs);
        console.log("Seletcted FR links = ", this.poo_all_selected_frs);

        let id = null;
        if(checkExistingPOOId) {
            id = value.pooMainId;
        }
        let poo_name = this.selectedPOOName;
        //let frs = value.frs;
        let frsArray = value.frsArray;
        let existingFRs = [];
        let versionId = "";

        if (checkSelectedVersions) {
           versionId = value.versionupdate;
        }



        /*if(checkExistingFRs) {
            if(value.pooExistingFRs.length > 0) {
                existingFRs = value.pooExistingFRs;
            }
        }*/

       if(frsArray.length !== 0) {
            frsArray.forEach((fr: string) =>{
                this.poo_all_selected_frs.push(fr);
            });
        }


        let ooVersions: OrganisationalObjectiveVersion[] = [];

        let poo = {
            id: id,
            project_id: this.project.id,
            name:poo_name,
            oo_version_id: versionId,
            oo_is_deleted: false,
            oo_versions: ooVersions.flat(),
            oirs: this.poo_selected_oirs.flat(),
            deleted_oirs: [],
            frs: this.poo_all_selected_frs.flat()
        };

        console.log("saving poo", poo);

        this.pooService.save(poo, this.project.id)
            .subscribe(
                () => {
                    //window.alert("poo saved");
                    window.location.reload()
                },
                error => {
                    this.handleRestError(error);
                }
            );
        /* Closing Modal after sending Save */
        this.openModal('pooContent', false);
    }

    saveFunctionalRequirements(value: any): void {
        console.log("Save FR = ", value);
        let checkExistingFRId = 'frMainId' in value;
        let checkExistingFOs = 'frExistingFOs' in value;
        let id = null;
        if(checkExistingFRId) {
            id = value.frMainId;
        }
        let frName = this.selectedFRText;
        //let frNameArray = this.selectedFRArray;
        let fos = value.fos;
        let fosArray = value.fosArray;
        let existingFOs = [];

        if(checkExistingFOs) {
            if(value.frExistingFOs.length > 0) {
                existingFOs = value.frExistingFOs;
            }
        }

        if(fosArray.length !== 0) {
            fosArray.forEach(function(fo: FunctionalOutput){
                existingFOs.push(fo);
            });
        }

        if(this.fr_all_selected_fo_values.length > 0) {
            existingFOs = existingFOs.filter( (e1: string) => !this.fr_all_selected_fo_values.includes( e1 ));
        }

        this.frToSave = {
            id: id,
            name: frName,
            fos: existingFOs.flat()
        };

        let fr = this.frToSave;
        console.log("saving fr = ", this.frToSave);

        this.frService.save(fr, this.project.id)
            .subscribe(
                () => {
                    window.location.reload()
                },
                error => {
                    this.handleRestError(error)
                }
            );

        /* Closing Modal after sending Save */
        this.openModal('functionalRequirementsContent', false);



    }

    saveFunctionalOutput(value: any): void {
        console.log("FO value received: ", value);

        let checkExistingFOId = 'foMainId' in value;
        let checkExistingFIRs = 'foExistingFIRs' in value;
        let checkExistingAssets = 'foExistingAssets' in value;
        let id = null;
        if(checkExistingFOId) {
            id = value.foMainId;
        }
        let foDDId = this.selectedFOId;
        let foNameArray = this.selectedFOArray;
        let firsText = value.firs;
        let assetText = value.assets;
        let assetsArray = value.assetsArray;
        let existingFIRs = [];
        let existingAssets = [];

        if(checkExistingFIRs) {
            if(value.foExistingFIRs.length > 0) {
                existingFIRs = value.foExistingFIRs;
            }
        }

        if(checkExistingAssets) {
            if(value.foExistingAssets.length > 0) {
                existingAssets = value.foExistingAssets;
            }
        }

        if(firsText !== "") {
            existingFIRs.push(firsText);
        }

        if(assetsArray.length !== 0) {
            assetsArray.forEach(function(asset: Asset){
                existingAssets.push(asset);
            });
        }

        if(this.fo_all_selected_fir_values.length > 0) {
            existingFIRs = existingFIRs.filter( (e1: string) => !this.fo_all_selected_fir_values.includes( e1 ));
        }

        if(this.fo_all_selected_asset_values.length > 0) {
            existingAssets = existingAssets.filter( (e1: string) => !this.fo_all_selected_asset_values.includes( e1 ));
        }

        let fo = {
            id: id,
            data_dictionary_entry : {
                id: foDDId,
                text: ''
            },
            name: '',
            firs: existingFIRs.flat(),
            assets: existingAssets.flat()
        };

        this.foService.save(fo, this.project.id)
            .subscribe(
                () => {
                    window.location.reload()
                },
                error => {
                    this.handleRestError(error)
                }
            );

        /* Closing Modal after sending Save */
        this.openModal('functionalOutputContent', false);
        /* Refreshing Screen */
        setTimeout(() =>
            {
                window.location.reload();
            },
            2000);
    }

    saveAsset(value: any): void {
        console.log("Asset dialog value : ", value);
        let checkExistingAssedId = 'assetMainId' in value;
        let checkExistingAIRs = 'assetExistingAIRs' in value;
        let id = '';
        if(checkExistingAssedId) {
            id = value.assetMainId;
        }
        let assetDDId = this.selectedAssetDDId;
        let assetDDText = this.selectedAssetDDText;
        let airText = value.airs;
        let existingAIRs = [];

        if(checkExistingAIRs) {
            if(value.assetExistingAIRs.length > 0) {
                existingAIRs = value.assetExistingAIRs;
            }
        }

        if(airText !== "") {
            existingAIRs.push(airText);
        }

        if(this.asset_all_selected_values.length > 0) {
            existingAIRs = existingAIRs.filter( (e1: string) => !this.asset_all_selected_values.includes( e1 ));
        }

        this.assetToSave = {
            id: id,
            data_dictionary_entry : {
                id: assetDDId,
                text: assetDDText
            },
            name: assetDDText,
            airs: existingAIRs.flat()
        };

        let asset = this.assetToSave;

        console.log("Final asset: ", asset);

        this.assetService.save(asset, this.project.id)
            .subscribe(
                () => window.location.reload(),
                error => {
                    this.handleRestError(error);
                }
            );

        /* Closing Modal after sending Save */
        this.openModal('assetContent', false);

    }

    getPOOsValue(caller: string, selectedPOOName: string): void {

        if (caller === 'form') {
            this.selectedPOOName = (<HTMLInputElement>document. getElementById("pooId")).value;
        } else if (caller === 'entity') {
            this.selectedPOOName = selectedPOOName;
        }
        if(this.selectedPOOName.length > 0) {
            this.enableSubmitBtn(this.selectedPOOName, 'pooId','pooSubmitBtn');
        }
        this.updateOirsAndSelectableFRsForPOO();
    }

    updateOirsAndSelectableFRsForPOO(): void {
        console.log("updateSelectableFRsForPOO");
        let selectedPOO: ProjectOrganisationalObjective | undefined;
        for (let poo of this.poos){
            if (poo.name === this.selectedPOOName){
                console.log("Found POO: ", this.selectedPOOName);
                selectedPOO = poo;
                this.poo_selected_oirs = [...poo.oirs];
                this.poo_all_selected_frs = [...poo.frs];
                break;
            };
        };

        console.log("SelectedPOO Value : ", selectedPOO);

        let frsToSelect: Array<FunctionalRequirement> = [];

        for (let fr of this.frs){
            if(selectedPOO !== undefined) {
                if (!(selectedPOO!.frs.includes(fr.id))){
                    frsToSelect.push(fr);
                }
            } else {
                frsToSelect.push(fr)
            }
        }

        this.pooFROptions = frsToSelect
        console.log('Updated POO FR Options');
    }

    getFRValue(caller: string, selectedFRName: string): void  {
        if (caller === 'form') {
            this.selectedFRText =  (<HTMLInputElement>document. getElementById("frName")).value;
        } else if (caller === 'entity') {
            this.selectedFRText = selectedFRName;
        }

        if(this.selectedFRText.length > 0) {
            this.enableSubmitBtn(this.selectedFRText,'frName','frSubmitBtn');
        }

        /* New FR selected - Clear pooLinkedToFRArray for newly selected FO */
        this.pooLinkedToFRArray = [];

        this.updateSelectableFOsForFR();

        /* Log */
        console.log("Functional Requirement Selected : ",this.selectedFRText);
    }

    updateSelectableFOsForFR(): void {
        let selectedFR: FunctionalRequirement | undefined;
        for (let fr of this.frs){
            if (fr.name === this.selectedFRText){
                console.log("Found FR: ", this.selectedFRText);
                selectedFR = fr;
                break;
            };
        };

        let fosToSelect: Array<FunctionalOutput> = [];

        for (let fo of this.fos){
            if(selectedFR !== undefined) {
                if (!(selectedFR!.fos.includes(fo.id))){
                    this.foForSelect = {
                        id: fo.id,
                        data_dictionary_entry : {
                            id: fo.data_dictionary_entry.id,
                            text: fo.data_dictionary_entry.text
                        },
                        name: fo.data_dictionary_entry.text,
                        firs: fo.firs,
                        assets: fo.assets
                    }
                    fosToSelect.push(this.foForSelect)
                }
            } else {
                this.foForSelect = {
                    id: fo.id,
                    data_dictionary_entry : {
                        id: fo.data_dictionary_entry.id,
                        text: fo.data_dictionary_entry.text
                    },
                    name: fo.data_dictionary_entry.text,
                    firs: fo.firs,
                    assets: fo.assets
                }
                fosToSelect.push(this.foForSelect)
            }

        }

        this.frFOOptions = fosToSelect
        console.log('Updated FR FO Options');
    }


    fosChange(e:  any): void {
        let find = this.foDataDictionary.find(x => x?.text === e.target.value);
        console.log(find?.id);
        this.getFOSValue(find === undefined ? '' : find.id);
    }

    getFOSValue(selectedFOId: string): void {

        this.selectedFOId = selectedFOId;
        let selectedFO = this.foDataDictionary.find(x => x?.id === selectedFOId);
        this.selectedFOText = selectedFO === undefined ? "" : selectedFO.text;

        console.log("Selected FO: ", this.selectedFOId);

        if(this.selectedFOId.length > 0 && this.selectedFOText !== undefined) {
            this.enableSubmitBtn(this.selectedFOText,'fosId','foSubmitBtn');
        }

        /* New FO selected - Clear frLinkedToFOArray for newly selected FO */
        this.frLinkedToFOArray = [];
        this.updateSelectableAssetsForFO();
    }

    updateSelectableAssetsForFO(): void {
        let selectedFO: FunctionalOutput | undefined;
        for (let fo of this.fos){
            if (fo.data_dictionary_entry.id === this.selectedFOId){
                console.log("Found FO: ", this.selectedFOId);
                selectedFO = fo;
                break;
            };
        };

        let assetsToSelect: Array<Asset> = [];

        console.log("Assets loaded from DB (Quantity): ", this.assets.length);
        for (let asset of this.assets){
            if(selectedFO !== undefined) {
                if (!(selectedFO!.assets.includes(asset.id))){
                    this.assetForSelect = {
                        id: asset.id,
                        data_dictionary_entry : {
                            id: asset.data_dictionary_entry.id,
                            text: asset.data_dictionary_entry.text
                        },
                        name: asset.data_dictionary_entry.text,
                        airs: asset.airs,
                    };
                    assetsToSelect.push(this.assetForSelect)
                }
            } else {
                this.assetForSelect = {
                    id: asset.id,
                    data_dictionary_entry : {
                        id: asset.data_dictionary_entry.id,
                        text: asset.data_dictionary_entry.text
                    },
                    name: asset.data_dictionary_entry.text,
                    airs: asset.airs,
                };
                assetsToSelect.push(this.assetForSelect)
            }
        }

        console.log("assetsToSelect Length: ", assetsToSelect.length);
        this.foAssetOptions = assetsToSelect
        console.log('Updated FO Asset Options');
    }

    getAssetValue(caller: string, selectedAssetText: string): void  {
        if (caller === 'form') {
            this.selectedAsset = (<HTMLInputElement>document. getElementById("assetId")).value;
        } else if (caller == 'entity') {
            this.selectedAsset = selectedAssetText;
        }

        console.log("Selected Asset: ", this.selectedAsset);

        if(this.selectedAsset.length > 0) {
            let assetData = this.selectedAsset.split("-");
            this.selectedAssetDDId = assetData[0].trim();
            this.selectedAssetDDText = assetData[1].trim();

            this.enableSubmitBtn(this.selectedAsset, 'assetId','assetSubmitBtn');

            /* Log */
            console.log("ID : ", assetData[0].trim());
            console.log("Text : ", assetData[1].trim());
            console.log("Asset Selected : ",this.selectedAsset);
        } else {
            this.selectedAssetDDId = "";
            this.selectedAssetDDText = "";
        }

        /* New Asset selected - Clear foLinkedToAssetArray for newly selected Asset */
        this.foLinkedToAssetArray = [];


    }

    foLinkedToAssetCheck(assetDDId: string, selectedAssetId: string, foAssetLength: any, assetId: string, foAsset: string): boolean {
        if(assetDDId === selectedAssetId && foAssetLength > 0 && assetId === foAsset) {
            this.foLinkedToAssetArray.push(foAsset);
            console.log("FOLinkedToAsset for assetDDId (" + assetDDId + ") has length : " + this.foLinkedToAssetArray.length);
            return true;
        } else {
            // do nothing
        }
        return false;
    }

    frLinkedToFOCheck(foDDId: string, selectedFuncOutputId: string, frFOSLength: any, funcOutputId: string, funcOutput: string): boolean {
        if(foDDId === selectedFuncOutputId && frFOSLength > 0 && funcOutputId === funcOutput) {
            this.frLinkedToFOArray.push(funcOutput);
            console.log("FRLinkedToFO for foDDId (" + foDDId + ") has length : " + this.frLinkedToFOArray.length);
            return true;
        } else {
            // do nothing
        }
        return false;
    }

    pooLinkedToFRCheck(funcReqName: string, selectedFRName: string, pooFRsLength: any, funcReqId: string, funcReq: string): boolean {
        if(funcReqName === selectedFRName && pooFRsLength > 0 && funcReqId === funcReq) {
            this.pooLinkedToFRArray.push(funcReqName);
            console.log("POOLinkedToFR for funcReqName (" + funcReqName + ") has length : " + this.pooLinkedToFRArray.length);
            return true;
        } else {
            // do nothing
        }
        return false;
    }

    onAssetAIRChange(value: string): void {
        if (this.asset_all_selected_values.includes(value)) {
            this.asset_all_selected_values = this.asset_all_selected_values.filter((item) => item !== value);
        } else {
            this.asset_all_selected_values.push(value);
        }
        console.log(this.asset_all_selected_values);
    }

    onFOAssetChange(value: string): void {
        if (this.fo_all_selected_asset_values.includes(value)) {
            this.fo_all_selected_asset_values = this.fo_all_selected_asset_values.filter((item) => item !== value);
        } else {
            this.fo_all_selected_asset_values.push(value);
        }
        console.log(this.fo_all_selected_asset_values);
    }

    onFOFIRChange(value: string): void {
        if (this.fo_all_selected_fir_values.includes(value)) {
            this.fo_all_selected_fir_values = this.fo_all_selected_fir_values.filter((item) => item !== value);
        } else {
            this.fo_all_selected_fir_values.push(value);
        }
        console.log(this.fo_all_selected_fir_values);
    }

    onFRFOSChange(value: string): void {
        if (this.fr_all_selected_fo_values.includes(value)) {
            this.fr_all_selected_fo_values = this.fr_all_selected_fo_values.filter((item) => item !== value);
        } else {
            this.fr_all_selected_fo_values.push(value);
        }
        console.log(this.fr_all_selected_fo_values);
    }

    onPOOOIRChange(event: any, value: Oir): void {
        if (event.target.checked) {
            this.poo_selected_oirs.push(value);
        } else {
            this.poo_selected_oirs = this.poo_selected_oirs.filter((item) => item !== value);
        }
        console.log("onPOOOIRChange:", this.poo_selected_oirs);
    }

    onPOOFRChange(event:any, value: string): void {
        if (event.target.checked) {
            this.poo_all_selected_frs.push(value);
        }
        else {
            this.poo_all_selected_frs = this.poo_all_selected_frs.filter((item) => item !== value);
        }
        console.log(this.poo_all_selected_frs);
    }

    ngOnInit(): void {}

    private addEntityLink(fromElementId: string, toElementId: string): boolean {
        let entityLines = this.entityLinks.get(fromElementId);
        if(entityLines === undefined) {
            entityLines = new Set<String>();
            this.entityLinks.set(fromElementId, entityLines);
        }
        if(!entityLines.has(toElementId)) {
            entityLines.add(toElementId);
            return true;
        }
        return false;
    }

    ngAfterViewInit(): void {

        this.readProject();
        this.assetDdeService.getAssetDataDictionaryEntries(this.project.asset_dd_id).subscribe(ddes => console.log("Got", ddes.length, "asset data dictionary entries"));
        this.foDdeService.getFunctionalOutputDataDictionaryEntries(this.project.fo_dd_id).subscribe(ddes => console.log("Got", ddes.length, "fo data dictionary entries"));

        this.getFODataDictionaryEntries();
        this.getAssets();
        this.getAssetDataDictionaryEntries();
        this.getFunctionalOutputs();
        this.getFunctionalRequirements();
        this.getProjectOrganisationalObjectives();

        console.log('POOs', this.poos);
        console.log('FRs', this.frs);
        console.log('FOs', this.foDataDictionary);
        console.log('Assets', this.assetDataDictionary);
        console.log('Saved FOs', this.fos);
        console.log('Saved Assets', this.assets);
    }

    pooLinkStart(event: DragEvent): boolean {
        // @ts-ignore
        event.dataTransfer.setData('application/json', JSON.stringify({sourceType: 'project_organisational_objective', source: event.target.id}));
        return true;
    }

    foLinkStart(event: DragEvent): boolean {
        // @ts-ignore
        event.dataTransfer.setData('application/json', JSON.stringify({sourceType: 'functional_output', source: event.target.id}));
        return true;
    }

    frLinkStart(event: DragEvent): boolean {
        // @ts-ignore
        event.dataTransfer.setData('application/json', JSON.stringify({sourceType: 'functional_requirement', source: event.target.id}));
        return true;
    }

    linkToFR(event: DragEvent): boolean {
        event.preventDefault();
        // @ts-ignore
        const data = JSON.parse(event.dataTransfer.getData('application/json'));
        // @ts-ignore
        const targetId = event.target.id;

        console.log(data.source, 'to', targetId);
        if (data.sourceType === 'project_organisational_objective') {
            const startPOO = this.poos.find(poo => poo.id === data.source);
            if (startPOO !== undefined) {
                if (startPOO.frs.find(fr => fr === targetId) === undefined) {
                    startPOO.frs.push(targetId);

                    console.log('Updated POO', startPOO);
                    this.pooService.save(startPOO, this.project.id).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startPOO.frs = startPOO.frs.filter(fr => fr !== targetId);
                        console.log("Failed to save new link", startPOO);
                        this.handleRestError(error);
                    });

                    this.updateOirsAndSelectableFRsForPOO()
                }
            }
        }
        // Didn't manage to link it, return false;
        return false;
    }

    linkToFO(event: DragEvent): boolean {
        event.preventDefault();
        // @ts-ignore
        const data = JSON.parse(event.dataTransfer.getData('application/json'));
        // @ts-ignore
        const targetId = event.target.id;

        console.log(data.source, 'to', targetId);
        if (data.sourceType === 'functional_requirement') {
            const startFr = this.frs.find(fr => fr.id === data.source);
            if (startFr !== undefined) {
                if (startFr.fos.find(fo => fo === targetId) === undefined) {
                    startFr.fos.push(targetId);

                    console.log('Updated FR', startFr);
                    this.frService.save(startFr, this.project.id).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startFr.fos = startFr.fos.filter(fo => fo !== targetId);
                        console.log("Failed to save new link", startFr);
                        this.handleRestError(error)
                    });

                    this.updateSelectableFOsForFR()
                }
            }
        }
        // Didn't manage to link it, return false;
        return false;
    }

    linkToAsset(event: DragEvent): boolean {
        event.preventDefault();
        // @ts-ignore
        const data = JSON.parse(event.dataTransfer.getData('application/json'));
        // @ts-ignore
        const targetId = event.target.id;

        console.log(data.source, 'to', targetId);
        if (data.sourceType === 'functional_output') {
            const startFO = this.fos.find(objective => objective.id === data.source);
            if (startFO !== undefined) {
                if (startFO.assets.find(asset => asset === targetId) === undefined) {
                    startFO.assets.push(targetId);
/*

THIS IS NEED TO CHECK THOROUGHLY WITH MY CODE

 */
                    console.log('Updated FO', startFO);
                    this.foService.save(startFO, this.project.id).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startFO.assets = startFO.assets.filter(asset => asset !== targetId);
                        console.log("Failed to save new link", startFO);
                        this.handleRestError(error)
                    });

                    this.updateSelectableAssetsForFO()
                }
            }
        }
        // Didn't manage to link it, return false;
        return false;
    }

    allowLinkToFR(event: DragEvent): boolean {
        event.preventDefault();
        return true;
    }

    allowLinkToFO(event: DragEvent): boolean {
        event.preventDefault();
        return true;
    }

    allowLinkToAsset(event: DragEvent): boolean {
        event.preventDefault();
        return true;
    }

    getFOLinks(id: string, left: boolean, right: boolean): string[] {
        const links: string[] = [];
        if (left) {
            const linkedFRs = this.frs.filter(fr => fr.fos.find(fo => fo === id));
            linkedFRs.forEach(fr => links.push(fr.id));
            linkedFRs.flatMap(fr => this.getFRLinks(fr.id, true, false)).forEach(fr => links.push(fr));
        }
        if (right) {
            this.fos.filter(fo => fo.id === id).flatMap(fo => fo.assets).forEach(asset => links.push(asset));
        }
        return links;
    }

    getFRLinks(id: string, left: boolean, right: boolean): string[] {
        const links: string[] = [];
        if (right) {
            const linkedFOs = this.frs.filter(fr => fr.id === id).flatMap(fr => fr.fos);
            linkedFOs.forEach(fo => links.push(fo));
            linkedFOs.flatMap(fo => this.getFOLinks(fo, false, true)).forEach(foLinks => links.push(foLinks));
        }
        if (left) {
            this.poos.filter(obj => obj.frs.find(fr => fr === id)).forEach(obj => links.push(obj.id));
        }
        return links;
    }

    getAssetLinks(id: string): string[] {
        const links: string[] = [];
        const linkedFOs: string[] = this.fos.filter(fo => fo.assets.find(asset => asset === id)).map(fo => fo.id);
        linkedFOs.forEach(fo => links.push(fo));
        linkedFOs.flatMap(fo => this.getFOLinks(fo, true, false)).forEach(foLink => links.push(foLink));

        return links;
    }

    getPOOLinks(id: string): string[] {
        const links: string[] = [];
        const linkedFRs: string[] = this.poos.filter(poo => poo.id === id)
            .flatMap(poo => poo.frs);
        linkedFRs.forEach(fr => links.push(fr));
        linkedFRs.flatMap(fr => this.getFRLinks(fr, false, true)).forEach(frLink => links.push(frLink));

        return links;
    }

    mouseEnterFO(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getFOLinks(event.target.id, true, true);
            console.log('FO', event.target.id, 'links to', links);
            // @ts-ignore
            links.forEach(link => document.getElementById(link).style.backgroundColor = IRGraphComponent.darkblue);
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = IRGraphComponent.darkblue;
        }
    }

    mouseLeaveFO(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getFOLinks(event.target.id, true, true);
            // @ts-ignore
            links.forEach(link => document.getElementById(link).style.backgroundColor = null);
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = null;
        }
    }

    mouseEnterAsset(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getAssetLinks(event.target.id);
            console.log('Asset', event.target.id, 'links to', links);
            // @ts-ignore
            links.forEach(link => document.getElementById(link).style.backgroundColor = IRGraphComponent.darkblue);
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = IRGraphComponent.darkblue;
        }
    }

    mouseLeaveAsset(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getAssetLinks(event.target.id);
            // @ts-ignore
            links.forEach(link => document.getElementById(link).style.backgroundColor = null);
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = null;
        }
    }

    mouseEnterPOO(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getPOOLinks(event.target.id);
            console.log('POO', event.target.id, 'links to', links);
            // @ts-ignore
            links.forEach(link => document.getElementById(link).style.backgroundColor = IRGraphComponent.darkblue);
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = IRGraphComponent.darkblue;
        }
    }

    mouseLeavePOO(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getPOOLinks(event.target.id);
            // @ts-ignore
            links.forEach(link => document.getElementById(link).style.backgroundColor = null);
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = null;
        }
    }

    mouseEnterFR(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getFRLinks(event.target.id, true, true);
            console.log('FR', event.target.id, 'links to', links);
            // @ts-ignore
            links.forEach(link => document.getElementById(link).style.backgroundColor = IRGraphComponent.darkblue);
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = IRGraphComponent.darkblue;
        }
    }

    mouseLeaveFR(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getFRLinks(event.target.id, true, true);
            // @ts-ignore
            links.forEach(link => document.getElementById(link).style.backgroundColor = null);
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = null;
        }
    }

    open(content: any) {
        this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', centered: true}).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
        }, (reason) => {
            this.closeResult = `Dismissed ${IRGraphComponent.getDismissReason(reason)}`;
        });
    }

    openModal(content: string, isOpen : boolean) : void {
        if(isOpen) {
            this.clearContent(content);

            if(content === 'pooContent') {
                this.pooIsOpen = isOpen;
            } else if(content === 'functionalRequirementsContent') {
                this.funcReqIsOpen = isOpen;
            } else if(content === 'functionalOutputContent') {
                this.funcOutputIsOpen = isOpen;
            } else if(content === 'assetContent') {
                this.assetIsOpen = isOpen;
            }
        } else if(!isOpen) {
            if(content === 'pooContent') {
                this.pooIsOpen = isOpen;
            } else if(content === 'functionalRequirementsContent') {
                this.funcReqIsOpen = isOpen;
            } else if(content === 'functionalOutputContent') {
                this.funcOutputIsOpen = isOpen;
            } else if(content === 'assetContent') {
                this.assetIsOpen = isOpen;
            }
            this.clearContent(content);
        }

        console.log(content + ' open status is : ' + isOpen );
    }

    openPrePopulatedModal(entityType: string, selectedEntityId: string, selectedEntityText: string, isOpen : boolean): void {
        console.log("I clicked Entity (" + entityType + ") with ID: " + selectedEntityId + " and Text: " + selectedEntityText);

        if(entityType === 'asset') {
            if(selectedEntityId !== "" && selectedEntityText !== "") {
                this.selectedAsset = selectedEntityText;
                this.openModal('assetContent', true);
                this.getAssetValue('entity', selectedEntityText);
            }
        } else if (entityType === 'funcOutput') {
            console.log(entityType + " - selectedEntityId=", selectedEntityId);
            if(selectedEntityId !== "" && selectedEntityText !== "") {
                this.openModal('functionalOutputContent', true);
                console.log("selectedEntityId=", selectedEntityId);
                this.getFOSValue(selectedEntityId);
            }
        } else if (entityType === 'funcRequirements') {
            if (selectedEntityId !== "" && selectedEntityText !== "") {
                this.openModal('functionalRequirementsContent', true);
                this.getFRValue('entity', selectedEntityText);
            }
        } else if (entityType === 'poos') {
            if (selectedEntityId !== "" && selectedEntityText !== "") {
                this.openModal('pooContent', true);
                this.getPOOsValue('entity', selectedEntityText);
            }
        }
        console.log("exit openPrePopulatedModal");
    }

    /* Clear the form once pop-up Window is closed */
    clearContent(content: string): void {
        console.log("Clearing " + content + " fields");
        if(content === 'pooContent') {
            this.resetFormContentState('pooId','pooSubmitBtn');
            this.getPOOsValue('form','');
        } else if(content === 'functionalRequirementsContent') {
            this.resetFormContentState('frName','frSubmitBtn');
            this.getFRValue('form','');
        } else if(content === 'functionalOutputContent') {
            this.resetFormContentState('fosId','foSubmitBtn');
            this.getFOSValue('');
        } else if(content === "assetContent") {
            this.resetFormContentState('assetId','assetSubmitBtn');
            this.getAssetValue('form','');
        }
    }

    /* Delete Entity Methods */
    deleteAsset(assetId: string, assetName: string): void {
        console.log("Processing delete request for : " + assetName);

        this.assetService.delete(assetId, this.project.id)
            .subscribe(
                () => window.location.reload(),
                error => {
                    this.handleRestError(error);
                });

        /* Closing Modal after sending Save */
        this.modalService.dismissAll();
        this.openModal('assetContent', false);

    }

    deleteFuncOutput(funcOutputId: string, funcOutputName: string): void {
        console.log("Processing delete request for : " + funcOutputName);

        this.foService.delete(funcOutputId, this.project.id)
            .subscribe(
                () =>  window.location.reload(),
                error => {
                    this.handleRestError(error);
                });

        /* Closing Modal after sending Save */
        this.modalService.dismissAll();
        this.openModal('functionalOutputContent', false);

    }

    deleteFuncRequirement(funcReqId: string, funcReqName: string): void {
        console.log("Processing delete request for : " + funcReqName);

        this.frService.delete(funcReqId, this.project.id)
            .subscribe(
                () =>  window.location.reload(),
                error => {
                    this.handleRestError(error);
                });

        /* Closing Modal after sending Save */
        this.modalService.dismissAll();
        this.openModal('functionalRequirementsContent', false);
    }

    deleteProjectOrganisationalObjective(pooId: string, pooName: string): void {
        console.log("Processing delete request for : " + pooName);

        this.pooService.delete(pooId, this.project.id)
            .subscribe(
                () =>  window.location.reload(),
                error => {
                    this.handleRestError(error)
                });

        /* Closing Modal after sending Save */
        this.modalService.dismissAll();
        this.openModal('pooContent', false);
    }

    /* Enable Popup Window Submit Button (on Title input) */
    enableSubmitBtn(selectedEntityName: string, id: string, btnText: string) : void {
        console.log("selectedEntityName : ", selectedEntityName);
        (<HTMLInputElement>document.getElementById(id)).value = selectedEntityName;
        (<HTMLInputElement>document.getElementById(id)).classList.remove("is-invalid");
        (<HTMLInputElement>document.getElementById(btnText)).disabled = false;
    }

    /* Reset Popup Window Form Contents (incl. CSS) */
    resetFormContentState(id: string, btnText: string) : void {
        console.log("resetFormContentState - entry id=", id);
        (<HTMLInputElement>document.getElementById(id)).value = "";
        (<HTMLInputElement>document.getElementById(id)).classList.add("is-invalid");
        (<HTMLInputElement>document.getElementById(btnText)).disabled = true;
        this.selectedAssetArray = [];
        this.selectedFRArray = [];
        console.log("resetFormContentState - exit");
    }

    private static getDismissReason(reason: any): string {
        if (reason === ModalDismissReasons.ESC) {
            return 'by pressing ESC';
        } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
            return 'by clicking on a backdrop';
        } else {
            return `with: ${reason}`;
        }
    }

    reloadWindow(): void {
        window.location.reload();
    }

    readProject(){
        let storedProject = this.projectDataService.getProject();
        if (storedProject != null) {
            console.log("Setting project to ", storedProject);
            this.project = storedProject;
        } else {
            console.log("Failed to set projectId to ", storedProject);
        }
    }

    pooUpdateAlert () {
       window.alert("Click on individual organisational objectives to update");
    }

    handleRestError(error: any) {
        this.errorMessage = error.message;
        console.error('There was an error!', error);
        if (error.error.forUi == true) {
            window.alert(error.error.error);
        }
        this.reloadWindow();
    }
}
import {AfterViewInit, Component, Inject, OnInit, ViewChild, ElementRef} from '@angular/core';
import {Asset} from '../../asset';
import {ProjectOrganisationalObjective} from '../../project-organisational-objective';
import {OrganisationalObjectiveVersion} from '../../organisational-objective-version';
import {FunctionalRequirement} from '../../functional-requirement';
import {FunctionalOutput} from '../../functional-output';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AssetService} from '../../asset.service';
import {ProjectOrganisationalObjectiveService} from '../../project-organisational-objective.service';
import {FunctionalRequirementService} from '../../functional-requirement.service';
import {FunctionalOutputService} from '../../functional-output.service';
import {DOCUMENT} from '@angular/common';
import {AssetDataDictionaryEntryService} from '../../asset-data-dictionary-entry.service';
import {FunctionalOutputDataDictionaryEntryService} from '../../functional-output-data-dictionary-entry.service';
import {DataDictionaryEntry} from '../../data-dictionary-entry';
import {IMultiSelectSettings, IMultiSelectOption, IMultiSelectTexts} from 'ngx-bootstrap-multiselect';
import { ProjectDataService } from 'src/app/project-data.service';
import { Project } from 'src/app/project';
import { Oir } from 'src/app/organisational-objective';
import {PermissionService} from '../../services/permission.service';

@Component({
    selector: 'app-irgraph',
    templateUrl: './irgraph.component.html',
    styleUrls: ['./irgraph.component.scss']
})
export class IRGraphComponent implements OnInit, AfterViewInit {

    constructor(@Inject(DOCUMENT) private document: any,  private assetService: AssetService,
                private pooService: ProjectOrganisationalObjectiveService, private frService: FunctionalRequirementService,
                private foService: FunctionalOutputService, private assetDdeService: AssetDataDictionaryEntryService,
                private foDdeService: FunctionalOutputDataDictionaryEntryService,
                private modalService: NgbModal,
                private projectDataService: ProjectDataService,
                public permissionService: PermissionService) {
    }

    static darkblue = '#4974a7';

    project: Project = {
        id: '',
        name: '',
        fo_dd_id: '',
        asset_dd_id: ''
    };
    optionsModel: Array<number> = [];
    mySettings: IMultiSelectSettings = {buttonClasses: 'form-control element-text', enableSearch: true, dynamicTitleMaxItems: 0};
    pooFROptions: Array<IMultiSelectOption> = [];
    pooFRTexts: IMultiSelectTexts = {defaultTitle: 'Select FRs to link', searchEmptyResult: 'No FRs found ...'};
    frFOOptions: Array<IMultiSelectOption> = [];
    frFOTexts: IMultiSelectTexts = {defaultTitle: 'Select FOs to link', searchEmptyResult: 'No FOs found ...'};
    foAssetOptions: Array<IMultiSelectOption> = [];
    foAssetTexts: IMultiSelectTexts = {defaultTitle: 'Select Assets to link', searchEmptyResult: 'No Assets found ...'};
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
    selectedPOOName = '';

    selectedFRText = '';
    selectedFRArray: Array<string> = [];
    selectedFOId = '';
    selectedFOText = '';
    selectedFOArray: Array<string> = [];
    selectedAsset = '';
    selectedAssetDDId = '';
    selectedAssetDDText = '';
    selectedAssetArray: Array<string> = [];

    pooIsOpen = false;
    funcReqIsOpen = false;
    funcOutputIsOpen = false;
    assetIsOpen = false;

    foLinkedToAssetArray: Array<string> = [];
    frLinkedToFOArray: Array<string> = [];
    pooLinkedToFRArray: Array<string> = [];

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

    entityLinks = new Map<string, Set<string>>();

    closeResult: string | undefined;
    assetForm: any;

    @ViewChild('elPOOId') elPOOId!: ElementRef;
    @ViewChild('elPOOSubmitBtn') elPOOSubmitBtn!: ElementRef;
    @ViewChild('elFRId') elFRId!: ElementRef;
    @ViewChild('elFRSubmitBtn') elFRSubmitBtn!: ElementRef;
    @ViewChild('elFOId') elFOId!: ElementRef;
    @ViewChild('elFOSubmitBtn') elFOSubmitBtn!: ElementRef;
    @ViewChild('elAssetId') elAssetId!: ElementRef;
    @ViewChild('elAssetSubmitBtn') elAssetSubmitBtn!: ElementRef;

    private static getDismissReason(reason: any): string {
        if (reason === ModalDismissReasons.ESC) {
            return 'by pressing ESC';
        } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
            return 'by clicking on a backdrop';
        } else {
            return `with: ${reason}`;
        }
    }

    ngOnInit(): void {}
    ngAfterViewInit(): void {
        this.readProject();
        this.assetDdeService.getAssetDataDictionaryEntries(this.project.asset_dd_id);
        this.foDdeService.getFunctionalOutputDataDictionaryEntries(this.project.fo_dd_id);

        this.getFODataDictionaryEntries();
        this.getAssets();
        this.getAssetDataDictionaryEntries();
        this.getFunctionalOutputs();
        this.getFunctionalRequirements();
        this.getProjectOrganisationalObjectives();
    }

    getProjectOrganisationalObjectives(): void {
        this.pooService.getProjectOrganisationalObjectives(this.project.id)
            .subscribe(poos => {
                this.poos = poos;
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

    getAssetDataDictionaryEntries(): void {
        this.assetDdeService.getAssetDataDictionaryEntries(this.project.asset_dd_id)
            .subscribe(asset => {
                this.assetDataDictionary = asset;
            });
    }

    saveProjectOrganisationalObjective(value: any): void {
        const checkExistingPOOId = 'pooMainId' in value;
        const checkSelectedVersions = 'pooVersions' in value;

        let id = null;
        if (checkExistingPOOId) {
            id = value.pooMainId;
        }
        const pooName = this.selectedPOOName;
        const array = value.frsArray;
        let versionId = '';

        if (checkSelectedVersions) {
           versionId = value.versionupdate;
        }

        if (array.length !== 0) {
            array.forEach((fr: string) => {
                this.poo_all_selected_frs.push(fr);
            });
        }

        const ooVersions: OrganisationalObjectiveVersion[] = [];

        const poo = {
            id,
            project_id: this.project.id,
            name: pooName,
            oo_version_id: versionId,
            oo_is_deleted: false,
            oo_versions: ooVersions.flat(),
            oirs: this.poo_selected_oirs.flat(),
            deleted_oirs: [],
            frs: this.poo_all_selected_frs.flat()
        };

        this.pooService.save(poo, this.project.id)
            .subscribe(
                () => {
                    window.location.reload();
                },
                error => {
                    this.handleRestError(error);
                }
            );
        /* Closing Modal after sending Save */
        this.openModal('pooContent', false);
    }

    saveFunctionalRequirements(value: any): void {
        const checkExistingFRId = 'frMainId' in value;
        const checkExistingFOs = 'frExistingFOs' in value;
        let id = null;
        if (checkExistingFRId) {
            id = value.frMainId;
        }else{
            id = this.frToSave ? this.frToSave.id : id;
        }
        const frName = this.selectedFRText;
        const fosArray = value.fosArray;
        let existingFOs = [];

        if (checkExistingFOs) {
            if (value.frExistingFOs.length > 0) {
                existingFOs = value.frExistingFOs;
            }
        }

        if (fosArray.length !== 0) {
            fosArray.forEach((fo: FunctionalOutput) => {
                existingFOs.push(fo);
            });
        }

        if (this.fr_all_selected_fo_values.length > 0) {
            existingFOs = existingFOs.filter( (e1: string) => !this.fr_all_selected_fo_values.includes( e1 ));
        }



        const fr = {
            id: id,
            name: frName,
            fos: existingFOs.flat()
        };
        this.frService.save(fr, this.project.id)
            .subscribe(
                () => {
                    window.location.reload();
                },
                error => {
                    this.handleRestError(error);
                }
            );

        /* Closing Modal after sending Save */
        this.openModal('functionalRequirementsContent', false);
    }

    saveFunctionalOutput(value: any): void {
        const checkExistingFOId = 'foMainId' in value;
        const checkExistingFIRs = 'foExistingFIRs' in value;
        const checkExistingAssets = 'foExistingAssets' in value;
        let id = null;
        if (checkExistingFOId) {
            id = value.foMainId;
        }
        const foDDId = this.selectedFOId;
        const foNameArray = this.selectedFOArray;
        const firsText = value.firs;
        const assetText = value.assets;
        const assetsArray = value.assetsArray;
        let existingFIRs = [];
        let existingAssets = [];

        if (checkExistingFIRs) {
            if (value.foExistingFIRs.length > 0) {
                existingFIRs = value.foExistingFIRs;
            }
        }

        if (checkExistingAssets) {
            if (value.foExistingAssets.length > 0) {
                existingAssets = value.foExistingAssets;
            }
        }

        if (firsText !== '') {
            existingFIRs.push(firsText);
        }

        if (assetsArray.length !== 0) {
            assetsArray.forEach((asset: Asset) => {
                existingAssets.push(asset);
            });
        }

        if (this.fo_all_selected_fir_values.length > 0) {
            existingFIRs = existingFIRs.filter( (e1: string) => !this.fo_all_selected_fir_values.includes( e1 ));
        }

        if (this.fo_all_selected_asset_values.length > 0) {
            existingAssets = existingAssets.filter( (e1: string) => !this.fo_all_selected_asset_values.includes( e1 ));
        }

        const fo = {
            id,
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
                    window.location.reload();
                },
                error => {
                    this.handleRestError(error);
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
        const checkExistingAssedId = 'assetMainId' in value;
        const checkExistingAIRs = 'assetExistingAIRs' in value;
        let id = '';
        if (checkExistingAssedId) {
            id = value.assetMainId;
        }
        const assetDDId = this.selectedAssetDDId;
        const assetDDText = this.selectedAssetDDText;
        const airText = value.airs;
        let existingAIRs = [];

        if (checkExistingAIRs) {
            if (value.assetExistingAIRs.length > 0) {
                existingAIRs = value.assetExistingAIRs;
            }
        }

        if (airText !== '') {
            existingAIRs.push(airText);
        }

        if (this.asset_all_selected_values.length > 0) {
            existingAIRs = existingAIRs.filter( (e1: string) => !this.asset_all_selected_values.includes( e1 ));
        }

        this.assetToSave = {
            id,
            data_dictionary_entry : {
                id: assetDDId,
                text: assetDDText
            },
            name: assetDDText,
            airs: existingAIRs.flat()
        };

        const asset = this.assetToSave;

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
            this.selectedPOOName = this.elPOOId.nativeElement.value;
        } else if (caller === 'entity') {
            this.selectedPOOName = selectedPOOName;
        }
        if (this.selectedPOOName.length > 0) {
            this.enableSubmitBtn(this.selectedPOOName, this.elPOOId, this.elPOOSubmitBtn);
        }
        this.updateOirsAndSelectableFRsForPOO();
    }

    updateOirsAndSelectableFRsForPOO(): void {
        let selectedPOO: ProjectOrganisationalObjective | undefined;
        for (const poo of this.poos){
            if (poo.name === this.selectedPOOName){
                selectedPOO = poo;
                this.poo_selected_oirs = [...poo.oirs];
                this.poo_all_selected_frs = [...poo.frs];
                break;
            }
        }

        const frsToSelect: Array<FunctionalRequirement> = [];

        for (const fr of this.frs){
            if (selectedPOO !== undefined) {
                if (!(selectedPOO!.frs.includes(fr.id))){
                    frsToSelect.push(fr);
                }
            } else {
                frsToSelect.push(fr);
            }
        }

        this.pooFROptions = frsToSelect;
    }

    getFRValue(caller: string, selectedFRName: string): void  {
        if (caller === 'form') {
            this.selectedFRText =  this.elFRId.nativeElement.value;
        } else if (caller === 'entity') {
            const findFr = this.frs.find(x => x.name === selectedFRName);
            this.frToSave = findFr ? findFr : this.frToSave;
            this.selectedFRText = selectedFRName;
        }

        if (this.selectedFRText.length > 0) {
            this.enableSubmitBtn(this.selectedFRText, this.elFRId, this.elFRSubmitBtn);
        }

        /* New FR selected - Clear pooLinkedToFRArray for newly selected FO */
        this.pooLinkedToFRArray = [];

        this.updateSelectableFOsForFR();
    }

    updateSelectableFOsForFR(): void {
        let selectedFR: FunctionalRequirement | undefined;
        for (const fr of this.frs){
            if (fr.name === this.selectedFRText){
                selectedFR = fr;
                break;
            }
        }

        const fosToSelect: Array<FunctionalOutput> = [];

        for (const fo of this.fos){
            if (selectedFR !== undefined) {
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
                    };
                    fosToSelect.push(this.foForSelect);
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
                };
                fosToSelect.push(this.foForSelect);
            }

        }

        this.frFOOptions = fosToSelect;
    }


    fosChange(e: any): void {
        const find = this.foDataDictionary.find(x => x?.text === e.target.value);
        this.getFOSValue(find === undefined ? '' : find.id);
    }

    getFOSValue(selectedFOId: string): void {
        this.selectedFOId = selectedFOId;
        const selectedFO = this.foDataDictionary.find(x => x?.id === selectedFOId);
        this.selectedFOText = selectedFO === undefined ? '' : selectedFO.text;

        if (this.selectedFOId.length > 0 && this.selectedFOText !== undefined) {
            this.enableSubmitBtn(this.selectedFOText, this.elFOId, this.elFOSubmitBtn);
        }

        /* New FO selected - Clear frLinkedToFOArray for newly selected FO */
        this.frLinkedToFOArray = [];
        this.updateSelectableAssetsForFO();
    }

    updateSelectableAssetsForFO(): void {
        let selectedFO: FunctionalOutput | undefined;
        for (const fo of this.fos){
            if (fo.data_dictionary_entry.id === this.selectedFOId){
                selectedFO = fo;
                break;
            }
        }


        const assetsToSelect: Array<Asset> = [];
        for (const asset of this.assets){
            if (selectedFO !== undefined) {
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
                    assetsToSelect.push(this.assetForSelect);
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
                assetsToSelect.push(this.assetForSelect);
            }
        }
        this.foAssetOptions = assetsToSelect;
    }

    getAssetValue(caller: string, selectedAssetText: string): void  {
        if (caller === 'form') {
            this.selectedAsset = this.elAssetId.nativeElement.value;
        } else if (caller == 'entity') {
            this.selectedAsset = selectedAssetText;
        }

        if (this.selectedAsset.length > 0) {
            const assetData = this.selectedAsset.split('-');
            this.selectedAssetDDId = assetData[0].trim();
            this.selectedAssetDDText = assetData[1].trim();
            this.enableSubmitBtn(this.selectedAsset, this.elAssetId, this.elAssetSubmitBtn);
        } else {
            this.selectedAssetDDId = '';
            this.selectedAssetDDText = '';
        }

        /* New Asset selected - Clear foLinkedToAssetArray for newly selected Asset */
        this.foLinkedToAssetArray = [];
    }

    foLinkedToAssetCheck(assetDDId: string, selectedAssetId: string, foAssetLength: any, assetId: string, foAsset: string): boolean {
        if (assetDDId === selectedAssetId && foAssetLength > 0 && assetId === foAsset) {
            this.foLinkedToAssetArray.push(foAsset);
            return true;
        }
        return false;
    }

    frLinkedToFOCheck(foDDId: string, selectedFuncOutputId: string, frFOSLength: any, funcOutputId: string, funcOutput: string): boolean {
        if (foDDId === selectedFuncOutputId && frFOSLength > 0 && funcOutputId === funcOutput) {
            this.frLinkedToFOArray.push(funcOutput);
            return true;
        }
        return false;
    }

    pooLinkedToFRCheck(funcReqName: string, selectedFRName: string, pooFRsLength: any, funcReqId: string, funcReq: string): boolean {
        if (funcReqName === selectedFRName && pooFRsLength > 0 && funcReqId === funcReq) {
            this.pooLinkedToFRArray.push(funcReqName);
            return true;
        }
        return false;
    }

    onAssetAIRChange(value: string): void {
        if (this.asset_all_selected_values.includes(value)) {
            this.asset_all_selected_values = this.asset_all_selected_values.filter((item) => item !== value);
        } else {
            this.asset_all_selected_values.push(value);
        }
    }

    onFOAssetChange(value: string): void {
        if (this.fo_all_selected_asset_values.includes(value)) {
            this.fo_all_selected_asset_values = this.fo_all_selected_asset_values.filter((item) => item !== value);
        } else {
            this.fo_all_selected_asset_values.push(value);
        }
    }

    onFOFIRChange(value: string): void {
        if (this.fo_all_selected_fir_values.includes(value)) {
            this.fo_all_selected_fir_values = this.fo_all_selected_fir_values.filter((item) => item !== value);
        } else {
            this.fo_all_selected_fir_values.push(value);
        }
    }

    onFRFOSChange(value: string): void {
        if (this.fr_all_selected_fo_values.includes(value)) {
            this.fr_all_selected_fo_values = this.fr_all_selected_fo_values.filter((item) => item !== value);
        } else {
            this.fr_all_selected_fo_values.push(value);
        }
    }

    onPOOOIRChange(event: any, value: Oir): void {
        if (event.target.checked) {
            this.poo_selected_oirs.push(value);
        } else {
            this.poo_selected_oirs = this.poo_selected_oirs.filter((item) => item !== value);
        }
    }

    onPOOFRChange(event: any, value: string): void {
        if (event.target.checked) {
            this.poo_all_selected_frs.push(value);
        }
        else {
            this.poo_all_selected_frs = this.poo_all_selected_frs.filter((item) => item !== value);
        }
    }

    private addEntityLink(fromElementId: string, toElementId: string): boolean {
        let entityLines = this.entityLinks.get(fromElementId);
        if (entityLines === undefined) {
            entityLines = new Set<string>();
            this.entityLinks.set(fromElementId, entityLines);
        }
        if (!entityLines.has(toElementId)) {
            entityLines.add(toElementId);
            return true;
        }
        return false;
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

        if (data.sourceType === 'project_organisational_objective') {
            const startPOO = this.poos.find(poo => poo.id === data.source);
            if (startPOO !== undefined) {
                if (startPOO.frs.find(fr => fr === targetId) === undefined) {
                    startPOO.frs.push(targetId);
                    this.pooService.save(startPOO, this.project.id).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startPOO.frs = startPOO.frs.filter(fr => fr !== targetId);
                        this.handleRestError(error);
                    });

                    this.updateOirsAndSelectableFRsForPOO();
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

        if (data.sourceType === 'functional_requirement') {
            const startFr = this.frs.find(fr => fr.id === data.source);
            if (startFr !== undefined) {
                if (startFr.fos.find(fo => fo === targetId) === undefined) {
                    startFr.fos.push(targetId);
                    this.frService.save(startFr, this.project.id).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startFr.fos = startFr.fos.filter(fo => fo !== targetId);
                        this.handleRestError(error);
                    });

                    this.updateSelectableFOsForFR();
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
        if (data.sourceType === 'functional_output') {
            const startFO = this.fos.find(objective => objective.id === data.source);
            if (startFO !== undefined) {
                if (startFO.assets.find(asset => asset === targetId) === undefined) {
                    startFO.assets.push(targetId);
                    this.foService.save(startFO, this.project.id).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startFO.assets = startFO.assets.filter(asset => asset !== targetId);
                        this.handleRestError(error);
                    });

                    this.updateSelectableAssetsForFO();
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
            links.forEach(link => {
                // @ts-ignore
                document.getElementById(link).style.backgroundColor = IRGraphComponent.darkblue;
            });
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = null;
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

    openModal(content: string, isOpen: boolean): void {
        if (isOpen) {
            this.clearContent(content);

            if (content === 'pooContent') {
                this.pooIsOpen = isOpen;
            } else if (content === 'functionalRequirementsContent') {
                this.funcReqIsOpen = isOpen;
            } else if (content === 'functionalOutputContent') {
                this.funcOutputIsOpen = isOpen;
            } else if (content === 'assetContent') {
                this.assetIsOpen = isOpen;
            }
        } else if (!isOpen) {
            if (content === 'pooContent') {
                this.pooIsOpen = isOpen;
            } else if (content === 'functionalRequirementsContent') {
                this.funcReqIsOpen = isOpen;
            } else if (content === 'functionalOutputContent') {
                this.funcOutputIsOpen = isOpen;
            } else if (content === 'assetContent') {
                this.assetIsOpen = isOpen;
            }
            this.clearContent(content);
        }
    }

    openPrePopulatedModal(entityType: string, selectedEntityId: string, selectedEntityText: string,
                          isOpen: boolean, event: MouseEvent): void {
        if (entityType === 'asset') {
            if (selectedEntityId !== '' && selectedEntityText !== '') {
                this.selectedAsset = selectedEntityText;
                this.openModal('assetContent', true);
                this.getAssetValue('entity', selectedEntityText);
            }
        } else if (entityType === 'funcOutput') {
            if (selectedEntityId !== '' && selectedEntityText !== '') {
                this.openModal('functionalOutputContent', true);
                this.getFOSValue(selectedEntityId);
            }
        } else if (entityType === 'funcRequirements') {
            if (selectedEntityId !== '' && selectedEntityText !== '') {
                this.openModal('functionalRequirementsContent', true);
                this.getFRValue('entity', selectedEntityText);
            }
        } else if (entityType === 'poos') {
            if (selectedEntityId !== '' && selectedEntityText !== '') {
                this.openModal('pooContent', true);
                this.getPOOsValue('entity', selectedEntityText);
            }
        }
    }

    /* Clear the form once pop-up Window is closed */
    clearContent(content: string): void {
        if (content === 'pooContent') {
            this.resetFormContentState(this.elPOOId, this.elPOOSubmitBtn);
            this.getPOOsValue('form', '');
        } else if (content === 'functionalRequirementsContent') {
            this.resetFormContentState(this.elFRId, this.elFRSubmitBtn);
            this.getFRValue('form', '');
        } else if (content === 'functionalOutputContent') {
            this.resetFormContentState(this.elFOId, this.elFOSubmitBtn);
            this.getFOSValue('');
        } else if (content === 'assetContent') {
            this.resetFormContentState(this.elAssetId, this.elAssetSubmitBtn);
            this.getAssetValue('form', '');
        }
    }

    /* Delete Entity Methods */
    deleteAsset(assetId: string, assetName: string): void {

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

        this.pooService.delete(pooId, this.project.id)
            .subscribe(
                () =>  window.location.reload(),
                error => {
                    this.handleRestError(error);
                });

        /* Closing Modal after sending Save */
        this.modalService.dismissAll();
        this.openModal('pooContent', false);
    }

    /* Enable Popup Window Submit Button (on Title input) */
    enableSubmitBtn(selectedEntityName: string, el: ElementRef, btnEl: ElementRef): void {
        el.nativeElement.value = selectedEntityName;
        el.nativeElement.classList.remove('is-invalid');
        btnEl.nativeElement.disabled = false;
    }

    /* Reset Popup Window Form Contents (incl. CSS) */
    resetFormContentState(el: ElementRef, btnEl: ElementRef): void {
        el.nativeElement.value = '';
        el.nativeElement.classList.add('is-invalid');
        btnEl.nativeElement.disabled = true;

        this.selectedAssetArray = [];
        this.selectedFRArray = [];
    }

    reloadWindow(): void {
        window.location.reload();
    }

    readProject(){
        const storedProject = this.projectDataService.getProject();
        if (storedProject != null) {
            this.project = storedProject;
        }
    }

    pooUpdateAlert() {
       window.alert('Click on individual organisational objectives to update');
    }

    handleRestError(error: any) {
        this.errorMessage = error.message;
        if (error.status === 403) {
            window.alert('You do not have permission to access the function you have requested');
        }
        else if (error.error.forUi === true) {
            window.alert(error.error.error);
        }
        this.reloadWindow();
    }
}

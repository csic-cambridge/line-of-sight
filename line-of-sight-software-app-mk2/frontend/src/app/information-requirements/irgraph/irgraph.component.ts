import {AfterViewInit, Component, Inject, NgZone, OnInit} from '@angular/core';
import {Asset} from '../../asset';
import {OrganisationalObjective} from '../../organisational-objective';
import {FunctionalRequirement} from '../../functional-requirement';
import {FunctionalObjective} from '../../functional-objective';
import {ModalDismissReasons, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AssetService} from "../../asset.service";
import {OrganisationalObjectiveService} from "../../organisational-objective.service";
import {FunctionalRequirementService} from "../../functional-requirement.service";
import {FunctionalObjectiveService} from "../../functional-objective.service";
import {DOCUMENT} from "@angular/common";
import {AssetDataDictionaryEntryService} from "../../asset-data-dictionary-entry.service";
import {FunctionalObjectiveDataDictionaryEntryService} from "../../functional-objective-data-dictionary-entry.service";
import {AuthenticationService} from "../../authentication.service";

@Component({
    selector: 'app-irgraph',
    templateUrl: './irgraph.component.html',
    styleUrls: ['./irgraph.component.scss']
})
export class IRGraphComponent implements OnInit, AfterViewInit {

    static darkblue = '#4974a7';
    private errorMessage: ((error: any) => void) | null | undefined;

    objectives: Array<OrganisationalObjective> = [];
    frs: Array<FunctionalRequirement> = [];
    fos: Array<FunctionalObjective> = [];
    assets: Array<Asset> = [];
    ooToSave: OrganisationalObjective | undefined;
    foToSave: FunctionalObjective | undefined;
    frToSave: FunctionalRequirement | undefined;
    assetToSave: Asset | undefined;
    selectedOOName: string = "";
    selectedFRText: string = "";
    selectedFO: string = "";
    selectedFOId: string = "";
    selectedFOText: string = "";
    selectedAsset: string = "";
    selectedAssetDDId: string = "";
    selectedAssetDDText: string = "";

    /* Asset Modal Variables*/
    asset_all_selected_values: string[] = [];

    /* Functional Objective Modal Variables */
    fo_all_selected_asset_values: string[] = [];
    fo_all_selected_fir_values: string[] = [];

    /* Functional Requirements Modal Values */
    fr_all_selected_fo_values: string[] = [];

    /* Organisation Objectives Modal Values */
    oo_all_selected_oir_values: string[] = [];
    oo_all_selected_fr_values: string[] = [];

    entityLinks = new Map<String, Set<String>>();

    closeResult: string | undefined;
    assetForm: any;

    constructor(@Inject(DOCUMENT) private document: any, private assetService: AssetService,
                private ooService : OrganisationalObjectiveService, private frService: FunctionalRequirementService,
                private foService: FunctionalObjectiveService, private assetDdeService: AssetDataDictionaryEntryService,
                private foDdeService: FunctionalObjectiveDataDictionaryEntryService,
                private authService: AuthenticationService, private modalService: NgbModal, private _ngZone: NgZone) {
    }

    getOrganisationalObjectives(): void {
        this.ooService.getOrganisationalObjectives()
            .subscribe(objectives => {
                this.objectives = objectives;

                this.objectives.flatMap((objective) => objective.frs.map((fr) => [objective.id, fr]))
                    .forEach(v => this.addEntityLink(v[0], v[1]));
            });
    }
    getFunctionalObjectives(): void {
        this.foService.getFunctionalObjectives()
            .subscribe(objectives => {
                this.fos = objectives;

                this.fos.flatMap((fo) => fo.assets.map((asset) => [fo.id, asset]))
                    .forEach(v => this.addEntityLink(v[0], v[1]));
            });
    }
    getFunctionalRequirements(): void {
        this.frService.getFunctionalRequirements()
            .subscribe(frs => {
                this.frs = frs;

                this.frs.flatMap((fr) => fr.fos.map((fo) => [fr.id, fo]))
                    .forEach(v => this.addEntityLink(v[0], v[1]));
            });
    }

    getAssets(): void {
        this.assetService.getAssets()
            .subscribe(assets => {
                this.assets = assets;
            });
    }

    saveOrganisationalObjective(value: any): void {
        let checkExistingOOId = 'ooMainId' in value;
        let checkExistingOIRs = 'ooExistingOIRs' in value;
        let checkExistingFRs = 'ooExistingFRs' in value;
        let id = '50740e2b-a2b6-491d-a92f-babf0759067a'
        if(checkExistingOOId) {
            id = value.ooMainId;
        }
        let oo_name = this.selectedOOName;
        let oirs = value.oirs;
        let frs = value.frs;
        let existingOIRs = [];
        let existingFRs = [];

        if(checkExistingOIRs) {
            if(value.ooExistingOIRs.length > 0) {
                existingOIRs = value.ooExistingOIRs;
            }
        }

        if(checkExistingFRs) {
            if(value.ooExistingFRs.length > 0) {
                existingFRs = value.ooExistingFRs;
            }
        }

        if(oirs !== "") {
            existingOIRs.push(oirs);
        }

        if(frs !== "") {
            existingFRs.push(frs);
        }

        if(this.oo_all_selected_oir_values.length > 0) {
            existingOIRs = existingOIRs.filter( (e1: string) => !this.oo_all_selected_oir_values.includes( e1 ));
        }

        if(this.oo_all_selected_fr_values.length > 0) {
            existingFRs = existingFRs.filter( (e1: string) => !this.oo_all_selected_fr_values.includes( e1 ));
        }

        this.ooToSave = {
            id: id,
            name: oo_name,
            oirs: existingOIRs.flat(),
            frs: existingFRs.flat()
        }

        let oo = this.ooToSave

        this.ooService.save(oo)
            .subscribe(
                oo => this.objectives.push(oo),
                error => {
                    this.errorMessage = error.message;
                    console.error('There was an error!', error)
                }
            )
        /* Closing Modal after sending Save */
        this.modalService.dismissAll();
        /* Refreshing Screen */
        setTimeout(() =>
            {
                window.location.reload(true);
            },
            2000);
    }

    saveFunctionalRequirements(value: any): void {
        let checkExistingFRId = 'frMainId' in value;
        let checkExistingFOs = 'frExistingFOs' in value;
        let id = '1cfda9b4-1466-4695-8ab2-c19848fa0759'
        if(checkExistingFRId) {
            id = value.frMainId;
        }
        let frName = this.selectedFRText;
        let fos = value.fos;
        let existingFOs = [];

        if(checkExistingFOs) {
            if(value.frExistingFOs.length > 0) {
                existingFOs = value.frExistingFOs;
            }
        }

        if(fos !== "") {
            existingFOs.push(fos);
        }

        if(this.fr_all_selected_fo_values.length > 0) {
            existingFOs = existingFOs.filter( (e1: string) => !this.fr_all_selected_fo_values.includes( e1 ));
        }

        this.frToSave = {
            id: id,
            name: frName,
            fos: existingFOs.flat()
        }

        let fr = this.frToSave

        this.frService.save(fr)
            .subscribe(
                fr => this.frs.push(fr),
                error => {
                    this.errorMessage = error.message;
                    console.error('There was an error!', error);
                }
            );

        /* Closing Modal after sending Save */
        this.modalService.dismissAll();
        /* Refreshing Screen */
        setTimeout(() =>
            {
                window.location.reload(true);
            },
            2000);
    }

    saveFunctionalOutput(value: any): void {
        console.log("FO value received: ", value);

        let checkExistingFOId = 'foMainId' in value;
        let checkExistingFIRs = 'foExistingFIRs' in value;
        let checkExistingAssets = 'foExistingAssets' in value;
        let id = '0d72e486-74cf-45bc-b11e-cbb7c16a075f'
        if(checkExistingFOId) {
            id = value.foMainId;
        }
        let foDDId = this.selectedFOId;
        let foDDText = this.selectedFOText
        let firsText = value.firs;
        let assetText = value.assets;
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
        if(assetText !== "") {
            existingAssets.push(assetText);
        }

        if(this.fo_all_selected_fir_values.length > 0) {
            existingFIRs = existingFIRs.filter( (e1: string) => !this.fo_all_selected_fir_values.includes( e1 ));
        }

        if(this.fo_all_selected_asset_values.length > 0) {
            existingAssets = existingAssets.filter( (e1: string) => !this.fo_all_selected_asset_values.includes( e1 ));
        }

        this.foToSave = {
            id: id,
            data_dictionary_entry : {
                id: foDDId,
                text: foDDText
            },
            firs: existingFIRs.flat(),
            assets: existingAssets.flat()
        }

        let fo = this.foToSave

        this.foService.save(fo)
            .subscribe(
                fo => this.fos.push(fo),
                error => {
                    this.errorMessage = error.message;
                    console.error('There was an error!', error);
                }
            );

        /* Closing Modal after sending Save */
        this.modalService.dismissAll();
        /* Refreshing Screen */
        setTimeout(() =>
            {
                window.location.reload(true);
            },
            2000);
    }

    saveAsset(value: any): void {
        console.log("Value : ", value);
        let checkExistingAssedId = 'assetMainId' in value;
        let checkExistingAIRs = 'assetExistingAIRs' in value;
        let id = '0ee6c05d-c82c-494f-b13c-82f8d07590a1'
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
            airs: existingAIRs.flat()
        }

        let asset = this.assetToSave

        console.log("Final asset: ", asset);

        this.assetService.save(asset)
            .subscribe(
                asset => this.assets.push(asset),
                error => {
                    this.errorMessage = error.message;
                    console.error('There was an error!', error);
                }
            );

        /* Closing Modal after sending Save */
        this.modalService.dismissAll();
        /* Refreshing Screen */
        setTimeout(() =>
            {
                window.location.reload(true);
            },
            2000);

    }

    getOOsValue(): void {
        this.selectedOOName = (<HTMLInputElement>document. getElementById("ooId")).value;
        /* Log */
        console.log("Selected OO Name : ",this.selectedOOName);
    }

    getFRValue(): void  {
        this.selectedFRText =  (<HTMLInputElement>document. getElementById("frName")).value;
        /* Log */
        console.log("Functional Requirement Selected : ",this.selectedFRText);
    }

    getFOSValue(): void {
        let foData = this.selectedFO.split("-");
        this.selectedFOId = foData[0].trim();
        this.selectedFOText = foData[1].trim();
        /* Log */
        console.log("ID : ", foData[0].trim());
        console.log("Text : ", foData[1].trim());
        console.log("Asset Selected : ",this.selectedFO.trim);
    }

    getAssetValue(): void  {
        let assetData = this.selectedAsset.split("-");
        this.selectedAssetDDId = assetData[0].trim();
        this.selectedAssetDDText = assetData[1].trim();
        /* Log */
        console.log("ID : ", assetData[0].trim());
        console.log("Text : ", assetData[1].trim());
        console.log("Asset Selected : ",this.selectedAsset.trim);
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

    onOOOIRChange(value: string): void {
        if (this.oo_all_selected_oir_values.includes(value)) {
            this.oo_all_selected_oir_values = this.oo_all_selected_oir_values.filter((item) => item !== value);
        } else {
            this.oo_all_selected_oir_values.push(value);
        }
        console.log(this.oo_all_selected_oir_values);
    }

    onOOFRChange(value: string): void {
        if (this.oo_all_selected_fr_values.includes(value)) {
            this.oo_all_selected_fr_values = this.oo_all_selected_fr_values.filter((item) => item !== value);
        } else {
            this.oo_all_selected_fr_values.push(value);
        }
        console.log(this.oo_all_selected_fr_values);
    }

    ngOnInit(): void {
    }

    private addEntityLink(fromElementId: string, toElementId: string): boolean {
        let entityLines = this.entityLinks.get(fromElementId)
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

        this.assetDdeService.getAssetDataDictionaryEntries().subscribe(ddes => console.log("Got", ddes.length, "asset data dictionary entries"));
        this.foDdeService.getFunctionalObjectiveDataDictionaryEntries().subscribe(ddes => console.log("Got", ddes.length, "fo data dictionary entries"));

        this.getAssets();
        this.getFunctionalObjectives();
        this.getFunctionalRequirements();
        this.getOrganisationalObjectives();

        console.log('Objectives', this.objectives);
        console.log('FRs', this.frs);
        console.log('FOs', this.fos);
        console.log('Assets', this.assets);
    }

    ooLinkStart(event: DragEvent): boolean {
        // @ts-ignore
        event.dataTransfer.setData('application/json', JSON.stringify({sourceType: 'organisational_objective', source: event.target.id}));
        return true;
    }

    foLinkStart(event: DragEvent): boolean {
        // @ts-ignore
        event.dataTransfer.setData('application/json', JSON.stringify({sourceType: 'functional_objective', source: event.target.id}));
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
        if (data.sourceType === 'organisational_objective') {
            const startObjective = this.objectives.find(objective => objective.id === data.source);
            if (startObjective !== undefined) {
                if (startObjective.frs.find(fr => fr === targetId) === undefined) {
                    startObjective.frs.push(targetId);

                    console.log('Updated OO', startObjective);
                    this.ooService.save(startObjective).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startObjective.frs = startObjective.frs.filter(fr => fr !== targetId);
                        console.log("Failed to save new link", startObjective);
                    });
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
                    this.frService.save(startFr).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startFr.fos = startFr.fos.filter(fo => fo !== targetId);
                        console.log("Failed to save new link", startFr);
                    });
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
        if (data.sourceType === 'functional_objective') {
            const startObjective = this.fos.find(objective => objective.id === data.source);
            if (startObjective !== undefined) {
                if (startObjective.assets.find(asset => asset === targetId) === undefined) {
                    startObjective.assets.push(targetId);
/*

THIS IS NEED TO CHECK THOROUGHLY WITH MY CODE

 */
                    console.log('Updated FO', startObjective);
                    this.foService.save(startObjective).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startObjective.assets = startObjective.assets.filter(asset => asset !== targetId);
                        console.log("Failed to save new link", startObjective);
                    });
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
            this.objectives.filter(obj => obj.frs.find(fr => fr === id)).forEach(obj => links.push(obj.id));
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

    getObjectiveLinks(id: string): string[] {
        const links: string[] = [];
        const linkedFRs: string[] = this.objectives.filter(objective => objective.id === id)
            .flatMap(objective => objective.frs);
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

    mouseEnterObjective(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getObjectiveLinks(event.target.id);
            console.log('Objective', event.target.id, 'links to', links);
            // @ts-ignore
            links.forEach(link => document.getElementById(link).style.backgroundColor = IRGraphComponent.darkblue);
            // @ts-ignore
            document.getElementById(event.target.id).style.backgroundColor = IRGraphComponent.darkblue;
        }
    }

    mouseLeaveObjective(event: any): void {
        if (event.target !== null && event.target.id !== null) {
            const links = this.getObjectiveLinks(event.target.id);
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

    modalClose(value: String): void {
        this.modalService.dismissAll();
        // modal.close('Cancel')
    }

    reloadWindow(): void {
        window.location.reload(true);
    }

    logout(): void {
        this.authService.logout().subscribe();
        this.reloadWindow();
    }

    clearField(value: string): void {
        // @ts-ignore
        document.getElementById(value).innerHTML = "";
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
}

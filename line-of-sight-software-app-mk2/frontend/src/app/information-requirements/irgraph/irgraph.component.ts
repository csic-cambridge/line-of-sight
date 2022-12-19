import {AfterViewInit, Component, Inject, OnInit} from '@angular/core';
import {Asset} from '../../types/asset';
import {ProjectOrganisationalObjective} from '../../types/project-organisational-objective';
import {FunctionalRequirement} from '../../types/functional-requirement';
import {FunctionalOutput} from '../../types/functional-output';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DOCUMENT} from '@angular/common';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';
import { Project } from 'src/app/types/project';
import {BehaviorSubject, combineLatest} from 'rxjs';
import {IrGraphDialogs} from '../../types/ir-graph-dialogs';
import {AppToastService} from '../../services/app-toast.service';
import {BaseIoService} from '../../services/base/base-io-service';
import {IrgraphOoDialogComponent} from './irgraph-oo-dialog/irgraph-oo-dialog.component';
import {IrgraphFrDialogComponent} from './irgraph-fr-dialog/irgraph-fr-dialog.component';
import {IrgraphFoDialogComponent} from './irgraph-fo-dialog/irgraph-fo-dialog.component';
import {IrgraphAssetDialogComponent} from './irgraph-asset-dialog/irgraph-asset-dialog.component';
import {WsService} from '../../services/ws.service';
import {BasePermissionService} from '../../services/base/base-permission-service';
import {BaseProjectDataService} from '../../services/base/base-project-data-service';
import {BaseAssetService} from '../../services/base/base-asset-service';
import {BaseFunctionalOutputService} from '../../services/base/base-functional-output-service';
import {BaseFunctionalRequirementService} from '../../services/base/base-functional-requirement-service';
import {BaseProjectOrganisationalObjectiveService} from '../../services/base/base-project-organisational-objective-service';
import {BaseAssetDictionaryEntryService} from '../../services/base/base-asset-dictionary-entry-service';
import {BaseFunctionalOutputDictionaryEntryService} from '../../services/base/base-functional-output-dictionary-entry-service';
import {environment} from '../../../environments/environment';
import {CookieService} from 'ngx-cookie-service';
import {map} from 'rxjs/operators';
import * as pako from 'pako';
import {DashboardDialog} from '../../types/dashboard-dialog';
import {ClearShowcaseDialogComponent} from '../../components/login/clear-showcase-dialog/clear-showcase-dialog.component';
import {ConfirmProjectImportDialogComponent} from './confirm-project-import-dialog/confirm-project-import-dialog.component';

@Component({
    selector: 'app-irgraph',
    templateUrl: './irgraph.component.html',
    styleUrls: ['./irgraph.component.scss']
})
export class IRGraphComponent implements OnInit {
    constructor(@Inject(DOCUMENT) private document: any,
                private pooService: BaseProjectOrganisationalObjectiveService,
                private frService: BaseFunctionalRequirementService,
                private foService: BaseFunctionalOutputService,
                private assetService: BaseAssetService,
                public toastr: AppToastService,
                private ioService: BaseIoService,
                private assetDdeService: BaseAssetDictionaryEntryService,
                private foDdeService: BaseFunctionalOutputDictionaryEntryService,
                private modalService: NgbModal,
                private wsService: WsService,
                private projectDataService: BaseProjectDataService,
                public permissionService: BasePermissionService,
                private cookieService: CookieService) {

        this.poos = this.pooService.projectOrganisationalObjectives;
        this.frs = this.frService.functionalRequirements;
        this.fos = this.foService.functionalOutputs;
        this.assets = this.assetService.assets;
    }

    static darkblue = '#4974a7';
    public env = environment;
    showIRs = this.cookieService.get('app-irgraph-showIRs') === '0' ? false : true;
    showSelected: boolean = this.cookieService.get('app-irgraph-showSelected') === '1' ? true : false;
    isSinglePooClick = true;
    isSingleFOClick = true;
    isSingleAssetClick = true;
    isSingleFRClick = true;
    activeId: string | undefined;
    activeDialog = IrGraphDialogs.NONE;
    public dialogTypes = IrGraphDialogs;

    project: Project = {
        id: '',
        name: '',
        fo_dd_id: '',
        asset_dd_id: ''
    };
    optionsModel: Array<number> = [];
    private errorMessage: ((error: any) => void) | null | undefined;

    poos: BehaviorSubject<ProjectOrganisationalObjective[]>;
    frs: BehaviorSubject<FunctionalRequirement[]>;
    assets: BehaviorSubject<Asset[]>;
    fos: BehaviorSubject<FunctionalOutput[]>;

    fosArray: Array<FunctionalOutput> = [];
    frsArray: Array<FunctionalRequirement> = [];
    assetsArray: Array<Asset> = [];
    foForSelect: FunctionalOutput | undefined;
    frToSave: FunctionalRequirement | undefined;
    assetForSelect: Asset | undefined;

    selectedAssetArray: Array<string> = [];

    foLinkedToAssetArray: Array<string> = [];
    frLinkedToFOArray: Array<string> = [];
    pooLinkedToFRArray: Array<string> = [];
    entityLinks = new Map<string, Set<string>>();

    closeResult: string | undefined;

    private static getDismissReason(reason: any): string {
        if (reason === ModalDismissReasons.ESC) {
            return 'by pressing ESC';
        } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
            return 'by clicking on a backdrop';
        } else {
            return `with: ${reason}`;
        }
    }

    ngOnInit(): void {
        this.wsService.ReloadProject.subscribe(x => {
            setTimeout(() => {
                this.reloadLinks();
            }, 500);
        });

        this.readProject();

        this.loadEntities();
    }

    getProjectOrganisationalObjectives(): void {
        this.pooService.loadProjectOrganisationalObjectives(this.project.id);
        this.pooService.getProjectOrganisationalObjectives(this.project.id)
            .subscribe(poos => {
                poos.flatMap((poo) => poo.frs.map((fr) => [poo.id, fr]))
                    .forEach(v => this.addEntityLink(v[0], v[1]));
            });
    }

    private loadEntities(): void {
        const pooRequest = this.pooService.getProjectOrganisationalObjectives(this.project.id);
        const foRequest = this.foService.getFunctionalOutputs(this.project.id);
        const assetRequest = this.assetService.getAssets(this.project.id);
        const frRequest = this.frService.getFunctionalRequirements(this.project.id);
        combineLatest([pooRequest, foRequest, assetRequest, frRequest]).subscribe(results => {
            this.poos.next(results[0]);
            this.fos.next(results[1]);
            this.assets.next(results[2]);
            this.frs.next(results[3]);
            this.reloadLinks();
        });
    }

    foLinkedToAssetCheck(assetDDId: string, selectedAssetId: string, foAssetLength: any, assetId: string, foAsset: string): boolean {
        if (assetDDId === selectedAssetId && foAssetLength > 0 && assetId === foAsset) {
            this.foLinkedToAssetArray.push(foAsset);
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
        (event.dataTransfer as DataTransfer).setData('application/json', JSON.stringify(
            {sourceType: 'project_organisational_objective', source: (event.target as HTMLElement).id}));
        return true;
    }

    foLinkStart(event: DragEvent): boolean {
        (event.dataTransfer as DataTransfer).setData('application/json', JSON.stringify({sourceType: 'functional_output',
            source: (event.target as HTMLElement).id}));
        return true;
    }

    frLinkStart(event: DragEvent): boolean {
        (event.dataTransfer as DataTransfer).setData('application/json', JSON.stringify({sourceType: 'functional_requirement',
            source: (event.target as HTMLElement).id}));
        return true;
    }

    linkToFR(event: DragEvent): boolean {
        event.preventDefault();
        const data = JSON.parse((event.dataTransfer as DataTransfer).getData('application/json'));
        const targetId = (event.target as HTMLElement).id;

        if (data.sourceType === 'project_organisational_objective') {
            const startPOO = this.poos.value.find(poo => poo.id === data.source);
            if (startPOO !== undefined) {
                if (startPOO.frs.find(fr => fr === targetId) === undefined) {
                    startPOO.frs.push(targetId);
                    this.pooService.save(startPOO, this.project.id).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startPOO.frs = startPOO.frs.filter(fr => fr !== targetId);
                        this.handleRestError(error);
                    });
                }
            }
        }
        // Didn't manage to link it, return false;
        return false;
    }

    linkToFO(event: DragEvent): boolean {
        event.preventDefault();
        const data = JSON.parse((event.dataTransfer as DataTransfer).getData('application/json'));
        const targetId = (event.target as HTMLElement).id;

        if (data.sourceType === 'functional_requirement') {
            const startFr = this.frs.value.find(fr => fr.id === data.source);
            if (startFr !== undefined) {
                if (startFr.fos.find(fo => fo === targetId) === undefined) {
                    startFr.fos.push(targetId);
                    this.frService.save(startFr, this.project.id).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startFr.fos = startFr.fos.filter(fo => fo !== targetId);
                        this.handleRestError(error);
                    });
                }
            }
        }
        // Didn't manage to link it, return false;
        return false;
    }

    linkToAsset(event: DragEvent): boolean {
        event.preventDefault();
        const data = JSON.parse((event.dataTransfer as DataTransfer).getData('application/json'));
        const targetId = (event.target as HTMLElement).id;
        if (data.sourceType === 'functional_output') {
            const startFO = this.fos.value.find(objective => objective.id === data.source);
            if (startFO !== undefined) {
                if (startFO.assets.find(asset => asset === targetId) === undefined) {
                    startFO.assets.push(targetId);
                    this.foService.save(startFO, this.project.id).subscribe(value => {
                        this.addEntityLink(data.source, targetId);
                    }, error => {
                        startFO.assets = startFO.assets.filter(asset => asset !== targetId);
                        this.handleRestError(error);
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
            const linkedFRs = this.frs.value.filter(fr => fr.fos.find(fo => fo === id));
            linkedFRs.forEach(fr => links.push(fr.id));
            linkedFRs.flatMap(fr => this.getFRLinks(fr.id, true, false)).forEach(fr => links.push(fr));
        }
        if (right) {
            this.fos.value.filter(fo => fo.id === id).flatMap(fo => fo.assets).forEach(asset => links.push(asset));
        }
        return links;
    }

    getFRLinks(id: string, left: boolean, right: boolean): string[] {
        const links: string[] = [];
        if (right) {
            const linkedFOs = this.frs.value.filter(fr => fr.id === id).flatMap(fr => fr.fos);
            linkedFOs.forEach(fo => links.push(fo));
            linkedFOs.flatMap(fo => this.getFOLinks(fo, false, true)).forEach(foLinks => links.push(foLinks));
        }
        if (left) {
            this.poos.value.filter(obj => obj.frs.find(fr => fr === id)).forEach(obj => links.push(obj.id));
        }
        return links;
    }

    getAssetLinks(id: string): string[] {
        const links: string[] = [];
        const linkedFOs: string[] = this.fos.value.filter(fo => fo.assets.find(asset => asset === id)).map(fo => fo.id);
        linkedFOs.forEach(fo => links.push(fo));
        linkedFOs.flatMap(fo => this.getFOLinks(fo, true, false)).forEach(foLink => links.push(foLink));

        return links;
    }

    getPOOLinks(id: string): string[] {
        const links: string[] = [];
        const linkedFRs: string[] = this.poos.value.filter(poo => poo.id === id)
            .flatMap(poo => poo.frs);
        linkedFRs.forEach(fr => links.push(fr));
        linkedFRs.flatMap(fr => this.getFRLinks(fr, false, true)).forEach(frLink => links.push(frLink));

        return links;
    }

    mouseEnterFO(id: string): void {
        // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getFOLinks(id, true, true);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.add('on'));
        // (document.getElementById(id) as HTMLElement).classList.add('on');
    }

    mouseLeaveFO(id: string): void {
        // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getFOLinks(id, true, true);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.remove('on'));
        // (document.getElementById(id) as HTMLElement).classList.remove('on');
    }

    selectFO(id: string): void {
        this.isSingleFOClick = true;
        setTimeout(() => {
            if (this.isSingleFOClick){
                const selected = (document.getElementById(id) as HTMLElement).classList.contains('selected');
                const element = document.getElementById('leader-line-container');
                if (element !== null) {
                    const matches = element.querySelectorAll('.card-header.selected');
                    matches.forEach(x => x.classList.remove('selected'));
                }
                if (!selected) {
                    const links = this.getFOLinks(id, true, true);
                    links.forEach(link => (document.getElementById(link) as HTMLElement).classList.add('selected'));
                    (document.getElementById(id) as HTMLElement).classList.add('selected');
                }
                this.reloadLinks();
            }
        }, 250);
    }

    mouseEnterAsset(id: string): void {
        // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getAssetLinks(id);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.add('on'));
        // (document.getElementById(id) as HTMLElement).classList.add('on');
    }

    mouseLeaveAsset(id: string): void {
        // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getAssetLinks(id);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.remove('on'));
        // (document.getElementById(id) as HTMLElement).classList.remove('on');
    }

    selectAsset(id: string): void {
        this.isSingleAssetClick = true;
        setTimeout(() => {
            if (this.isSingleAssetClick) {
                const selected = (document.getElementById(id) as HTMLElement).classList.contains('selected');
                const element = document.getElementById('leader-line-container');
                if (element !== null) {
                    const matches = element.querySelectorAll('.card-header.selected');
                    matches.forEach(x => x.classList.remove('selected'));
                }
                if (!selected) {
                    const links = this.getAssetLinks(id);
                    links.forEach(link => (document.getElementById(link) as HTMLElement).classList.add('selected'));
                    (document.getElementById(id) as HTMLElement).classList.add('selected');
                }
                this.reloadLinks();

            }
        }, 250);
    }

    mouseEnterPOO(id: string): void {
        // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getPOOLinks(id);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.add('on'));
        // (document.getElementById(id) as HTMLElement).classList.add('on');
    }

    mouseLeavePOO(id: string): void {
        // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getPOOLinks(id);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.remove('on'));
        // (document.getElementById(id) as HTMLElement).classList.remove('on');
    }

    selectPoo(id: string): void {


        this.isSinglePooClick = true;
        setTimeout(() => {
            if (this.isSinglePooClick){
                const selected = (document.getElementById(id) as HTMLElement).classList.contains('selected');
                const element = document.getElementById('leader-line-container');
                if (element !== null) {
                    const matches = element.querySelectorAll('.card-header.selected');
                    matches.forEach(x => x.classList.remove('selected'));
                }
                if (!selected) {
                    const links = this.getPOOLinks(id);
                    links.forEach(link => (document.getElementById(link) as HTMLElement).classList.add('selected'));
                    (document.getElementById(id) as HTMLElement).classList.add('selected');
                }
                this.reloadLinks();
            }
        }, 250);
    }

    mouseEnterFR(id: string): void {
        // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getFRLinks(id, true, true);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.add('on'));
        // (document.getElementById(id) as HTMLElement).classList.add('on');
    }

    mouseLeaveFR(id: string): void {
        // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getFRLinks(id, true, true);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.remove('on'));
        // (document.getElementById(id) as HTMLElement).classList.remove('on');
    }

    selectFR(id: string): void {

        this.isSingleFRClick = true;
        setTimeout(() => {
            if (this.isSingleFRClick){
                const selected = (document.getElementById(id) as HTMLElement).classList.contains('selected');
                const element = document.getElementById('leader-line-container');
                if (element !== null) {
                    const matches = element.querySelectorAll('.card-header.selected');
                    matches.forEach(x => x.classList.remove('selected'));
                }
                if (!selected) {
                    const links = this.getFRLinks(id, true, true);
                    links.forEach(link => (document.getElementById(link) as HTMLElement).classList.add('selected'));
                    (document.getElementById(id) as HTMLElement).classList.add('selected');
                }
                this.reloadLinks();
            }
        }, 250);
    }

    open(content: any): void {
        this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', centered: true}).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
        }, (reason) => {
            this.closeResult = `Dismissed ${IRGraphComponent.getDismissReason(reason)}`;
        });
    }

    editPoo(poo: any): void {

        this.isSinglePooClick = false;
        const modalRef = this.modalService.open(IrgraphOoDialogComponent, { scrollable: true, centered: true, size: 'lg' });
        modalRef.componentInstance.selectedPoo = poo;
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.frs = this.frs.value;
        modalRef.componentInstance.poos = this.poos.value;

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event.updated) {
                this.pooService.getProjectOrganisationalObjectives(this.project.id).subscribe(x => {
                    this.poos.next(x);
                    this.reloadLinks();
                });
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }

    editFr(fr: any): void {

        this.isSingleFRClick = false;
        const modalRef = this.modalService.open(IrgraphFrDialogComponent, { scrollable: true, centered: true, size: 'lg' });
        modalRef.componentInstance.selectedFr = fr;
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.frs = this.frs.value;
        modalRef.componentInstance.poos = this.poos.value;
        modalRef.componentInstance.fos = this.fos.value;

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event.updated) {
                this.frService.getFunctionalRequirements(this.project.id)
                    .subscribe(frs => {
                        this.frs.next(frs);
                        this.reloadLinks();
                    });
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }


    editFo(fo: any): void {

        this.isSingleFOClick = false;
        const modalRef = this.modalService.open(IrgraphFoDialogComponent, { scrollable: true, centered: true, size: 'lg' });
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.frs = this.frs.value;
        modalRef.componentInstance.fos = this.fos.value;
        modalRef.componentInstance.assets = this.assets.value;
        modalRef.componentInstance.foDataDictionary = this.foDdeService.entries$.value;
        modalRef.componentInstance.selectedFO = fo;

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event.updated) {
                this.foService.getFunctionalOutputs(this.project.id)
                    .subscribe(fos => {
                        this.fos.next(fos);
                        this.reloadLinks();
                    });
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }

    editAsset(asset: any): void {

        this.isSingleAssetClick = false;
        const modalRef = this.modalService.open(IrgraphAssetDialogComponent, { scrollable: true, centered: true, size: 'lg' });
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.fos = this.fos.value;
        modalRef.componentInstance.assets = this.assets.value;
        modalRef.componentInstance.selectedAsset = asset;

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event.updated) {
                this.assetService.getAssets(this.project.id)
                    .subscribe(assets => {
                        this.assets.next(assets);
                        this.reloadLinks();
                    });
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }

    new(dialog: IrGraphDialogs): void{
        switch (dialog) {
            case IrGraphDialogs.FO_DIALOG: {
                const data = {id: ''} as FunctionalOutput;
                data.firs = [];
                data.assets = [];
                data.data_dictionary_entry = {} as DataDictionaryEntry;
                this.editFo(data);
                break;
            }
            case IrGraphDialogs.ASSET_DIALOG: {
                const data = {id: ''} as Asset;
                data.airs = [];
                data.data_dictionary_entry = {} as DataDictionaryEntry;
                this.editAsset(data);
                break;
            }
            case IrGraphDialogs.OO_DIALOG: {
                const data = {id: ''} as ProjectOrganisationalObjective;
                data.oirs = [];
                data.deleted_oirs = [];
                data.frs = [];
                this.editPoo(data);
                break;
            }
            default: {
                this.editFr({id: ''} as FunctionalRequirement);
                break;
            }
        }
    }

    readProject(): void {
        const storedProject = this.projectDataService.getProject();
        if (storedProject != null) {
            this.project = storedProject;
            this.foDdeService.load(this.project.fo_dd_id);
            this.assetDdeService.load(this.project.asset_dd_id);
        }
    }

    handleRestError(error: any): void {
        console.log('handleRestError', error);
        console.log('handleRestError', error.error);
        console.log('handleRestError', error.error.error);
        this.errorMessage = error.message;
        if (error.status === 403) {
            window.alert('You do not have permission to access the function you have requested');
        }
        else if (error.error.forUi === true) {
            this.toastr.show(error.error,
                { classname: 'bg-danger text-light', delay: 5000 });
        }
        else{
            this.toastr.show(error.error.error,
                { classname: 'bg-danger text-light', delay: 5000 });
        }
    }

    reloadLinks(): void {
        this.entityLinks.forEach(x => x.clear());

        this.poos.value.flatMap((poo) => poo.frs.map((fr) => [poo.id, fr]))
            .forEach(v => {
                setTimeout(() => {
                    this.addEntityLink(v[0], v[1]);
                }, 1500);
            });

        this.fos.value.flatMap((fo) => fo.assets.map((asset) => [fo.id, asset]))
            .forEach(v => {
                setTimeout(() => {
                    this.addEntityLink(v[0], v[1]);
                }, 1500);
            });

        this.frs.value.flatMap((fr) => fr.fos.map((fo) => [fr.id, fo]))
            .forEach(v => {
                setTimeout(() => {
                    this.addEntityLink(v[0], v[1]);
                }, 1500);
            });
    }

    onDialogError($event: any): void {
        this.handleRestError($event);
    }

    track($event: any): void {
        console.log($event);
    }

    importFirs($event: Event): void {
        const target: DataTransfer = $event.target as unknown as DataTransfer;
        const reader: FileReader = new FileReader();

        reader.onload = (result: any) => {
            this.ioService.importFirs(result.target.result, this.project.id)
                .then((data) => {
                    this.toastr.show('Import Project Firs Completed.',
                        { classname: 'bg-success text-light', delay: 5000 });

                    this.foService.getFunctionalOutputs(this.project.id)
                        .subscribe(fos => {
                            this.fos.next(fos);
                            this.reloadLinks();
                        });
                })
                .catch(err => {
                    this.toastr.show(JSON.parse(err.error).error,
                        { classname: 'bg-danger text-light', delay: 5000 });
                });
        };
        reader.readAsBinaryString(target.files[0]);
    }

    importAirs(evt: Event): void {
        const target: DataTransfer = evt.target as unknown as DataTransfer;
        const reader: FileReader = new FileReader();

        reader.onload = (result: any) => {
            this.ioService.importAirs(result.target.result, this.project.id).then((data) => {
                this.toastr.show('Import Project AIRs Completed.',
                    { classname: 'bg-success text-light', delay: 5000 });

                this.assetService.getAssets(this.project.id)
                    .subscribe(assets => {
                        this.assets.next(assets);
                        this.reloadLinks();
                    });
            }).catch(err => {
                this.toastr.show(JSON.parse(err.error).error,
                    { classname: 'bg-danger text-light', delay: 5000 });
            });
        };
        reader.readAsBinaryString(target.files[0]);
    }

    toggleShowIRs(): void {
        this.showIRs = !this.showIRs;
        this.cookieService.set('app-irgraph-showIRs', this.showIRs ? '1' : '0');
        this.reloadLinks();
    }
    toggleShowSelected(): void {
        this.showSelected = !this.showSelected;
        this.cookieService.set('app-irgraph-showSelected', this.showSelected ? '1' : '0');
        this.reloadLinks();
    }

    hideIR(id: any, evt: Event): void {
        evt.preventDefault();
        evt.stopPropagation();
        (document.getElementById(id) as HTMLElement).classList.toggle('hide-ir');
        this.reloadLinks();
    }

    showHide(id: string): string {
        return (document.getElementById(id) as HTMLElement)
            .classList.contains('hide-ir') ?  'Show' : 'Hide';
    }

    importProject(evt: Event): void {
        const target: DataTransfer = evt.target as unknown as DataTransfer;
        const reader: FileReader = new FileReader();
        const file = target.files[0];
        reader.onload = (e: any) => {
            if (this.foService.functionalOutputs.value.length > 0 ||
                this.frService.functionalRequirements.value.length > 0 ||
                this.assetService.assets.value.length > 0 ||
                this.pooService.projectOrganisationalObjectives.value.length > 0){
                const modalRef = this.modalService.open(ConfirmProjectImportDialogComponent,
                    { scrollable: true, centered: true, size: 'lg' });

                modalRef.componentInstance.closed.subscribe(($event: any) => {
                    modalRef.close();
                    if ($event) {

                        this.ioService.importProject(e.target.result).then(msg => {
                            this.toastr.show(msg,
                                { classname: 'bg-success text-light', delay: 5000 });
                            const proj = this.projectDataService.getProject();
                            proj.name = file.name.replace('.los_cs', '');
                            this.projectDataService.setProject(proj);
                            this.reloadLinks();
                        }, err => {
                            this.toastr.show(err,
                                { classname: 'bg-danger text-light', delay: 5000 });
                        });
                    }

                });
            } else {
                this.ioService.importProject(e.target.result).then(msg => {
                    this.toastr.show(msg,
                        { classname: 'bg-success text-light', delay: 5000 });
                    const proj = this.projectDataService.getProject();
                    proj.name = file.name.replace('.los_cs', '');
                    this.projectDataService.setProject(proj);
                    this.reloadLinks();
                }, err => {
                    this.toastr.show(err,
                        { classname: 'bg-danger text-light', delay: 5000 });
                });
            }

        };
        reader.readAsBinaryString(target.files[0]);
    }

    export(): void {
        this.ioService.exportProject(this.project.id)
            .then(data => {
                const blob = new Blob([data], {type: 'application/octet-stream'});
                const fileLink = document.createElement('a');
                fileLink.href = window.URL.createObjectURL(blob);
                fileLink.download = `${this.project.name.replace(/[^a-zA-Z0-9]/g, '')}.los_cs`;
                fileLink.click();

                this.toastr.show('Export Project Completed',
                    { classname: 'bg-success text-light', delay: 5000 });
            }, err => {
                this.toastr.show('Export Project Failed',
                    { classname: 'bg-error text-light', delay: 5000 });
            });
    }
    clearData(): void {
        const modalRef = this.modalService.open(ClearShowcaseDialogComponent, { scrollable: true, centered: true, size: 'lg' });

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event) {
                this.pooService.projectOrganisationalObjectives.next([]);
                this.frService.functionalRequirements.next([]);
                this.foService.functionalOutputs.next([]);
                this.assetService.assets.next([]);
                this.reloadLinks();
            }

        });
    }
}

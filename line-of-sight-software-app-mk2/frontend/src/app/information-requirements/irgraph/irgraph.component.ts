import {AfterViewInit, Component, Inject} from '@angular/core';
import {Asset} from '../../types/asset';
import {ProjectOrganisationalObjective} from '../../types/project-organisational-objective';
import {FunctionalRequirement} from '../../types/functional-requirement';
import {FunctionalOutput} from '../../types/functional-output';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AssetService} from '../../services/asset.service';
import {ProjectOrganisationalObjectiveService} from '../../services/project-organisational-objective.service';
import {FunctionalRequirementService} from '../../services/functional-requirement.service';
import {FunctionalOutputService} from '../../services/functional-output.service';
import {DOCUMENT} from '@angular/common';
import {AssetDataDictionaryEntryService} from '../../services/asset-data-dictionary-entry.service';
import {FunctionalOutputDataDictionaryEntryService} from '../../services/functional-output-data-dictionary-entry.service';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';
import { ProjectDataService } from 'src/app/services/project-data.service';
import { Project } from 'src/app/types/project';
import {PermissionService} from '../../services/permission.service';
import {BehaviorSubject} from 'rxjs';
import {IrGraphDialogs} from '../../types/ir-graph-dialogs';
import {DialogClosedParam} from '../../types/dialog-closed-param';
import {AppToastService} from '../../services/app-toast.service';
import {BaseIoService} from '../../services/base/base-io-service';
import {IrgraphOoDialogComponent} from './irgraph-oo-dialog/irgraph-oo-dialog.component';
import {IrgraphFrDialogComponent} from './irgraph-fr-dialog/irgraph-fr-dialog.component';
import {IrgraphFoDialogComponent} from './irgraph-fo-dialog/irgraph-fo-dialog.component';
import {IrgraphAssetDialogComponent} from './irgraph-asset-dialog/irgraph-asset-dialog.component';

@Component({
    selector: 'app-irgraph',
    templateUrl: './irgraph.component.html',
    styleUrls: ['./irgraph.component.scss']
})
export class IRGraphComponent implements AfterViewInit {
    constructor(@Inject(DOCUMENT) private document: any,  private assetService: AssetService,
                private pooService: ProjectOrganisationalObjectiveService,
                private frService: FunctionalRequirementService,
                private foService: FunctionalOutputService,
                public toastr: AppToastService,
                private ioService: BaseIoService,
                private assetDdeService: AssetDataDictionaryEntryService,
                private foDdeService: FunctionalOutputDataDictionaryEntryService,
                private modalService: NgbModal,
                private projectDataService: ProjectDataService,
                public permissionService: PermissionService) {
    }

    static darkblue = '#4974a7';

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

    poos: Array<ProjectOrganisationalObjective> = [];
    frs: Array<FunctionalRequirement> = [];
    frsArray: Array<FunctionalRequirement> = [];
    fos: Array<FunctionalOutput> = [];
    fosArray: Array<FunctionalOutput> = [];

    assets: BehaviorSubject<Asset[]> = new BehaviorSubject<Asset[]>([]);
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

    ngAfterViewInit(): void {
        this.readProject();
        this.getAssets();
        this.getFunctionalOutputs();
        this.getFunctionalRequirements();
        this.getProjectOrganisationalObjectives();

        const elmWrapper = document.getElementById('wrapper');
        const elmContainer = document.getElementById('topLevelBody');
        if (elmWrapper) {
            const rectWrapper = elmWrapper.getBoundingClientRect();
            elmWrapper.style.transform = 'translate(-' +
                (rectWrapper.left + pageXOffset) + 'px, -' +
                (rectWrapper.top + pageYOffset) + 'px)';
        }

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

    getAssets(): void {
        this.assetService.getAssets(this.project.id)
            .subscribe(assets => {
                this.assets.next(assets);
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

    open(content: any): void {
        this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', centered: true}).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
        }, (reason) => {
            this.closeResult = `Dismissed ${IRGraphComponent.getDismissReason(reason)}`;
        });
    }

    editPoo(poo: any): void {
        const modalRef = this.modalService.open(IrgraphOoDialogComponent, { scrollable: true, centered: true, size: 'lg' });
        modalRef.componentInstance.selectedPoo = poo;
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.frs = this.frs;
        modalRef.componentInstance.poos = this.poos;

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event.updated) {
                this.pooService.getProjectOrganisationalObjectives(this.project.id)
                    .subscribe(poos => {
                        this.poos = poos;
                        this.reloadLinks();
                    });
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }

    editFr(fr: any): void {
        const modalRef = this.modalService.open(IrgraphFrDialogComponent, { scrollable: true, centered: true, size: 'lg' });
        modalRef.componentInstance.selectedFr = fr;
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.frs = this.frs;
        modalRef.componentInstance.poos = this.poos;
        modalRef.componentInstance.fos = this.fos;

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event.updated) {
                this.frService.getFunctionalRequirements(this.project.id)
                    .subscribe(frs => {
                        this.frs = frs;
                        this.reloadLinks();
                    });
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }


    editFo(fo: any): void {
        const modalRef = this.modalService.open(IrgraphFoDialogComponent, { scrollable: true, centered: true, size: 'lg' });
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.frs = this.frs;
        modalRef.componentInstance.fos = this.fos;
        modalRef.componentInstance.assets = this.assets.value;
        modalRef.componentInstance.foDataDictionary = this.foDdeService.entries$.value;
        modalRef.componentInstance.selectedFO = fo;

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event.updated) {
                this.foService.getFunctionalOutputs(this.project.id)
                    .subscribe(fos => {
                        this.fos = fos;
                        this.reloadLinks();
                    });
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }

    editAsset(asset: any): void {

        const modalRef = this.modalService.open(IrgraphAssetDialogComponent, { scrollable: true, centered: true, size: 'lg' });
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.fos = this.fos;
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
                this.editFo({id: ''} as FunctionalOutput);
                break;
            }
            case IrGraphDialogs.ASSET_DIALOG: {
                this.editAsset({id: ''} as Asset);
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
        this.errorMessage = error.message;
        if (error.status === 403) {
            window.alert('You do not have permission to access the function you have requested');
        }
        else if (error.error.forUi === true) {
            this.toastr.show(error.error.error,
                { classname: 'bg-danger text-light', delay: 5000 });
        }
        else{
            this.toastr.show(error.error.error,
                { classname: 'bg-danger text-light', delay: 5000 });
        }
    }

    reloadLinks(): void {
        this.entityLinks.forEach(x => x.clear());

        this.poos.flatMap((poo) => poo.frs.map((fr) => [poo.id, fr]))
            .forEach(v => {
                setTimeout(() => {
                    this.addEntityLink(v[0], v[1]);
                }, 1000);
            });

        this.fos.flatMap((fo) => fo.assets.map((asset) => [fo.id, asset]))
            .forEach(v => {
                setTimeout(() => {
                    this.addEntityLink(v[0], v[1]);
                }, 1000);
            });

        this.frs.flatMap((fr) => fr.fos.map((fo) => [fr.id, fo]))
            .forEach(v => {
                setTimeout(() => {
                    this.addEntityLink(v[0], v[1]);
                }, 1000);
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
                            this.fos = fos;
                            this.reloadLinks();
                        });
                })
                .catch(err => {
                    this.toastr.show('Import Project Firs Failed.',
                        { classname: 'bg-danger text-light', delay: 5000 });
                    this.handleRestError(err);
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
                this.toastr.show('Import Project AIRs Failed.',
                    { classname: 'bg-danger text-light', delay: 5000 });
                this.handleRestError(err);
            });
        };
        reader.readAsBinaryString(target.files[0]);
    }
}

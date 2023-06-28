import {
    AfterViewInit,
    Component, ElementRef,
    HostListener,
    Inject,
    OnInit,
    QueryList,
    ViewChild,
    ViewChildren
} from '@angular/core';
import {Asset} from '../../types/asset';
import {ProjectOrganisationalObjective} from '../../types/project-organisational-objective';
import {FunctionalRequirement} from '../../types/functional-requirement';
import {FunctionalOutput} from '../../types/functional-output';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DOCUMENT} from '@angular/common';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';
import { Project } from 'src/app/types/project';
import {BehaviorSubject, combineLatest, Subscription} from 'rxjs';
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
import {ClearShowcaseDialogComponent} from '../../components/login/clear-showcase-dialog/clear-showcase-dialog.component';
import {ConfirmProjectImportDialogComponent} from './confirm-project-import-dialog/confirm-project-import-dialog.component';
import { PanZoomConfig, PanZoomAPI, PanZoomModel, PanZoomConfigOptions } from 'ngx-panzoom';
import {EntityLinkComponent} from "./entity-link/entity-link.component";
import {IrgraphOirDialogComponent} from "./irgraph-oir-dialog/irgraph-oir-dialog.component";
import {IrgraphAirDialogComponent} from "./irgraph-air-dialog/irgraph-air-dialog.component";
import { Oir } from 'src/app/types/organisational-objective';
import {Airs} from "../../types/airs";
import {IrgraphBulkAirDialogComponent} from "./irgraph-bulk-air-dialog/irgraph-bulk-air-dialog.component";
import 'leader-line';

declare let LeaderLine: any;

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
                private cookieService: CookieService,
                private elementRef: ElementRef
                ) {

        this.poos = this.pooService.projectOrganisationalObjectives;
        this.frs = this.frService.functionalRequirements;
        this.fos = this.foService.functionalOutputs;
        this.assets = this.assetService.assets;
    }

    static darkblue = '#4974a7';
    public env = environment;
    showIRs = this.cookieService.get('app-irgraph-showIRs') === '0' ? false : true;
    showSelected: boolean = this.cookieService.get('app-irgraph-showSelected') === '1' ? true : false;
    collapseTree: boolean= false
    isSinglePooClick = true;
    isSingleFOClick = true;
    isSingleAssetClick = true;
    isSingleFRClick = true;
    activeId: string | undefined;
    activeDialog = IrGraphDialogs.NONE;
    public dialogTypes = IrGraphDialogs;
    @ViewChildren(EntityLinkComponent) links!: QueryList<EntityLinkComponent>;

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
    highlightedNodes: string[] = []
    highlightedIrs: string[] = [];

    irDropdown: object[] = []
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
    toggleIr: boolean = false;
    panZoomConfig: PanZoomConfig = new PanZoomConfig({
        dynamicContentDimensions: false,
        keepInBounds: false,
        zoomOnDoubleClick: false,
        keepInBoundsRestoreForce: 0.5,
        keepInBoundsDragPullback: 1,
        panOnClickDrag: false,
        zoomOnMouseWheel: false,
        initialPanY: 70,
        zoomButtonIncrement: 0.25
    });
    private draggingLink!: string;
    private draggingLinkLine: any;
    private dragTarget!: HTMLElement;

    private modelChangedSubscription!: Subscription;
    private apiSubscription!: Subscription;
    private panZoomAPI!: PanZoomAPI;


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
        // @ts-ignore

    }


    ngAfterViewInit(): void {
        this.modelChangedSubscription = this.panZoomConfig.modelChanged.subscribe((model: PanZoomModel) => this.onModelChanged(model));
        this.apiSubscription = this.panZoomConfig.api.subscribe((api: PanZoomAPI) => {
            this.panZoomAPI = api
        })
    }

    @HostListener('document:keydown.space')
    spacePress() {
        this.panZoomAPI.resetView()
    }
    @HostListener('document:click',['$event'])
    dismissSelections(e: any){
        const el = e.target as HTMLElement
        const hasClassInTree: (element: HTMLElement, classNames: string[]) => (boolean | any) = (element, classNames) => {
            // If the element has one of the classNames, return true
            for(let i = 0; i < classNames.length; i++){
                if(element.classList.contains(classNames[i])){
                    return true;
                }
            }

            // If the element's parent is the body or null (indicating we've reached the top of the tree), return false
            if (element.parentElement === null || element.parentElement === document.body) {
                return false;
            }

            // Otherwise, check the parent element
            return hasClassInTree(element.parentElement, classNames);
        }
        if (!hasClassInTree(el, ['entity','ir','modal', 'entity-col']) && !this.modalService.hasOpenModals()) {
            this.removeAllHighlights()
        }
    }

    @HostListener('document:keydown.control',['$event'])
    controlDown(e:any) {
        if (!this.modalService.hasOpenModals()) {
            this.panZoomConfig.panOnClickDrag = true
            this.panZoomConfig.zoomOnMouseWheel = true
        }


    }

    @HostListener('document:keyup.control')
    controlUp() {
        if (!this.modalService.hasOpenModals()) {
            this.panZoomConfig.panOnClickDrag = false
            this.panZoomConfig.zoomOnMouseWheel = false
        }
    }
    onContainerWheel(event: WheelEvent) {
        event.preventDefault();
    }

    @HostListener('window:wheel', ['$event'])
    scroll(e: any) {
        if (this.draggingLinkLine) this.draggingLinkLine.position()
        if (this.panZoomConfig.zoomOnMouseWheel === false && !this.modalService.hasOpenModals()) {
            const isFirefox = navigator.userAgent.toLowerCase().indexOf('firefox') > -1;

            // Prevent zooming in Firefox
            if (isFirefox && e.ctrlKey) {
                e.preventDefault();
                return;
            }
            this.panZoomAPI.panDelta({x: 0, y: -e.wheelDelta * 1.05}, 0)

        }
    }

    @HostListener('document:mousemove', ['$event'])
    onMouseMove(e: MouseEvent) {
        if (this.draggingLinkLine && `${(e.target as HTMLElement).id}` !== this.dragTarget.id) {
            this.dragTarget.style.left = e.clientX + 'px';
            this.dragTarget.style.top = e.clientY + 'px';
            const el = e.target as HTMLElement;
            const sourceType = this.draggingLink.split("-")[0];
            const sourceId = this.draggingLink.split(`${sourceType}-`)[1]
            const linkColorMap: any = {
                'poo': this.poos.value,
                'fr': this.frs.value,
                'fo': this.fos.value,
            };
            const linkToMap = (entity: any)  =>  {
                if (sourceType === 'poo') return entity.frs
                if (sourceType === 'fr') return entity.fos
                if (sourceType === 'fo') return entity.assets
            };

            if (
                (el.classList.contains('fr') && sourceType === 'poo') ||
                (el.classList.contains('fo') && sourceType === 'fr') ||
                (el.classList.contains('asset') && sourceType === 'fo')
            ) {
                const entity = linkColorMap[sourceType].find((item: any) => item.id === sourceId);
                this.draggingLinkLine.color = linkToMap(entity).find((x:any) => x === el.id) === undefined ? 'green' : 'gray';
            } else {
                // Default color
                this.draggingLinkLine.color = '#007bff';
            }
            this.draggingLinkLine.position().show();
        }
    }


    handleMouseDown(id: string, type: string) {
        const el = document.getElementById(id);
        this.dragTarget = this.dragTarget || document.createElement('div');
        this.dragTarget.setAttribute('id', id);
        this.dragTarget.style.position = 'absolute';
        this.dragTarget.style.left = '0px';
        this.dragTarget.style.top = '0px';

        const container = document.getElementById('draggable');
        if (container) container.appendChild(this.dragTarget);

        this.draggingLinkLine = new LeaderLine(el, this.dragTarget, { endPlug: 'arrow1', hide: true });
        this.draggingLink = `${type}-${id}`;
    }

    @HostListener("mousedown", ["$event"])
    onMouseDown(e: MouseEvent) {
        const target = e.target as HTMLElement;
        const id = target.id;
        const classList = target.classList;

        if (classList.contains('poo')) {
            this.handleMouseDown(id, 'poo');
        } else if (classList.contains('fr')) {
            this.handleMouseDown(id, 'fr');
        } else if (classList.contains('fo')) {
            this.handleMouseDown(id, 'fo');
        }

        e.preventDefault();
    }

    @HostListener("mouseup", ["$event"])
    onMouseUp(e: DragEvent) {
        if (this.draggingLinkLine) {
            const target = e.target as HTMLElement;
            const id = target.id;
            const classList = target.classList;
            const sourceType = this.draggingLink.split('-')[0];
            const sourceId = this.draggingLink.split(`${sourceType}-`)[1];
            if (classList.contains('fr') && sourceType === 'poo' && !this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_POOS)) {
                this.linkToFR(sourceId, id);
            } else if (classList.contains('fo') && sourceType === 'fr' && !this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_FRS)) {
                this.linkToFO(sourceId, id);
            } else if (classList.contains('asset') && sourceType === 'fo' && !this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_FOS)) {
                this.linkToAsset(sourceId, id);
            }
            this.draggingLinkLine.hide();
            this.draggingLinkLine = undefined;
        }
        this.draggingLink = '';
        e.preventDefault();
    }

    zoomIn(): void {
        this.panZoomAPI.zoomIn()
    }

    zoomOut(): void {
        this.panZoomAPI.zoomOut()
    }

    resetView(): void {
        this.panZoomAPI.resetView()
    }

    onModelChanged(model: PanZoomModel): void {
        setTimeout(() => {
            this.links.forEach((link => {
                link.onResize()
            }))
        }, 1)
    }

    ngOnDestroy(): void {
        this.modelChangedSubscription.unsubscribe();
        this.apiSubscription.unsubscribe();
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

    linkToFR(sourceId: string, targetId: string): boolean {
            const startPOO = this.poos.value.find(poo => poo.id === sourceId);
            if (startPOO !== undefined) {
                if (startPOO.frs.find(fr => fr === targetId) === undefined) {
                    startPOO.frs.push(targetId);
                    this.pooService.save(startPOO, this.project.id).subscribe(value => {
                        this.addEntityLink(sourceId, targetId);
                    }, error => {
                        startPOO.frs = startPOO.frs.filter(fr => fr !== targetId);
                        this.handleRestError(error);
                    });
                }
        }
        // Didn't manage to link it, return false;
        return false;
    }

    linkToFO(sourceId: string, targetId:string): boolean {
            const startFr = this.frs.value.find(fr => fr.id === sourceId);
            if (startFr !== undefined) {
                if (startFr.fos.find(fo => fo === targetId) === undefined) {
                    startFr.fos.push(targetId);
                    this.frService.save(startFr, this.project.id).subscribe(value => {
                        this.addEntityLink(sourceId, targetId);
                    }, error => {
                        startFr.fos = startFr.fos.filter(fo => fo !== targetId);
                        this.handleRestError(error);
                    });
                }
            }
        // Didn't manage to link it, return false;
        return false;
    }

    linkToAsset(sourceId: string, targetId: string): boolean {
            const startFO = this.fos.value.find(objective => objective.id === sourceId);
            if (startFO !== undefined) {
                if (startFO.assets.find(asset => asset === targetId) === undefined) {
                    startFO.assets.push(targetId);
                    this.foService.save(startFO, this.project.id).subscribe(value => {
                        this.addEntityLink(sourceId, targetId);
                    }, error => {
                        startFO.assets = startFO.assets.filter(asset => asset !== targetId);
                        this.handleRestError(error);
                    });
                }
            }
        // Didn't manage to link it, return false;
        return false;
    }
    getPOOLinks(id: string): string[] {
        const links: string[] = [];
        const linkedFRs: string[] = this.poos.value.filter(poo => poo.id === id)
            .flatMap(poo => poo.frs);
        linkedFRs.forEach(fr => links.push(fr));
        linkedFRs.flatMap(fr => this.getFRLinks(fr, false, true)).forEach(frLink => links.push(frLink));

        return [id, ...links];
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


    getAssetLinks(id: string): string[] {
        const links: string[] = [];
        const linkedFOs: string[] = this.fos.value.filter(fo => fo.assets.find(asset => asset === id)).map(fo => fo.id);
        linkedFOs.forEach(fo => links.push(fo));
        linkedFOs.flatMap(fo => this.getFOLinks(fo, true, false)).forEach(foLink => links.push(foLink));
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


    // We shouldn't have to redraw all the lines every time. Hence this code.
    highlightEntities(entities: string[]): void {
        // Set all links to a default color
        this.links.forEach((l) => {
            l.setColor("#ccc");
        });

        entities.forEach((entity) => {
            // Cache DOM element for performance
            const element = document.getElementById(entity) as HTMLElement;

            // Guard clause to prevent potential null exception
            if (element) {
                element.classList.add('selected');
            }

            this.links.forEach((l) => {
                // Check entity relationship with links
                if ((l.leftLink === entity && entities.includes(<string>l.rightLink)) ||
                    (l.rightLink === entity && entities.includes(<string>l.leftLink))) {
                    l.setColor(IRGraphComponent.darkblue);
                }
            });
        });
    }

    selectPoo(id: string): void {
        this.isSinglePooClick = true;
        setTimeout(() => {
            if (this.isSinglePooClick) {
                this.removeAllHighlights()
                const links = this.getPOOLinks(id);
                links.forEach(link => this.highlightedNodes.push(link))
                //this.highlightEntities([...links,id])
                this.reloadLinks();
            }
        }, 250);
    }
    selectFR(id: string): void {

        this.isSingleFRClick = true;
        setTimeout(() => {
            if (this.isSingleFRClick) {
                this.removeAllHighlights()
                const links = this.getFRLinks(id,true,true);
                links.forEach(link => this.highlightedNodes.push(link))
                //this.highlightEntities([...links,id])
                this.highlightedNodes.push(id)
                this.reloadLinks();
            }
        }, 250);
    }

    selectFO(id: string): void {
        this.isSingleFOClick = true;
        setTimeout(() => {
            if (this.isSingleFOClick) {
                this.removeAllHighlights()
                const links = this.getFOLinks(id,true,true);
                links.forEach(link => this.highlightedNodes.push(link))
                //this.highlightEntities([...links,id])
                this.highlightedNodes.push(id)
                this.reloadLinks();
            }
        }, 250);
    }
    selectAsset(id: string): void {
        this.isSingleAssetClick = true;
        setTimeout(() => {
            if (this.isSingleAssetClick) {
                this.removeAllHighlights()
                const links = this.getAssetLinks(id);
                links.forEach(link => this.highlightedNodes.push(link))
                this.highlightedNodes.push(id)
                //this.highlightEntities([...links,id])
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



    mouseEnterPOO(id: string): void {
        // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getPOOLinks(id);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.add('on'));
        // (document.getElementById(id) as HTMLElement).classList.add('on');
    }

    mouseLeavePOO(id: string): void {
        // // UNCOMMENT TO ENEABLE HOVER FEATURE
        // const links = this.getPOOLinks(id);
        // links.forEach(link => (document.getElementById(link) as HTMLElement).classList.remove('on'));
        // (document.getElementById(id) as HTMLElement).classList.remove('on');
    }

    highlightPoo(id:string) {
        const selected = (document.getElementById(id) as HTMLElement).classList.contains('selected');
        const element = document.getElementById('leader-line-container');
        if (element !== null) {
            const matches = element.querySelectorAll('.card-header.selected');
            matches.forEach(x => x.classList.remove('selected'));
        }
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


    open(content: any): void {
        this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', centered: true}).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
        }, (reason) => {
            this.closeResult = `Dismissed ${IRGraphComponent.getDismissReason(reason)}`;
        });
    }

    editOir(poo: any, oir: string) {
        console.log("edit oir")
        const selectedOir = poo.oirs.find((sOir: Oir)=> sOir.id === oir);
        const pooLinks = this.getPOOLinks(poo.id);
        const linkedAssets = new Set<Asset>();

        pooLinks.forEach(link => {
            const asset = this.assets.value.find(asset => asset.id === link);
            if (asset) {
                linkedAssets.add(asset);
            }
        });

        const assetsLinkedOirs = Array.from(linkedAssets).map(asset => {
            console.log(selectedOir)
            const linkedAirs = asset.airs.filter(a => selectedOir.airs.some((s: Airs) => s.id === a.id));
            const nonLinkedAirs = asset.airs.filter(a => !linkedAirs.some(l => l.id === a.id));
            return {...asset, linkedAirs, nonLinkedAirs};
        });

        const possibleAirs = Array.from(linkedAssets).flatMap(asset => asset.airs);
        const linkedAirs = selectedOir.airs;

        this.isSinglePooClick = false;
        const modalRef = this.modalService.open(IrgraphOirDialogComponent, {
            scrollable: true,
            centered: true,
            size: 'lg'
        });
        modalRef.componentInstance.selectedPoo = poo;
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.frs = this.frs.value;
        modalRef.componentInstance.assets = this.assets.value;
        modalRef.componentInstance.poos = this.poos.value;
        modalRef.componentInstance.selectedOir = selectedOir
        modalRef.componentInstance.linkedAirs = linkedAirs
        modalRef.componentInstance.possibleAirs = possibleAirs
        modalRef.componentInstance.selectedAssets = possibleAirs
        modalRef.componentInstance.assetLinkedOirs = assetsLinkedOirs
        // modalRef.componentInstance.linkedAssets =
        // modalRef.componentInstance.poosLinks = this.getPOOLinks(poo.id)

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event.updated) {
                this.pooService.getProjectOrganisationalObjectives(this.project.id).subscribe(x => {
                    this.poos.next(x);
                    let y = x.filter((p) => p.id === poo.id)[0]
                    this.toggleOirLinks(y, selectedOir.id )
                    this.reloadLinks()
                    // this.removeAllHighlights()
                });
                this.assetService.getAssets(this.project.id)
                    .subscribe(assets => {
                        this.assets.next(assets);
                        this.reloadLinks()
                    });
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }

    editPoo(poo: any): void {
        this.isSinglePooClick = false;
        const modalRef = this.modalService.open(IrgraphOoDialogComponent, {
            scrollable: true,
            centered: true,
            size: 'lg'
        });
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
        const modalRef = this.modalService.open(IrgraphFrDialogComponent, {
            scrollable: true,
            centered: true,
            size: 'lg'
        });
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
        const modalRef = this.modalService.open(IrgraphFoDialogComponent, {
            scrollable: true,
            centered: true,
            size: 'lg'
        });
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
        console.log(asset)
        this.isSingleAssetClick = false;
        const modalRef = this.modalService.open(IrgraphAssetDialogComponent, {
            scrollable: true,
            centered: true,
            size: 'lg'
        });
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
                        this.removeAllHighlights()
                        this.reloadLinks();
                    });
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }
    bulkAirEdit(): void {
        this.isSingleAssetClick = false;
        const modalRef = this.modalService.open(IrgraphBulkAirDialogComponent, {
            scrollable: true,
            centered: true,
            size: 'lg'
        });
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.poos = this.poos.value;
        modalRef.componentInstance.assets = this.assets.value;
        modalRef.componentInstance.airs = Array.from(this.assets.value
            .map(asset => asset.airs) // Get all airs arrays
            .flat() // Flatten the array
            .reduce((unique, air) => { // Use reduce to remove duplicates
                if (!unique.has(air.airs)) { // Check if air object has been seen before
                    unique.set(air.airs, air); // Add air object to Map
                }
                return unique;
            }, new Map())
            .values()); // Get unique air objects from Map and convert to array

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
    editAir(asset: Asset, air: Airs): void {
        const selectedAir = asset.airs.filter((a: { id: string; }) => a.id === air.id)[0]
        let poosLinkedAirs: ProjectOrganisationalObjective[] = []
        this.poos.value
            .filter((objective: any) => {
                if (selectedAir.oirs) {
                    const air_oirs = objective.oirs.filter((o: Oir) =>
                        selectedAir.oirs.some((oir: Oir) => o.id === oir.id)
                    );
                    if (air_oirs.length > 0) {
                        poosLinkedAirs.push({ ...objective, air_oirs })
                    }
                }

            });

        this.isSingleAssetClick = false;
        const modalRef = this.modalService.open(IrgraphAirDialogComponent, {
            scrollable: true,
            centered: true,
            size: 'lg'
        });
        modalRef.componentInstance.project = this.project;
        modalRef.componentInstance.poos = this.poos.value;
        modalRef.componentInstance.assets = this.assets.value;
        modalRef.componentInstance.selectedAsset = asset;
        modalRef.componentInstance.selectedAir = selectedAir;
        modalRef.componentInstance.poosLinkedAirs = poosLinkedAirs;
        modalRef.componentInstance.assetLinks = this.getAssetLinks(asset.id)

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event.updated) {
                this.assetService.getAssets(this.project.id)
                    .subscribe(assets => {
                        this.assets.next(assets);
                        const newAsset = assets.filter(as => as.id === asset.id)[0]
                        const newAir = newAsset.airs.filter(a=> a.id === air.id)[0]
                        this.toggleAirLinks(newAsset, newAir)
                        this.reloadLinks();
                    });

            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }

    new(dialog: IrGraphDialogs): void {
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
            case IrGraphDialogs.BULK_AIR_DIALOG: {
                this.bulkAirEdit();
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
        } else if (error.error.forUi === true) {
            this.toastr.show(error.error.error,
                {classname: 'bg-danger text-light', delay: 5000});
        } else {
            this.toastr.show("An error has occurred",
                {classname: 'bg-danger text-light', delay: 5000});
        }
    }

    restyleLinks(): void {
        //
        //     if (this.leaderline === undefined && this.leftLink !== undefined && this.rightLink !== undefined) {
        //     const startElement = document.getElementById(this.leftLink);
        //     const endElement = document.getElementById(this.rightLink);
        //
        //     if (startElement != null
        // && endElement != null && (!this.showSelected || (endElement.classList.contains('selected')
        // && startElement.classList.contains('selected')))) {
        //     this.leaderline = new LeaderLine(startElement, endElement,
        //         {
        //             size: 2,
        //             color: (startElement.classList.contains('selected') && endElement.classList.contains('selected')) ?
        //                 IRGraphComponent.darkblue : '#ccc',
        //             startSocket: 'right',
        //             endSocket: 'left',
        //             zIndex: -1,
        //         }
        //     );
        //     return true;
        // }
        // }
        // return false;
        // }
    }


    reloadLinks(): void {
        console.log("Reloading Links")
       this.entityLinks.forEach(x => x.clear());

        this.poos.value.flatMap((poo) => poo.frs.map((fr) => [poo.id, fr]))
            .forEach(v => {
                setTimeout(() => {
                    this.addEntityLink(v[0], v[1]);
                }, 250);
            });

        this.fos.value.flatMap((fo) => fo.assets.map((asset) => [fo.id, asset]))
            .forEach(v => {setTimeout(() => {
                this.addEntityLink(v[0], v[1])
            },250)});

        this.frs.value.flatMap((fr) => fr.fos.map((fo) => [fr.id, fo]))
            .forEach(v => {
                setTimeout(() => {
                    this.addEntityLink(v[0], v[1]);
                }, 250);
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


    // isIrHighlighted(irId: string) {
    //     const selectedOir: any = poo.oirs.find((sOir: Oir)=> sOir.id === oirId);
    //     const allAirs = this.assets.value.map((asset) => asset.airs).flat()
    //     if (selectedOir) {
    //         selectedOir.airs.forEach((air: Airs) => {
    //             if (!(document.getElementById(air.id) as HTMLElement).classList.contains('air-selected')) {
    //                 (document.getElementById(air.id) as HTMLElement).classList.add('air-selected');
    //             }
    //             allAirs.forEach((a) => {
    //                 if (air.id !== a.id && air.airs.toLowerCase() === a.airs.toLowerCase()) {
    //                     (document.getElementById(a.id) as HTMLElement).classList.add('air-selected');
    //                 }
    //             })
    //         })
    //     }
    //
    // }

    showHide(id: string): string {
        return (document.getElementById(id) as HTMLElement)
            .classList.contains('hide-ir') ?  'Show' : 'Hide';
    }

    removeAllHighlights() {
        this.highlightedNodes = []
        this.highlightedIrs = []
        this.links.forEach((l) => {
            l.setColor("#ccc");
        });
        //if (reload) this.reloadLinks()
    }
    toggleOirLinks(poo: ProjectOrganisationalObjective, oirId: string) {
        this.highlightedIrs = []
        this.highlightedNodes = []

        const links = this.getPOOLinks(poo.id);
        links.forEach(link => this.highlightedNodes.push(link));
        this.highlightedNodes.push(poo.id)

        const selectedOir: any = poo.oirs.find((sOir: Oir)=> sOir.id === oirId);
        //const allAirs = this.assets.value.map((asset) => asset.airs).flat()
        if (selectedOir) {
            this.highlightedIrs.push(oirId)
            selectedOir.airs.forEach((air: Airs) => {
                this.highlightedIrs.push(air.id)
            })
        }
        this.reloadLinks()
       // this.highlightEntities(links)
    }

    toggleAirLinks(asset: Asset, air: Airs) {
        this.highlightedIrs = []
        this.highlightedNodes = []
        const links = this.getAssetLinks(asset.id);
        links.forEach(link => this.highlightedNodes.push(link));
        this.highlightedNodes.push(asset.id)
       // this.highlightedIrs = [air.id, ...air.oirs.map(oir => oir.id)]
        if (air.oirs) this.highlightedIrs = [air.id, ...air.oirs.map(oir => oir.id)]
        if (!air.oirs) this.highlightedIrs = [air.id]
        this.reloadLinks()
        //this.highlightEntities([...links, asset.id])
    }

    hideAllIR(): void {
        this.toggleIr = !this.toggleIr
        this.reloadLinks();
    }

    toggleCollapseTree(): void {
        this.collapseTree = !this.collapseTree;
        this.reloadLinks();
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

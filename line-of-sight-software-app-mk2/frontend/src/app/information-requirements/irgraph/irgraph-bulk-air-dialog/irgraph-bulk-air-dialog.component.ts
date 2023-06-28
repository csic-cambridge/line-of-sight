import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {IrGraphDialogs} from '../../../types/ir-graph-dialogs';
import {Project} from '../../../types/project';
import {ProjectOrganisationalObjective} from '../../../types/project-organisational-objective';
import { FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {BasePermissionService} from '../../../services/base/base-permission-service';
import {environment} from '../../../../environments/environment';
import {Asset} from "../../../types/asset";
import {Airs} from "../../../types/airs";
import {forkJoin, merge, Observable, Subject} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs/operators';
import {NgbTypeahead} from "@ng-bootstrap/ng-bootstrap";
import {DataDictionaryEntry} from "../../../types/data-dictionary-entry";
import {BaseAssetService} from "../../../services/base/base-asset-service";
import {AppToastService} from "../../../services/app-toast.service";

@Component({
  selector: 'app-irgraph-air-dialog',
  templateUrl: './irgraph-bulk-air-dialog.component.html',
  styleUrls: ['./irgraph-bulk-air-dialog.component.scss']
})

// Bulk link airs to assets
export class IrgraphBulkAirDialogComponent implements OnInit {
    @Output() closed = new EventEmitter<{ updated: boolean, dialog: IrGraphDialogs }>();
    @Output() hasError = new EventEmitter<any>();
    @Input() project!: Project;
    @Input() assets!: Asset[];
    @Input() airs!: Airs[];
    @Input() poos!: ProjectOrganisationalObjective[];
    @Input() assetLinks!: string[]

    @ViewChild('instanceAirs', { static: true }) instanceOir: NgbTypeahead | undefined;

    bulkAirForm = new FormGroup({})

    focusOir$ = new Subject<string>();
    clickOir$ = new Subject<string>();
    public env = environment;

    airSearchItems: Airs[] = []
    targetAir!: Airs;
    newAir: string = ""
    formatter = (result: DataDictionaryEntry) => result.text;
    resultFormatter = (result: Airs) => result.airs

    assetNames(): FormArray {
        return this.bulkAirForm.get('assetNames') as FormArray;
    }

    assetsControls(): FormArray {
        return this.bulkAirForm.get('assetsControls') as FormArray;
    }


    constructor(private fb: FormBuilder,
                private assetService: BaseAssetService,
                public permissionService: BasePermissionService,
                public toastr: AppToastService,


    ) {


    }

    ngOnInit(): void {
        this.buildForm();
    }

    buildForm(): void {
        this.bulkAirForm = new FormGroup({
            assets: this.fb.array([]),
            assetNames: this.fb.array([]),
            assetsControls: this.fb.array([]),
        })

        this.assets?.map((asset) => this.assetNames().push(this.fb.control(asset.data_dictionary_entry.text)))
        this.assets?.map(asset => this.assetsControls().push(this.fb.control({
            value: false,
            disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_AIRS) || this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.DELETE_AIRS)
        })))

        this.bulkAirForm.markAsPristine()
    }

    closeModal(): void {
        this.closed.emit({updated: false, dialog: IrGraphDialogs.AIR_DIALOG});
    }

    getUpdates() {
        let toAdd: any= []
        let toRemove: any = []
        // let linkOirs: { link: boolean;  oir: string; }[] = []
        if (this.targetAir) {
            this.assetsControls().controls.forEach((asset,i) => {
                const assetAirs = this.assets[i].airs;
                if (asset.value && !assetAirs.some((air) => air.airs === this.targetAir.airs)) {
                    let updatedAsset = {
                        ...this.assets[i],
                        airs: [...this.assets[i].airs, {id: '', airs: this.targetAir.airs }]
                    }
                    toAdd.push(updatedAsset)
                } else if (!asset.value && assetAirs.some((air) => air.airs === this.targetAir.airs)) {
                    this.assets[i].airs.forEach((a) => {
                        if (a.airs === this.targetAir.airs) {
                            let updatedAsset = {
                                ...this.assets[i],
                                airs: this.assets[i].airs.filter((b) => b.airs !== a.airs)
                            }
                            toRemove.push(updatedAsset)
                        }
                    })
                }
            });
        }
        if (this.newAir && !this.targetAir) {
            this.assetsControls().controls.forEach((asset,i) => {
                const assetAirs = this.assets[i].airs;
                if (asset.value && !assetAirs.some((air) => air.airs === this.newAir)) {
                    let updatedAsset = {
                        ...this.assets[i],
                        airs: [...this.assets[i].airs, {id: '', airs: this.newAir }]
                    }
                    toAdd.push(updatedAsset)
                }
            });
        }

        let toUpdate = [...toAdd, ...toRemove]
        return toUpdate
    }
    save(): void {
        const toUpdate = this.getUpdates();

        const observables = toUpdate.map(x => this.assetService.save(x,this.project.id));
        forkJoin(observables).subscribe({
            next: () => this.closed.emit({updated: true, dialog: IrGraphDialogs.BULK_AIR_DIALOG}),
            error: (error: { error: { error: any; }; }) => {
                this.toastr.show(`Save Asset Failed.  ${error.error.error}`,
                    {classname: 'bg-danger text-light', delay: 5000});
                this.hasError.emit(error);
            },
        });
    }

    searchAir: (text$: Observable<string>) => Observable<Airs[]> = (text$: Observable<string>) => {
        const debouncedText$ = text$.pipe(
            debounceTime(200),
            distinctUntilChanged());
        const clicksWithClosedPopup$ = this.clickOir$.pipe(filter(() => !this.instanceOir?.isPopupOpen()));
        const inputFocus$ = this.focusOir$;
        console.log(this.airs)
        return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
            map((term) => {
                    const items = this.airs.filter((v) =>
                        v.airs.toLowerCase().indexOf(term.toLowerCase()) > -1);
                    this.airSearchItems = items;
                    return items
                }
            ),
        );
    }

    selectAllAssets() {
        this.assets.forEach((asset, i) => {
            this.assetsControls().setControl(i,this.fb.control({
                value: true,
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_AIRS) || this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.DELETE_AIRS)
            }))
        })
    }

    deselectAllAssets() {
        this.assets.forEach((asset, i) => {
            this.assetsControls().setControl(i,this.fb.control({
                value: false,
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_AIRS) || this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.DELETE_AIRS)
            }))
        })
    }
    isAllDeselected() {
        return this.assetsControls().controls.every((control) => control.value === false);
    }
    isAllSelected() {
        return this.assetsControls().controls.every((control) => control.value === true);
    }
    assetChange(event: Event): void {
        const target = event.target || event.srcElement || event.currentTarget;
        const checkbox = (target as HTMLInputElement);
        const index = checkbox.attributes.getNamedItem('index')?.value;
        if (index) {
            this.assetsControls().controls[Number(index)].setValue(checkbox.checked)
        }
    }

    onAirChange(event: any) {
        const target = event.target || event.srcElement || event.currentTarget;
        this.newAir = target.value
        this.assets.forEach(asset => {
            asset.airs.forEach((air => {
                if (air.airs === this.newAir) {
                    return this.selectAirs(air)
                }
            }))
        })
    }
    selectAirs(item: any) {
            this.targetAir = item
            this.assets.map((asset: any, i)=> {
            this.assetsControls().setControl(i,this.fb.control({
                value: false,
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_AIRS) || this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.DELETE_AIRS)
            }))
            asset.airs.forEach((air: Airs) => {
                if (item.airs === air.airs) {
                    console.log(asset)
                    this.assetsControls().setControl(i, this.fb.control({
                        value: true,
                        disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_AIRS) || this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.DELETE_AIRS)
                    }))
                }
            })

        })

    }


}

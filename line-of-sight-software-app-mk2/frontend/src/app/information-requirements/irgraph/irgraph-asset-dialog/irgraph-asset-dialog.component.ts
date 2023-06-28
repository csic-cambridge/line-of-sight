import {
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output, ViewChild,
} from '@angular/core';
import {Asset} from '../../../types/asset';
import {FunctionalOutput} from '../../../types/functional-output';
import {Project} from '../../../types/project';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {IrGraphDialogs} from '../../../types/ir-graph-dialogs';
import {BasePermissionService} from '../../../services/base/base-permission-service';
import {AppToastService} from '../../../services/app-toast.service';
import {DataDictionaryEntry} from '../../../types/data-dictionary-entry';
import {merge, Observable, OperatorFunction, Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs/operators';
import {requiredNameValidator} from '../../../helpers/validation/required-name-validator';
import {NgbModal, NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';
import {IrgraphDeleteDialogComponent} from '../irgraph-delete-dialog/irgraph-delete-dialog.component';
import {forbiddenNameValidator} from '../../../dashboard/copy-project-dialog/copy-project-dialog.component';
import {BaseAssetService} from '../../../services/base/base-asset-service';
import {BaseAirsService} from '../../../services/base/base-airs-service';
import {BaseAssetDictionaryEntryService} from '../../../services/base/base-asset-dictionary-entry-service';
import {Airs} from "../../../types/airs";

@Component({
  selector: 'app-irgraph-asset-dialog',
  templateUrl: './irgraph-asset-dialog.component.html',
  styleUrls: ['./irgraph-asset-dialog.component.scss']
})
export class IrgraphAssetDialogComponent implements OnInit {
    constructor(private assetService: BaseAssetService,
                private fb: FormBuilder,
                public toastr: AppToastService,
                public permissionService: BasePermissionService,
                public airsService: BaseAirsService,
                private modalService: NgbModal,
                private assetDdeService: BaseAssetDictionaryEntryService) {
    }

    @Input() project!: Project;
    @Input() selectedAsset!: Asset;
    @Input() fos!: FunctionalOutput[];
    @Input() assets!: Asset[];
    @Output() closed = new EventEmitter<{ updated: boolean, dialog: IrGraphDialogs }>();
    @Output() hasError = new EventEmitter<any>();
    @ViewChild('instance', {static: true}) instance: NgbTypeahead | undefined;
    @ViewChild('instanceAir', { static: true }) instanceAir: NgbTypeahead | undefined;
    focus$ = new Subject<string>();
    click$ = new Subject<string>();
    focusAir$ = new Subject<string>();
    clickAir$ = new Subject<string>();
    searchItems = [] as DataDictionaryEntry[];
    airSearchItems = [] as Airs[];
    showDelete = false;
    assetForm = new FormGroup({});
    newAirs = [] as Airs[];
    formatter = (result: DataDictionaryEntry) => result.text;
    inputformatter = (x: { text: string }) => x.text;

    search: OperatorFunction<string, readonly DataDictionaryEntry[]> = (text$: Observable<string>) => {
        const debouncedText$ = text$.pipe(
            debounceTime(200),
            distinctUntilChanged());

        const clicksWithClosedPopup$ = this.click$.pipe(filter(() => !this.instance?.isPopupOpen()));
        const inputFocus$ = this.focus$;
        return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
            map((term) => {
                    const items = this.getNewAssets().filter((v) =>
                        v.text.toLowerCase().indexOf(term.toLowerCase()) > -1);
                    this.searchItems = items;
                    return items;
                }
            ),
        );
    }
    searchAir: (text$: Observable<string>) => Observable<Airs[]> = (text$: Observable<string>) => {
        const debouncedText$ = text$.pipe(
            debounceTime(200),
            distinctUntilChanged());

        const clicksWithClosedPopup$ = this.clickAir$.pipe(filter(() => !this.instanceAir?.isPopupOpen()));
        const inputFocus$ = this.focusAir$;
        return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
            map((term) => {
                const items = this.newAirs.filter((v) =>
                        v.airs.toLowerCase().indexOf(term.toLowerCase()) > -1);
                    this.airSearchItems = items;
                    return items
                }
            ),
        );
    }

    resultFormatter = (result: Airs) => result.airs

    ngOnInit(): void {
        this.buildForm();
    }

    airs(): FormArray {
        return this.assetForm.get('airs') as FormArray;
    }

    airNames(): FormArray {
        return this.assetForm.get('airNames') as FormArray;
    }

    buildForm(): void {
        if (!this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_AIRS)) {
            this.getAirs();
        }
        this.assetForm = new FormGroup({
            id: this.fb.control(''),
            airs: this.fb.array([]),
            airNames: this.fb.array([]),
            newAir: this.fb.control({
                value: '',
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_AIRS)
            }, [forbiddenNameValidator(this.selectedAsset?.airs.map(x => x.airs), false)]),
            entry_Id: this.fb.control(''),
            assetTitle: this.fb.control('', [Validators.required,
                requiredNameValidator(this.getNewAssets().map(x => x.text))]),
        });
        if (this.selectedAsset.id !== '') {
            this.assetForm.patchValue({
                assetTitle: {
                    text: this.selectedAsset?.data_dictionary_entry?.text,
                    disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_ASSETS)
                },
                id: this.selectedAsset.id
            });
            this.assetForm.controls.assetTitle.setValidators([]);
            this.selectedAsset?.airs?.map(x => this.airs().push(this.fb.control({
                value: true,
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.DELETE_AIRS)
            })));
            this.selectedAsset?.airs?.map(x => this.airNames().push(this.fb.control(x.airs)));
            this.assetForm.markAsPristine();
        }
    }

    save(): void {

        if (!this.selectedAsset || this.assetForm.invalid) {
            return;
        }
        const asset: Asset = {
            id: this.assetForm.controls.id.value,
            data_dictionary_entry: {
                id: this.selectedAsset.data_dictionary_entry.text.split('-')[0].trim(),
                entry_id: this.selectedAsset.data_dictionary_entry.entry_id.split('-')[0].trim(),
                text: (typeof this.assetForm.value.assetTitle === 'string') ?
                    this.assetForm.value.assetTitle : this.assetForm.value.assetTitle.text
            },
            name: (typeof this.assetForm.value.assetTitle === 'string') ?
                this.assetForm.value.assetTitle : this.assetForm.value.assetTitle.text,
            airs: []
        };

        this.airs().getRawValue().map((v, i) => {
            if (v) {
                asset.airs.push(this.selectedAsset.airs[i]);
            }
        });

        if (this.assetForm.value.newAir !== '') {
                asset.airs.push({id:'', airs: this.assetForm.value.newAir.airs ?this.assetForm.value.newAir.airs : this.assetForm.value.newAir, oirs:[]});
        }
        this.assetService.save(asset, this.project.id)
            .subscribe(
                () => this.closed.emit({updated: true, dialog: IrGraphDialogs.ASSET_DIALOG}),
                (error: { error: { error: any; }; }) => {
                    this.toastr.show(`Save Asset Failed.  ${error.error.error}`,
                        {classname: 'bg-danger text-light', delay: 5000});
                    this.hasError.emit(error);
                });
    }

    delete(): void {

        const modalRef = this.modalService.open(IrgraphDeleteDialogComponent, {centered: true});
        modalRef.componentInstance.title = 'Delete Asset';
        modalRef.componentInstance.message = `Are you sure you want to delete ${this.selectedAsset?.data_dictionary_entry.text}`;

        modalRef.closed.toPromise().then(x => {
            if (x && this.selectedAsset) {
                this.assetService.delete(this.selectedAsset?.id, this.project.id)
                    .subscribe(
                        () => this.closed.emit({updated: true, dialog: IrGraphDialogs.ASSET_DIALOG}),
                        (error: any) => this.hasError.emit(error));
            }
        });
    }

    closeModal(): void {
        this.closed.emit({updated: false, dialog: IrGraphDialogs.ASSET_DIALOG});
    }

    hasFoLink(): boolean {
        const id = this.selectedAsset ? this.selectedAsset.id : '';
        if (id !== '') {
            return this.fos.filter(x => x.assets.includes(id)).length > 0;
        }
        return false;
    }

    airChange(event: Event): void {
        const target = event.target || event.srcElement || event.currentTarget;
        const checkbox = (target as HTMLInputElement);
        if (checkbox.checked) {
            const index = checkbox.attributes.getNamedItem('index')?.value;
            if (index) {
                this.airs().controls[Number(index)].markAsPristine();
            }
        }
    }

    newAirChange(event: Event): void {
        const target = event.target || event.srcElement || event.currentTarget;
        const textBox = (target as HTMLInputElement);
        if (textBox.value === '') {
            const control = this.assetForm.get('newAir');
            if (control) {
                control.markAsPristine();
            }
        }
    }

    selectAsset($event: any): void {
        const asset = this.assetDdeService.entries$.value.find((x: { text: string; }) => x.text.trim() === $event.target.value);
        if (asset) {
            this.selectedAsset = {
                id: '',
                airs: [],
                data_dictionary_entry: {
                    entry_id: asset.entry_id,
                    text: asset.text,
                    id: asset.id
                },
                name: asset.text
            };
            this.buildForm();
            this.assetForm.controls.assetTitle.markAsDirty();
        }
    }

    allowDelete(): boolean {
        if (!this.selectedAsset || this.selectedAsset.id === '') {
            return false;
        }
        return this.selectedAsset?.airs?.length === 0 && !this.hasFoLink();
    }

    getNewAssets(): DataDictionaryEntry[] {
        return this.assetDdeService.entries$.value.filter((x: { entry_id: string; }) =>
            this.assets.filter(a => a.data_dictionary_entry.entry_id === x.entry_id).length === 0);
    }

    itemSelected($event: any): void {
        this.selectedAsset = {
            id: '',
            airs: [],
            data_dictionary_entry: {
                entry_id: $event.item.entry_id,
                text: $event.item.text
            },
            name: $event.item.text
        };
        this.assetForm.patchValue({
            entry_Id: $event.item.entry_id,
        });
    }
    getAirs(): void {
        this.airsService.get().subscribe((x: any[]) => {
            let uniqueAirsMap = new Map();
            let selectedAirIds = new Set(this.selectedAsset.airs.map(a => a.id));
            let selecteAirNames = new Set(this.selectedAsset.airs.map(a => a.airs));
            for(let air of x) {
                // If the fir.firs value is not yet in the map or in selectedFirNames, add the whole fir object
                if(!uniqueAirsMap.has(air.airs) && !selectedAirIds.has(air.id) && !selecteAirNames.has(air.airs)) {
                    uniqueAirsMap.set(air.airs, air);
                }
            }
            this.newAirs = Array.from(uniqueAirsMap.values());
        });
    }
}

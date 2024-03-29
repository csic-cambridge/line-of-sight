import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Asset} from '../../../types/asset';
import {Project} from '../../../types/project';
import {DataDictionaryEntry} from '../../../types/data-dictionary-entry';
import {FunctionalOutput} from '../../../types/functional-output';
import {FunctionalRequirement} from '../../../types/functional-requirement';
import {IrGraphDialogs} from '../../../types/ir-graph-dialogs';
import {IMultiSelectOption, IMultiSelectSettings, IMultiSelectTexts} from 'ngx-bootstrap-multiselect';
import {BasePermissionService} from '../../../services/base/base-permission-service';
import {AppToastService} from '../../../services/app-toast.service';
import {merge, Observable, OperatorFunction, Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs/operators';
import {requiredNameValidator} from '../../../helpers/validation/required-name-validator';
import {IrgraphDeleteDialogComponent} from '../irgraph-delete-dialog/irgraph-delete-dialog.component';
import {NgbModal, NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';
import {forbiddenNameValidator} from '../../../dashboard/copy-project-dialog/copy-project-dialog.component';
import {BaseFirsService} from '../../../services/base/base-firs-service';
import {BaseFunctionalOutputService} from '../../../services/base/base-functional-output-service';
import {BaseAssetDictionaryEntryService} from '../../../services/base/base-asset-dictionary-entry-service';
import {Airs} from "../../../types/airs";
import {Firs} from "../../../types/firs";

@Component({
  selector: 'app-irgraph-fo-dialog',
  templateUrl: './irgraph-fo-dialog.component.html',
  styleUrls: ['./irgraph-fo-dialog.component.scss']
})
export class IrgraphFoDialogComponent implements OnInit {
    constructor(private fb: FormBuilder,
        private foService: BaseFunctionalOutputService,
                public permissionService: BasePermissionService,
                public firsService: BaseFirsService,
                public toastr: AppToastService,
                private modalService: NgbModal,
                public assetDdeService: BaseAssetDictionaryEntryService) {
    }
    @Input() foDataDictionary!: DataDictionaryEntry[];
    @Input() project!: Project;
    @Input() selectedFO!: FunctionalOutput;
    @Input() frs!: FunctionalRequirement[];
    @Input() fos!: FunctionalOutput[];
    @Input() assets!: Asset[];
    @Output() closed = new EventEmitter<{ updated: boolean, dialog: IrGraphDialogs }>();
    @Output() hasError = new EventEmitter<any>();

    @ViewChild('instance', { static: true }) instance: NgbTypeahead | undefined;
    @ViewChild('instanceFir', { static: true }) instanceFir: NgbTypeahead | undefined;
    focus$ = new Subject<string>();
    click$ = new Subject<string>();
    focusFir$ = new Subject<string>();
    clickFir$ = new Subject<string>();
    searchItems = [] as DataDictionaryEntry[];
    firSearchItems = [] as Firs[];
    foAssetOptions: IMultiSelectOption[] = [];
    foForm = new FormGroup({});
    foAssetTexts: IMultiSelectTexts = {defaultTitle: 'Select Assets to link', searchEmptyResult: 'No Assets found ...'};
    mySettings: IMultiSelectSettings = {buttonClasses: 'form-control element-text', enableSearch: true, dynamicTitleMaxItems: 0};
    newFirs = [] as Firs[];
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
                    const items = this.getNewFunctionalObjectives().filter((v) =>
                        v.text.toLowerCase().indexOf(term.toLowerCase()) > -1);
                    this.searchItems = items;
                    return items
                }
            ),
        );
    }

    searchFir: (text$: Observable<string>) => Observable<Firs[]> = (text$: Observable<string>) => {
        const debouncedText$ = text$.pipe(
            debounceTime(200),
            distinctUntilChanged());

        const clicksWithClosedPopup$ = this.clickFir$.pipe(filter(() => !this.instanceFir?.isPopupOpen()));
        const inputFocus$ = this.focusFir$;
        return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
            map((term) => {
                    const items = this.newFirs.filter((v) =>
                        v.firs.toLowerCase().indexOf(term.toLowerCase()) > -1);
                    this.firSearchItems = items;
                    return items
                }
            ),
        );
    }
    firs(): FormArray {
        return this.foForm.get('firs') as FormArray;
    }
    firNames(): FormArray {
        return this.foForm.get('firNames') as FormArray;
    }

    linkedAssets(): FormArray {
        return this.foForm.get('linkedAssets') as FormArray;
    }

    linkedAssetNames(): FormArray {
        return this.foForm.get('linkedAssetNames') as FormArray;
    }
    linkedAssetIds(): FormArray {
        return this.foForm.get('linkedAssetIds') as FormArray;
    }

    linkedFrs(): FormArray {
        return this.foForm.get('linkedFrs') as FormArray;
    }

    closeModal(): void {
        this.closed.emit({updated: false, dialog: IrGraphDialogs.FO_DIALOG});
    }

    buildForm(): void {
        if (!this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_FIRS)) {
            this.getFirs();
        }
        this.foForm = new FormGroup({
            firs: this.fb.array([]),
            firNames: this.fb.array([]),
            linkedAssets: this.fb.array([]),
            linkedAssetIds: this.fb.array([]),
            linkedAssetNames: this.fb.array([]),
            selectedAssets: this.fb.control([]),
            linkedFrs: this.fb.array([]),
            foName: this.fb.control('', [Validators.required,
                requiredNameValidator(this.getNewFunctionalObjectives().map(x => x.text))]),
            newFir: this.fb.control({value: '', disabled:
                    this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.ADD_FIRS)},
            [forbiddenNameValidator(this.selectedFO?.firs.map(x => x.firs), false)]),
            id: this.fb.control(''),
            entry_id: this.fb.control(''),
        });
        if (this.selectedFO.id !== '') {
            this.foForm.patchValue({
                foName: {text: this.selectedFO?.data_dictionary_entry?.text},
                id: this.selectedFO.id
            });
            this.foForm.controls.foName.setValidators([]);
            this.selectedFO.firs?.map(x => this.firs().push(this.fb.control({value: true,
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.DELETE_FIRS)})));
            this.selectedFO.firs?.map(x => this.firNames().push(this.fb.control(x.firs)));
            this.selectedFO.assets?.map(x => this.linkedAssets().push(this.fb.control({value: true,
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_FOS)})));
        }
        this.assets.filter(x => this.selectedFO?.assets?.includes(x.id))
            .map((asset) => this.linkedAssetIds().push(this.fb.control(asset.id)));
        this.assets.filter(x => this.selectedFO?.assets?.includes(x.id))
            .map((asset) => this.linkedAssetNames().push(this.fb.control(asset.data_dictionary_entry.text)));

        this.frs.filter(f => f.fos.includes(this.selectedFO ? this.selectedFO.id : ''))
            .map(x => this.linkedFrs().push(this.fb.control(x.name)));
        this.foForm.markAsPristine();
        this.foAssetOptions = this.assets.filter(x => !this.selectedFO?.assets?.includes(x.id))
            .map((asset) => {
                return {id: asset.id, name: asset.data_dictionary_entry.text};
            });
    }

    delete(): void {
        const modalRef = this.modalService.open(IrgraphDeleteDialogComponent, {centered: true});
        modalRef.componentInstance.title = 'Delete Functional Objective';
        modalRef.componentInstance.message = `Are you sure you want to delete ${this.selectedFO?.data_dictionary_entry.text}`;

        modalRef.closed.toPromise().then((x: any) => {
            if (x && this.selectedFO) {
                this.foService.delete(this.selectedFO.id, this.project.id)
                    .subscribe(
                        () => this.closed.emit({updated: true, dialog: IrGraphDialogs.FO_DIALOG}),
                        (error: any) => this.hasError.emit(error));
            }
        });
    }

    save(): void {

        if (!this.selectedFO || this.foForm.invalid) {
            return;
        }

        const foToSave: FunctionalOutput = {
            id: this.selectedFO.id,
            data_dictionary_entry: {
                id: this.selectedFO.data_dictionary_entry.text.split('-')[0].trim(),
                entry_id: this.selectedFO.data_dictionary_entry.entry_id.split('-')[0].trim(),
                text: (typeof this.foForm.value.foName === 'string') ? this.foForm.value.foName : this.foForm.value.foName.text
            },
            name: '',
            assets: [],
            firs: []
        };
        this.firs().getRawValue().map((v, i) => {
            if (v) {
                foToSave.firs.push(this.selectedFO.firs[i]);
            }
        });

        if (this.foForm.value.newFir !== '') {
            if (this.foForm.value.newFir.id) {
                foToSave.firs.push({id:'', firs:this.foForm.value.newFir.firs});
            } else {
                foToSave.firs.push({id:'', firs: this.foForm.value.newFir});
            }
        }
        this.linkedAssets().getRawValue().map((v, i) => {
            if (v) {
                foToSave.assets.push(this.linkedAssetIds().controls[i].value);
            }
        });
        (this.foForm.value.selectedAssets as []).map(v => {
            foToSave.assets.push(v);
        });
        this.foService.save(foToSave, this.project.id)
            .subscribe(
                () => {
                    this.closed.emit({updated: true, dialog: IrGraphDialogs.FO_DIALOG});
                },
                (error: any) => {
                    this.hasError.emit(error);
                }
            );
    }

    firChange($event: Event): void {
        const target = $event.target || $event.srcElement || $event.currentTarget;
        const checkbox = (target as HTMLInputElement);
        if (checkbox.checked) {
            const index = checkbox.attributes.getNamedItem('index')?.value;
            if (index) {
                this.firs().controls[Number(index)].markAsPristine();
            }
        }
    }

    assetChange($event: Event): void {
        const target = $event.target || $event.srcElement || $event.currentTarget;
        const checkbox = (target as HTMLInputElement);
        if (checkbox.checked) {
            const index = checkbox.attributes.getNamedItem('index')?.value;
            if (index) {
                this.linkedAssets().controls[Number(index)].markAsPristine();
            }
        }
    }

    newFirChange($event: Event): void {
        const target = $event.target || $event.srcElement || $event.currentTarget;
        const textbox = (target as HTMLInputElement);
        if (textbox.value === '') {
            const control = this.foForm.get('newFir');
            if (control) {
                control.markAsPristine();
            }
        }
    }

    fosChange($event: Event): void {
        const target = $event.target || $event.srcElement || $event.currentTarget;
        const textbox = (target as HTMLInputElement);
        const find = this.foDataDictionary.find(x => x.text.trim() === textbox.value.trim());
        if (find) {
            this.selectedFO = {
                id: '',
                data_dictionary_entry: {
                    id: find.id,
                    entry_id: find.text.split('-')[0].trim(),
                    text: find.text, asset_dictionary_id: ''
                },
                name: find.text,
                assets: [],
                firs: []
            };
            this.buildForm();
            this.foForm.controls.foName.markAsDirty();
        }
    }

    ngOnInit(): void {
        this.buildForm();
    }

    allowDelete(): boolean {
        if (!this.selectedFO || this.selectedFO.id === '') {
            return false;
        }
        return this.selectedFO?.assets?.length === 0 &&
            this.selectedFO?.firs?.length === 0 &&
            this.frs.filter(f => f.fos.includes(this.selectedFO ? this.selectedFO.id : '')).length === 0;
    }

    selectedAssetsChange($event: Event): void {
        if (this.foForm.value.selectedAssets.length === 0) {
            this.foForm.controls.selectedAssets.markAsPristine();
        }
    }

    getNewFunctionalObjectives(): DataDictionaryEntry[] {
        return this.foDataDictionary.filter(x =>
            this.fos.filter(a => a.data_dictionary_entry.entry_id === x.entry_id).length === 0);
    }

    getFirs(): void {
        this.firsService.get().subscribe((x: any[]) => {
            console.log(x)
            let uniqueFirsMap = new Map();
            let selectedFirIds = new Set(this.selectedFO.firs.map(a => a.id));
            let selectedFirNames = new Set(this.selectedFO.firs.map(a => a.firs));
            for(let fir of x) {
                // If the fir.firs value is not yet in the map or in selectedFirNames, add the whole fir object
                if(!uniqueFirsMap.has(fir.firs) && !selectedFirIds.has(fir.id) && !selectedFirNames.has(fir.firs)) {
                    uniqueFirsMap.set(fir.firs, fir);
                }
            }
            this.newFirs = Array.from(uniqueFirsMap.values());
        });
    }

    resultFormatter = (result: Firs) => result.firs

    itemSelected($event: any): void {
        this.selectedFO = {
            id: '',
            assets: [],
            firs: [],
            data_dictionary_entry: {
                entry_id: $event.item.entry_id,
                text: $event.item.text
            },
            name: $event.item.text
        };
        this.foForm.patchValue({
            entry_Id: $event.item.entry_id,
        });
    }
}

import {
    Component,
    EventEmitter,
    Input, OnInit,
    Output
} from '@angular/core';
import {IrGraphDialogs} from '../../../types/ir-graph-dialogs';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Project} from '../../../types/project';
import {FunctionalRequirement} from '../../../types/functional-requirement';
import {ProjectOrganisationalObjective} from '../../../types/project-organisational-objective';
import {IMultiSelectOption, IMultiSelectSettings, IMultiSelectTexts} from 'ngx-bootstrap-multiselect';
import {FunctionalOutput} from '../../../types/functional-output';
import {IrgraphDeleteDialogComponent} from '../irgraph-delete-dialog/irgraph-delete-dialog.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {forbiddenNameValidator} from '../../../dashboard/copy-project-dialog/copy-project-dialog.component';
import {BaseFunctionalRequirementService} from '../../../services/base/base-functional-requirement-service';
import {BasePermissionService} from '../../../services/base/base-permission-service';
import {BaseAssetDictionaryEntryService} from '../../../services/base/base-asset-dictionary-entry-service';
@Component({
  selector: 'app-irgraph-fr-dialog',
  templateUrl: './irgraph-fr-dialog.component.html',
  styleUrls: ['./irgraph-fr-dialog.component.scss']
})
export class IrgraphFrDialogComponent implements OnInit {

    @Output() closed = new EventEmitter<{ updated: boolean, dialog: IrGraphDialogs }>();
    @Output() hasError = new EventEmitter<any>();
    @Input() selectedFr!: FunctionalRequirement;
    @Input() project!: Project;
    @Input() frs!: FunctionalRequirement[];
    @Input() fos: FunctionalOutput[] = [];
    @Input() poos!: ProjectOrganisationalObjective[];
    frFOOptions!: IMultiSelectOption[];
    frForm = new FormGroup({});
    frFOTexts: IMultiSelectTexts = {defaultTitle: 'Select FOs to link', searchEmptyResult: 'No FOs found ...'};
    mySettings: IMultiSelectSettings = {buttonClasses: 'form-control element-text', enableSearch: true, dynamicTitleMaxItems: 0};
    selectedFOArray: Array<string> = [];
    constructor(private fb: FormBuilder,
                private frService: BaseFunctionalRequirementService,
                public permissionService: BasePermissionService,
                private modalService: NgbModal,
                public assetDdeService: BaseAssetDictionaryEntryService) {
    }

    ngOnInit(): void {
        this.buildForm();
    }

    linkedFos(): FormArray {
        return this.frForm.get('linkedFos') as FormArray;
    }
    linkedFoNames(): FormArray {
        return this.frForm.get('linkedFoNames') as FormArray;
    }
    linkedFoIds(): FormArray {
        return this.frForm.get('linkedFoIds') as FormArray;
    }
    linkedPoos(): FormArray {
        return this.frForm.get('linkedPoos') as FormArray;
    }

    buildForm(): void {
        const frsList = this.frs.filter(x => x.id !== this.selectedFr?.id).map(x => x.name);
        this.frForm = new FormGroup({
            id: this.fb.control(''),
            linkedFos: this.fb.array([]),
            linkedPoos: this.fb.array([]),
            linkedFoNames: this.fb.array([]),
            linkedFoIds: this.fb.array([]),
            selectedFos: this.fb.control([]),
            frName: this.fb.control({value: '',
                    disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_FRS)},
                [Validators.required,
                    forbiddenNameValidator(frsList, true)]),
        });
        this.frFOOptions = this.fos.filter(x => this.selectedFr ? !this.selectedFr?.fos?.includes(x.id) : true)
            .map((fos) => {
                return {id: fos.id, name: fos.data_dictionary_entry.text};
            });
        if (this.selectedFr) {
            this.frForm.patchValue({
                frName: this.selectedFr.name,
                id: this.selectedFr.id
            });
        }
        this.selectedFr?.fos?.map(x => this.linkedFos().push(this.fb.control({value: true,
            disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_FRS)})));
        this.selectedFr?.fos?.map(find => this.linkedFoNames()
            .push(this.fb.control(this.fos.find(x => x.id === find)?.data_dictionary_entry.text)));
        this.selectedFr?.fos?.map(find => this.linkedFoIds()
            .push(this.fb.control(this.fos.find(x => x.id === find)?.id)));
        this.poos.filter(x => this.selectedFr ? x.frs.includes(this.selectedFr.id) : false)
            .map(poo => this.linkedPoos().push(this.fb.control(poo.name)));
        this.frForm.markAsPristine();
    }

    saveFunctionalRequirements(): void {
        if (this.frForm.invalid) {
            return;
        }

        const frToSave: FunctionalRequirement = {
            id: this.frForm.controls.id.value,
            name: this.frForm.controls.frName.value,
            fos: []
        };

        // Get checked fos and update the FunctionalRequirement to save.
        this.linkedFos().getRawValue().map((v, i) => {
            if (v) {
                frToSave.fos.push(this.linkedFoIds().getRawValue()[i]);
            }
        });
        (this.frForm.value.selectedFos as []).map(v => {
            frToSave.fos.push(v);
        });
        this.frService.save(frToSave, this.project.id)
            .subscribe(() => {
                    this.closed.emit({updated: true, dialog: IrGraphDialogs.FR_DIALOG});
                }, error => {
                    this.hasError.emit(error);
                }
            );
    }

    deleteFuncRequirement(): void {

        const modalRef = this.modalService.open(IrgraphDeleteDialogComponent, {centered: true});
        modalRef.componentInstance.title = 'Delete Functional Requirement';
        modalRef.componentInstance.message = `Are you sure you want to delete ${this.selectedFr?.name}`;

        modalRef.closed.toPromise().then(x => {
            if (x && this.selectedFr) {
                this.frService.delete(this.selectedFr.id, this.project.id)
                    .subscribe(
                        () =>  this.closed.emit({updated: true, dialog: IrGraphDialogs.FR_DIALOG}),
                        error => {
                            this.hasError.emit(error);
                        });
            }
        });
    }

    closeModal(): void {
        this.closed.emit({updated: false, dialog: IrGraphDialogs.FR_DIALOG});
    }

    foChange($event: Event): void {
        const target = $event.target || $event.srcElement || $event.currentTarget;
        const checkbox = (target as HTMLInputElement);
        if (checkbox.checked) {
            const index = checkbox.attributes.getNamedItem('index')?.value;
            if (index){
                this.linkedFos().controls[Number(index)].markAsPristine();
            }
        }
    }

    allowDelete(): boolean {
        if (!this.selectedFr || this.selectedFr.id === '') {
            return false;
        }
        const allow =  this.linkedFos().length === 0 && this.linkedPoos().length === 0;
        return allow;
    }
}

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {IrGraphDialogs} from '../../../types/ir-graph-dialogs';
import {Project} from '../../../types/project';
import {FunctionalRequirement} from '../../../types/functional-requirement';
import {ProjectOrganisationalObjective} from '../../../types/project-organisational-objective';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {PermissionService} from '../../../services/permission.service';
import {AssetDataDictionaryEntryService} from '../../../services/asset-data-dictionary-entry.service';
import {ProjectOrganisationalObjectiveService} from '../../../services/project-organisational-objective.service';
import {IMultiSelectOption, IMultiSelectSettings, IMultiSelectTexts} from 'ngx-bootstrap-multiselect';
import {Oir} from '../../../types/organisational-objective';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {IrgraphDeleteDialogComponent} from "../irgraph-delete-dialog/irgraph-delete-dialog.component";

@Component({
  selector: 'app-irgraph-oo-dialog',
  templateUrl: './irgraph-oo-dialog.component.html',
  styleUrls: ['./irgraph-oo-dialog.component.scss']
})
export class IrgraphOoDialogComponent implements OnInit {
    @Output() closed = new EventEmitter<{ updated: boolean, dialog: IrGraphDialogs }>();
    @Output() hasError = new EventEmitter<any>();
    @Input() selectedPoo!: ProjectOrganisationalObjective;
    @Input() project!: Project;
    @Input() frs!: FunctionalRequirement[];
    @Input() poos!: ProjectOrganisationalObjective[];
    pooFROptions: Array<IMultiSelectOption> = [];
    selectedFRArray: Array<string> = [];
    pooFRTexts: IMultiSelectTexts = {defaultTitle: 'Select FRs to link', searchEmptyResult: 'No FRs found ...'};
    mySettings: IMultiSelectSettings = {buttonClasses: 'form-control element-text', enableSearch: true, dynamicTitleMaxItems: 0};
    pooForm = new FormGroup({});

    constructor(private fb: FormBuilder,
                private pooService: ProjectOrganisationalObjectiveService,
                public permissionService: PermissionService,
                private activeModal: NgbModal,
                public assetDdeService: AssetDataDictionaryEntryService) {
    }
    oirs(): FormArray {
        return this.pooForm?.get('oirs') as FormArray;
    }
    oirNames(): FormArray {
        return this.pooForm?.get('oirNames') as FormArray;
    }
    oirIds(): FormArray {
        return this.pooForm?.get('oirIds') as FormArray;
    }
    deletedOirs(): FormArray {
        return this.pooForm?.get('deletedOirs') as FormArray;
    }
    deletedOirNames(): FormArray {
        return this.pooForm?.get('deletedOirNames') as FormArray;
    }
    deletedOirIds(): FormArray {
        return this.pooForm?.get('deletedOirIds') as FormArray;
    }
    linkedFrs(): FormArray {
        return this.pooForm.get('linkedFrs') as FormArray;
    }
    linkedFrNames(): FormArray {
        return this.pooForm.get('linkedFrNames') as FormArray;
    }
    linkedFrIds(): FormArray {
        return this.pooForm.get('linkedFrIds') as FormArray;
    }

    buildForm(): void {
        this.pooForm = new FormGroup({
            id: this.fb.control(''),
            versionId: this.fb.control({value: '',
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_POOS)}),
            name: this.fb.control(''),
            oirs: this.fb.array([]),
            oirNames: this.fb.array([]),
            oirIds: this.fb.array([]),
            deletedOirs: this.fb.array([]),
            deletedOirNames: this.fb.array([]),
            deletedOirIds: this.fb.array([]),
            linkedFrs: this.fb.array([]),
            selectedFrs: this.fb.control([]),
            linkedFrNames: this.fb.array([]),
            linkedFrIds: this.fb.array([]),
            linkedIds: this.fb.array([]),
            pooVersions: this.fb.control(''),
        });
        this.pooForm.patchValue({
            name: this.selectedPoo ? this.selectedPoo.name : '',
            id: this.selectedPoo ? this.selectedPoo.id : ''
        });

        this.selectedPoo?.oirs
            .map(x => this.oirs().push(this.fb.control({value: true,
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_POOS)})));
        this.selectedPoo?.oirs
            .map(x => this.oirNames().push(this.fb.control(x.oir)));
        this.selectedPoo?.oirs
            .map(x => this.oirIds().push(this.fb.control(x.id)));

        this.selectedPoo?.deleted_oirs
            .map(x => this.deletedOirs().push(this.fb.control({value: false,
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_POOS)})));
        this.selectedPoo?.deleted_oirs
            .map(x => this.deletedOirNames().push(this.fb.control(x.oir)));
        this.selectedPoo?.deleted_oirs
            .map(x => this.deletedOirIds().push(this.fb.control(x.id)));

        if (this.selectedPoo) {
            const pooFrs = this.selectedPoo.frs;
            this.frs.filter(x => pooFrs.includes(x.id))
                .map(fr => this.linkedFrs().push(this.fb.control({value: true,
                    disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.EDIT_POOS)})));
            this.frs.filter(x => pooFrs.includes(x.id))
                .map(fr => this.linkedFrNames().push(this.fb.control(fr.name)));
            this.frs.filter(x => pooFrs.includes(x.id))
                .map(fr => this.linkedFrIds().push(this.fb.control(fr.id)));
        }

        this.pooForm.markAsPristine();
        this.pooFROptions = this.frs.filter(x => this.selectedPoo ? !this.selectedPoo.frs.includes(x.id) : false);
    }

    save(): void {
        if (this.pooForm.invalid) {
            return;
        }
        const poo: ProjectOrganisationalObjective = {
            id: this.pooForm.controls.id.value,
            project_id: this.project.id,
            name: this.pooForm.controls.name.value,
            oo_version_id: this.pooForm.controls.versionId.value,
            oo_is_deleted: false,
            oo_versions: [],
            oirs: [],
            deleted_oirs: [],
            frs: []
        };
        this.linkedFrs().getRawValue().map((v, i) => {
            if (v) {
                poo.frs.push(this.linkedFrIds().controls[i].value);
            }
        });
        this.oirs().getRawValue().map((v, i) => {
            if (v) {
                poo.oirs.push({id: this.oirIds().controls[i].value, oir: this.oirNames().getRawValue()[i]} as Oir);
            }
        });
        this.deletedOirs().getRawValue().map((v, i) => {
            if (v) {
                poo.oirs.push({id: this.deletedOirIds().controls[i].value, oir: this.deletedOirNames().getRawValue()[i]} as Oir);
            }
        });
        (this.pooForm.value.selectedFrs as []).map(v => {
            poo.frs.push(v);
        });
        this.pooService.save(poo, this.project.id)
            .subscribe(
                () => {
                    this.closed.emit({updated: true, dialog: IrGraphDialogs.OO_DIALOG});
                },
                error => {
                    this.hasError.emit(error);
                }
            );
    }

    delete(): void {
        const modalRef = this.activeModal.open(IrgraphDeleteDialogComponent, {centered: true});
        modalRef.componentInstance.title = 'Delete Project Organisational Objective';
        modalRef.componentInstance.message = `Are you sure you want to delete ${this.selectedPoo?.name}`;

        modalRef.closed.toPromise().then(x => {
            if (x && this.selectedPoo) {
                this.pooService.delete(this.selectedPoo.id, this.project.id)
                    .subscribe(
                        () =>
                            this.closed.emit({updated: true, dialog: IrGraphDialogs.OO_DIALOG}),
                        error => {
                            this.hasError.emit(error);
                        });
            }
        });
    }

    closeModal(): void {
         this.closed.emit({updated: false, dialog: IrGraphDialogs.OO_DIALOG});
    }

    oirChange($event: Event): void {
        const target = $event.target || $event.srcElement || $event.currentTarget;
        const checkbox = (target as HTMLInputElement);
        if (checkbox.checked) {
            const index = checkbox.attributes.getNamedItem('index')?.value;
            if (index){
                this.oirs().controls[Number(index)].markAsPristine();
            }
        }
    }

    frChange($event: Event): void {
        const target = $event.target || $event.srcElement || $event.currentTarget;
        const checkbox = (target as HTMLInputElement);
        if (checkbox.checked) {
            const index = checkbox.attributes.getNamedItem('index')?.value;
            if (index) {
                this.linkedFrs().controls[Number(index)].markAsPristine();
            }
        }
    }

    selectedFrsChange($event: Event): void {
        if (this.pooForm.value.selectedFrs.length === 0) {
            this.pooForm.controls.selectedFrs.markAsPristine();
        }
    }

    allowDelete(): boolean {

        if (!this.selectedPoo || this.selectedPoo.id === '') {
            return false;
        }
        return this.selectedPoo?.oirs?.length === 0 && this.selectedPoo?.frs?.length === 0;
    }

    ngOnInit(): void {
        if (this.selectedPoo) {
            this.buildForm();
        }
    }
}

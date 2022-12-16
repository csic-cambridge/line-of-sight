import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {BaseOrganisationalObjectiveService} from '../../services/base/base-organisational-objective-service';
import {BasePermissionService} from '../../services/base/base-permission-service';
import {Oir, OrganisationalObjective} from '../../types/organisational-objective';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {IrgraphDeleteDialogComponent} from '../../information-requirements/irgraph/irgraph-delete-dialog/irgraph-delete-dialog.component';
import {forbiddenNameValidator} from '../../dashboard/copy-project-dialog/copy-project-dialog.component';

@Component({
  selector: 'app-oograph-dialog',
  templateUrl: './oograph-dialog.component.html',
  styleUrls: ['./oograph-dialog.component.scss']
})
export class OographDialogComponent implements OnInit {

    @Output() closed = new EventEmitter<boolean>();
    @Output() hasError = new EventEmitter<any>();
    ooForm = new FormGroup({});
    @Input() activeItem!: OrganisationalObjective;

    constructor(public ooService: BaseOrganisationalObjectiveService,
                public permissionService: BasePermissionService,
                private modalService: NgbModal,
                public activeModal: NgbActiveModal,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.bindForm();
    }
    bindForm(): void {
        this.ooForm = new FormGroup({
            name: this.fb.control(this.activeItem.name, [Validators.required,
                forbiddenNameValidator(this.ooService.getOrganisationalObjectives().value.filter(x => x.id !== this.activeItem.id).map(x => x.name), true)]),
            newOir: this.fb.control(''),
            linkedOOIRNames: this.fb.array([]),
            linkedOOIRs: this.fb.array([]),
            linkedOOIRIds: this.fb.array([])
        });

        this.activeItem?.oirs
            .map(x => this.linkedOOIRs().push(this.fb.control(true)));
        this.activeItem?.oirs
            .map(x => this.linkedOOIRNames().push(this.fb.control(x.oir)));
        this.activeItem?.oirs
            .map(x => this.linkedOOIRIds().push(this.fb.control(x.id)));

    }

    linkedOOIRNames(): FormArray {
        return this.ooForm.get('linkedOOIRNames') as FormArray;
    }

    linkedOOIRs(): FormArray {
        return this.ooForm.get('linkedOOIRs') as FormArray;
    }

    linkedOOIRIds(): FormArray {
        return this.ooForm.get('linkedOOIRIds') as FormArray;
    }

    save(): void {

        if (this.ooForm.invalid) {
            return;
        }
        const updatedOirs: Oir[] = [];

        this.linkedOOIRs().getRawValue().map((v, i) => {
            if (v) {
                updatedOirs.push({id: this.linkedOOIRIds().controls[i].value, oir: this.linkedOOIRNames().controls[i].value});
            }
        });
        if (this.ooForm.value.newOir){
            const newOir = {
                id: '',
                oir: this.ooForm.value.newOir
            };
            updatedOirs.push(newOir);
        }

        const oo = {
            id: this.activeItem.id,
            name: this.ooForm.value.name,
            oirs: updatedOirs.flat(),
            is_deleted: false
        } as OrganisationalObjective;

        this.ooService.save(oo).subscribe(
                (update) => {
                    const all = this.ooService.getOrganisationalObjectives().value;
                    const find = all.find(x => x.id === update.id);
                    if (find) {
                        Object.assign(find, update);
                        this.ooService.getOrganisationalObjectives().next(all);
                    } else {
                        this.ooService.getOrganisationalObjectives().next([...all, ...[update]]);
                    }
                    this.closed.emit(true);
                },
                error => {
                    this.hasError.emit(error);
                }
            );
    }

    close(): void {
        this.closed.emit(false);
    }

    delete(): void {
        if (!this.activeItem) {
            return;
        }
        const modalRef = this.modalService.open(IrgraphDeleteDialogComponent, {centered: true});
        modalRef.componentInstance.title = 'Delete Organisational Objective';
        modalRef.componentInstance.message = `Are you sure you want to delete ${this.activeItem?.name}`;

        modalRef.closed.toPromise().then(x => {
            if (x && this.activeItem) {
                this.ooService.delete(this.activeItem.id ? this.activeItem.id : '')
                    .subscribe(
                        (update) => {
                            const all = this.ooService.getOrganisationalObjectives().value;
                            const filtered = all.filter(x => x.id !== this.activeItem.id);
                            this.ooService.getOrganisationalObjectives().next(filtered);
                            this.closed.emit(true);
                        },
                        error => {
                            this.hasError.emit(error);
                        });
            }
        });
    }

    onOOOIRChange($event: Event): void {

    }
}

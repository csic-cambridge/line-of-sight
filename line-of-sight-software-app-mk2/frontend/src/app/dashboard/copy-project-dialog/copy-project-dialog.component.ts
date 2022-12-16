import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DashboardDialog} from '../../types/dashboard-dialog';
import {Project} from '../../types/project';
import {ProjectService} from '../../services/project.service';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {PermissionService} from '../../services/permission.service';
import {BasePermissionService} from '../../services/base/base-permission-service';
import {BaseProjectService} from '../../services/base/base-project-service';

export function forbiddenNameValidator(names: string[], matchCase: boolean): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const forbidden = names.map(x => matchCase ? x.toUpperCase() : x).includes(matchCase ? control.value?.toUpperCase() : control.value);
        return forbidden ? {forbiddenName: {value: control.value}} : null;
    };
}
@Component({
  selector: 'app-copy-project-dialog',
  templateUrl: './copy-project-dialog.component.html',
  styleUrls: ['./copy-project-dialog.component.scss']
})
export class CopyProjectDialogComponent implements OnInit {

    @Output() closed = new EventEmitter<{ updated: boolean, dialog: DashboardDialog.COPY_PROJECT_DIALOG }>();
    @Output() hasError = new EventEmitter<any>();

    @Input() project!: Project;
    projectForm = new FormGroup({});
    constructor(
        public permissionService: BasePermissionService,
        private fb: FormBuilder,
        private projectService: BaseProjectService) {
    }

    ngOnInit(): void {
        this.projectForm = new FormGroup({
            name: this.fb.control('', [Validators.required,
                forbiddenNameValidator(this.projectService.projects.value.map(x => x.name), true)])
        });
    }

    close(): void {
        this.closed.emit({updated: false, dialog: DashboardDialog.COPY_PROJECT_DIALOG});
    }

    copy(): void {
        if (this.projectForm.invalid) {
            return;
        }
        this.projectService.copy(this.project.id, this.projectForm.controls.name.value)
            .subscribe(
                () => this.closed.emit({updated: true, dialog: DashboardDialog.COPY_PROJECT_DIALOG}),
                error => this.hasError.emit(error)
            );
    }
}

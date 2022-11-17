import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DashboardDialog} from '../../types/dashboard-dialog';
import {Project} from '../../types/project';
import {ProjectService} from '../../services/project.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PermissionService} from '../../services/permission.service';
import {forbiddenNameValidator} from '../copy-project-dialog/copy-project-dialog.component';
import {BasePermissionService} from '../../services/base/base-permission-service';

@Component({
  selector: 'app-rename-project-dialog',
  templateUrl: './rename-project-dialog.component.html',
  styleUrls: ['./rename-project-dialog.component.scss']
})
export class RenameProjectDialogComponent implements OnInit {

    @Output() closed = new EventEmitter<{ updated: boolean, dialog: DashboardDialog.RENAME_PROJECT_DIALOG }>();
    @Output() hasError = new EventEmitter<any>();

    @Input() project!: Project;

    projectForm = new FormGroup({});
    constructor(
        public permissionService: BasePermissionService,
        private fb: FormBuilder,
        private projectService: ProjectService) {
    }

    ngOnInit(): void {
        this.projectForm = new FormGroup({
            name: this.fb.control('', [Validators.required,
                forbiddenNameValidator(this.permissionService.projects.value.map(x => x.name))])
        });
    }
    close(): void {
        this.closed.emit({updated: false, dialog: DashboardDialog.RENAME_PROJECT_DIALOG});
    }

    rename(): void {
        if (this.projectForm.invalid) {
            return;
        }
        this.project.name = this.projectForm.controls.name.value;
        this.projectService.rename(this.project)
            .subscribe(() => {
                    this.closed.emit({updated: true, dialog: DashboardDialog.RENAME_PROJECT_DIALOG});
                },
                error => {
                    this.hasError.emit(error);
                }
            );
    }
}

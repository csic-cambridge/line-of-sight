import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DashboardDialog} from '../../types/dashboard-dialog';
import {forbiddenNameValidator} from '../copy-project-dialog/copy-project-dialog.component';
import {BasePermissionService} from '../../services/base/base-permission-service';
import {BaseIoService} from '../../services/base/base-io-service';
import {AppToastService} from '../../services/app-toast.service';
import {BaseProjectService} from '../../services/base/base-project-service';

@Component({
  selector: 'app-import-project-dialog',
  templateUrl: './import-project-dialog.component.html',
  styleUrls: ['./import-project-dialog.component.scss']
})
export class ImportProjectDialogComponent implements OnInit {

    @Output() closed = new EventEmitter<{ updated: boolean, dialog: DashboardDialog.IMPORT_PROJECT_DIALOG }>();
    @Output() hasError = new EventEmitter<any>();
    @Input() projectData!: string;
    projectForm = new FormGroup({});

    constructor(
        public permissionService: BasePermissionService,
        private fb: FormBuilder,
        public projectService: BaseProjectService,
        private ioService: BaseIoService) {
    }

    ngOnInit(): void {
        let data = undefined;
        try {
            data = JSON.parse(this.projectData);
        }
        catch (err){
            this.hasError.emit({error: {error: err, forUi: true}});
        }
        this.projectForm = new FormGroup({
            id: this.fb.control(''),
            name: this.fb.control(data ? data.project.project_name : '', [Validators.required,
                forbiddenNameValidator(this.projectService.projects.value.map(x => x.name), true)])
        });
        this.projectForm.get('name')?.markAsDirty();
    }

    import(): void {
        if (this.projectForm.invalid) {
            return;
        }

        let data = undefined;
        try {
            data = JSON.parse(this.projectData);
        }
        catch (err){
            this.hasError.emit({error: {error: err, forUi: true}});
            return;
        }

        data.project.project_name = this.projectForm.value.name;
        this.projectData = JSON.stringify(data);

        this.ioService.importProject(this.projectData)
            .then((val) => this.closed.emit({updated: true, dialog: DashboardDialog.IMPORT_PROJECT_DIALOG}))
            .catch(err => this.hasError.emit(err));
    }

    close(): void {
        this.closed.emit({updated: false, dialog: DashboardDialog.IMPORT_PROJECT_DIALOG});
    }
}

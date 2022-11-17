import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Project} from '../../types/project';
import {Observable} from 'rxjs';
import {FunctionalOutputDictionary} from '../../types/functional-output-dictionary';
import {AssetDictionary} from '../../types/asset-dictionary';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DashboardDialog} from '../../types/dashboard-dialog';
import {forbiddenNameValidator} from '../copy-project-dialog/copy-project-dialog.component';
import {BasePermissionService} from '../../services/base/base-permission-service';
import {BaseProjectService} from '../../services/base/base-project-service';
import {AssetDictionaryService} from '../../services/asset-dictionary.service';
import {FunctionalOutputDictionaryService} from '../../services/functional-output-dictionary.service';
import {BaseDictionaryService} from '../../services/base/base-dictionary-service';

@Component({
  selector: 'app-add-project-dialog',
  templateUrl: './add-project-dialog.component.html',
  styleUrls: ['./add-project-dialog.component.scss']
})
export class AddProjectDialogComponent implements OnInit {

    @Output() closed = new EventEmitter<{ updated: boolean, dialog: DashboardDialog.NEW_PROJECT_DIALOG }>();
    @Output() hasError = new EventEmitter<any>();
    projectForm = new FormGroup({});
    assets: Observable<AssetDictionary[]>;
    fos: Observable<FunctionalOutputDictionary[]>;
    constructor(
        public permissionService: BasePermissionService,
        private fb: FormBuilder,
        public projectService: BaseProjectService) {
        this.fos = permissionService.foDataDictionary;
        this.assets = permissionService.assetDataDictionary;
    }

    ngOnInit(): void {
        this.projectForm = new FormGroup({
            id: this.fb.control(''),
            fo_dd_id: this.fb.control('', Validators.required),
            asset_dd_id: this.fb.control('', Validators.required),
            name: this.fb.control('', [Validators.required,
                forbiddenNameValidator(this.permissionService.projects.getValue().map(x => x.name))]),
            import_firs_project_id: this.fb.control(''),
            import_airs_project_id: this.fb.control(''),
        });
    }

    selectableFirProjects(): Project[] {
        return this.permissionService.projects.value.filter(project => project.fo_dd_id === this.projectForm.controls.fo_dd_id.value);
    }
    selectableAirProjects(): Project[] {
        return this.permissionService.projects.value.filter(project => project.asset_dd_id === this.projectForm.controls.asset_dd_id.value);
    }
    createProject(): void {
        if (this.projectForm.invalid) {
            return;
        }

        this.projectService.save(this.projectForm.value)
            .subscribe(
                () => this.closed.emit({updated: true, dialog: DashboardDialog.NEW_PROJECT_DIALOG}),
                error => this.hasError.emit(error)
            );
    }

    close(): void {
        this.closed.emit({updated: false, dialog: DashboardDialog.NEW_PROJECT_DIALOG});
    }
}

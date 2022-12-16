import {Component, EventEmitter, Output} from '@angular/core';
import {DashboardDialogClosedParam} from '../../types/dashboard-dialog-closed-param';
import {DashboardDialog} from '../../types/dashboard-dialog';
import {DictionaryType} from '../../types/dictionary-type';
import {BasePermissionService} from '../../services/base/base-permission-service';
import {AppToastService} from '../../services/app-toast.service';
import {IrgraphAssetDialogComponent} from '../../information-requirements/irgraph/irgraph-asset-dialog/irgraph-asset-dialog.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AddProjectDialogComponent} from '../add-project-dialog/add-project-dialog.component';
import {BaseProjectService} from '../../services/base/base-project-service';

@Component({
  selector: 'app-dashboard-project-button',
  templateUrl: './dashboard-project-button.component.html',
  styleUrls: ['./dashboard-project-button.component.scss']
})
export class DashboardProjectButtonComponent {
    @Output() hasError = new EventEmitter<any>();
    activeDialog = DashboardDialog.NONE;
    importData = '';
    importDictionaryType = DictionaryType.ASSET;
    dictionaryType = DictionaryType;
    dialogTypes = DashboardDialog;
    importFilename: string | undefined;

    constructor(
        public toastr: AppToastService,
        private modalService: NgbModal,
        private projectService: BaseProjectService,
        public permissionService: BasePermissionService) {
    }

    importProject(evt: Event): void {
        const target: DataTransfer = evt.target as unknown as DataTransfer;
        const reader: FileReader = new FileReader();
        reader.onload = (e: any) => {
            this.importData = e.target.result;
            this.activeDialog = DashboardDialog.IMPORT_PROJECT_DIALOG;
        };
        reader.readAsBinaryString(target.files[0]);
    }

    openAddProjectModal(): void {
        const modalRef = this.modalService.open(AddProjectDialogComponent, { scrollable: true, centered: true, size: 'lg' });

        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            this.projectService.getProjects().subscribe(x => {
                this.projectService.projects.next(x);
            });
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.onDialogError($event);
        });
    }

    onDialogClosed($event: DashboardDialogClosedParam): void {
        this.activeDialog = DashboardDialog.NONE;
        switch ($event.dialog) {
            case DashboardDialog.IMPORT_PROJECT_DIALOG: {
                if ($event.updated) {
                    this.toastr.show('Import Project Completed',
                        { classname: 'bg-success text-light', delay: 5000 });
                }
                break;
            }
            case DashboardDialog.IMPORT_DICTIONARY_DIALOG: {
                if ($event.updated) {
                    this.toastr.show('Import Dictionary Completed',
                        { classname: 'bg-success text-light', delay: 5000 });
                }
                break;
            }
            default: {
                break;
            }
        }

        if ($event.updated) {
            this.projectService.getProjects().subscribe(x => {
                this.projectService.projects.next(x);
            });
        }
    }

    importDictionary($event: Event, $dictionary: DictionaryType): void {
        const target: DataTransfer = $event.target as unknown as DataTransfer;
        const reader: FileReader = new FileReader();
        reader.onload = (result: any) => {
            this.importData = result.target.result;
            this.importDictionaryType = $dictionary;
            this.activeDialog = DashboardDialog.IMPORT_DICTIONARY_DIALOG;
        };
        this.importFilename = target.files[0].name;
        reader.readAsBinaryString(target.files[0]);
    }

    onDialogError($event: any): void {
        this.hasError.emit($event);
    }
}

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DashboardDialog} from '../../types/dashboard-dialog';
import {Project} from '../../types/project';
import {BaseProjectService} from '../../services/base/base-project-service';

@Component({
  selector: 'app-delete-project-dialog',
  templateUrl: './delete-project-dialog.component.html',
  styleUrls: ['./delete-project-dialog.component.scss']
})
export class DeleteProjectDialogComponent {

    @Output() closed = new EventEmitter<{ updated: boolean, dialog: DashboardDialog.DELETE_PROJECT_DIALOG }>();
    @Output() hasError = new EventEmitter<any>();

    @Input() project!: Project;

    constructor(private projectService: BaseProjectService) {
    }

    close(): void {
        this.closed.emit({updated: false, dialog: DashboardDialog.DELETE_PROJECT_DIALOG});
    }

    delete(): void {
        this.projectService.delete(this.project.id)
            .subscribe(() => {
                    this.closed.emit({updated: true, dialog: DashboardDialog.DELETE_PROJECT_DIALOG});
                },
                error => {
                    this.hasError.emit(error);
                }
            );
    }
}

import {Component, Inject} from '@angular/core';
import {Router} from '@angular/router';
import {DOCUMENT} from '@angular/common';
import {Project} from '../types/project';
import {ProjectDataService} from '../services/project-data.service';
import {DashboardDialogClosedParam} from '../types/dashboard-dialog-closed-param';
import {DashboardDialog} from '../types/dashboard-dialog';
import {BaseProjectService} from '../services/base/base-project-service';
import {BasePermissionService} from '../services/base/base-permission-service';
import {AppToastService} from '../services/app-toast.service';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent{
    constructor(@Inject(DOCUMENT) document: any,
                private router: Router,
                public toastr: AppToastService,
                private projectService: BaseProjectService,
                private sanitizer: DomSanitizer,
                private projectDataService: ProjectDataService,
                public permissionService: BasePermissionService) {
    }
    activeProject: Project | undefined;
    activeDialog = DashboardDialog.NONE;
    public dialogTypes = DashboardDialog;
    openDialog(project: Project, dialog: DashboardDialog): void {
        this.activeProject = project;
        this.activeDialog = dialog;
    }
    getHtml(html: string): SafeHtml {
        return this.sanitizer.bypassSecurityTrustHtml(html);
    }
    goToGraph(project: Project): void {
        this.projectDataService.setProject(project);
        this.router.navigate(['/project']);
    }

    handleRestError(error: any): void {
        console.log(JSON.parse(error.error));
        let msg = '';
        if (error.status === 403) {
            msg = 'You do not have permission to access the function you have requested';
        }
        else if (JSON.parse(error.error).forUi === true) {
            msg = JSON.parse(error.error).error;
        }
        else {
            /* Treat as generic backend error */
            msg = 'There was an error processing your request, please check and try again';
        }
        if (msg !== '') {
            this.toastr.show(msg,
                { classname: 'bg-danger text-light', delay: 5000 });
        }
    }

    onDialogError($event: any): void {
        this.handleRestError($event);
    }

    onDialogClosed($event: DashboardDialogClosedParam): void {
        this.activeDialog = DashboardDialog.NONE;
        this.activeProject = undefined;

        if ($event.updated && [DashboardDialog.COPY_PROJECT_DIALOG, DashboardDialog.DELETE_PROJECT_DIALOG].includes($event.dialog)) {
            this.permissionService.Reload();
        }
    }

}

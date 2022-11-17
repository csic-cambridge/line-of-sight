import {Component, Input} from '@angular/core';
import * as pako from 'pako';
import {Project} from '../../types/project';
import {BasePermissionService} from '../../services/base/base-permission-service';
import {BaseIoService} from '../../services/base/base-io-service';
import {AppToastService} from '../../services/app-toast.service';

@Component({
  selector: 'app-project-export-button',
  templateUrl: './project-export-button.component.html',
  styleUrls: ['./project-export-button.component.scss']
})
export class ProjectExportButtonComponent{
    @Input() project!: Project;
    constructor(
        public toastr: AppToastService,
        public permissionService: BasePermissionService,
        private ioService: BaseIoService) {
    }

    exportProject(event: MouseEvent): void {
        this.ioService.exportProject(this.project.id)
            .then(data => {
                const charData = atob(data.body).split('').map((x) => {
                    return x.charCodeAt(0);
                });
                const input = new Uint8Array(charData);
                const output = pako.ungzip(input, {to: 'string'});
                const blob = new Blob([output], {type: 'application/octet-stream'});
                const fileLink = document.createElement('a');
                fileLink.href = window.URL.createObjectURL(blob);
                fileLink.download = `${this.project.name.replace(/[^a-zA-Z0-9]/g, '')}.los`;
                fileLink.click();

                this.toastr.show('Export Project Completed',
                    { classname: 'bg-success text-light', delay: 5000 });
            }, err => {
                this.toastr.show('Export Project Failed',
                    { classname: 'bg-error text-light', delay: 5000 });
            });
    }
}

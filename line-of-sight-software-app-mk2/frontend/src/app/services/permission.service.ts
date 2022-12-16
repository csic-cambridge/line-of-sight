import { Injectable } from '@angular/core';
import {UserPermissions} from '../types/user-permissions';
import {BasePermissionService} from './base/base-permission-service';
import {BaseMeService} from './base/base-me-service';
import {BaseProjectService} from './base/base-project-service';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PermissionService extends BasePermissionService {
    constructor(private projectService: BaseProjectService,
                private meService: BaseMeService) {
        super();
        this.meService.getUserPermissions().subscribe(x => {
            this.meService.UserPermissions.next(x);
        });
    }

    accountChange(data: UserPermissions): void {
        this.meService.UserPermissions.next(data);
    }

    permissionDisabled(id: string, permissionId: string): boolean {
        if (this.meService.User.value.is_super_user === true) {
            return false;
        }
        let disabled = true;
        if (!this.meService.ProjectPermissions.value.find(x => x.project_id.toString() === id)) {
            this.meService.getProjectPermissions(id).subscribe(x => {
            });
        }
        const project = this.meService.ProjectPermissions.value.find(p => p.project_id.toString() === id);
        if (project) {
            const permission = project?.permissions.find(p => p.id.toString() === permissionId);
            disabled = permission ? !permission.is_granted : true;
        }
        return disabled;
    }

    userPermissionDisabled(permissionId: string): boolean {
        if (this.meService.User.value.is_super_user) {
            return false;
        }
        let disabled = true;

        if (this.meService.UserPermissions.value.user_id === undefined) {
            this.meService.getUserPermissions().pipe(
                map(userPermissions => {
                    const d = userPermissions.permissions.find(p => p.id.toString() === this.UPIds.VIEW_OOS);
                    disabled = d ? !d.is_granted : true;
                }));
        }
        else {
            if (this.meService.UserPermissions.value.permissions !== undefined) {
                const d = this.meService.UserPermissions.value.permissions.find(p => p.id.toString() === permissionId);
                disabled = d ? !d.is_granted : true;
            }

        }
        return disabled;

    }
}

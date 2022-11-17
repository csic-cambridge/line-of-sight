import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {UserPermissions} from '../types/user-permissions';
import {Permission} from '../types/permission';
import {ProjectPermissions} from '../types/project-permissions';
import {BasePermissionService} from './base/base-permission-service';
import {BaseMeService} from './base/base-me-service';
import {BaseProjectService} from './base/base-project-service';
import {AssetDictionaryService} from './asset-dictionary.service';
import {FunctionalOutputDictionaryService} from './functional-output-dictionary.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionService extends BasePermissionService {
    private userPermissions: BehaviorSubject<UserPermissions> = new BehaviorSubject<UserPermissions>({} as UserPermissions);
    private projectPermissions: BehaviorSubject<ProjectPermissions[]> = new BehaviorSubject<ProjectPermissions[]>([]);

    constructor(private assetDdeService: AssetDictionaryService,
                private foDdeService: FunctionalOutputDictionaryService,
                private projectService: BaseProjectService,
                private meService: BaseMeService) {
        super();
        this.Reload();
    }

    accountChange(data: UserPermissions): void {
        this.userPermissions.next(data);
    }

    permissionDisabled(id: string, permissionId: string): boolean {
        if (this.loggedInUser.getValue().is_super_user === true) {
            return false;
        }
        let disabled = true;
        this.projectPermissions
            .subscribe(projectPermission => {
                const permissions: ProjectPermissions | undefined = projectPermission?.find(permission => permission.project_id === id);

                if (permissions) {
                    const permission = permissions.permissions.find(p => p.id.toString() === permissionId);
                    disabled = permission ? !permission.is_granted : true;
                }
            });
        return disabled;
    }

    userPermissionDisabled(permissionId: string): boolean {
        if (this.loggedInUser.value.is_super_user) {
            return false;
        }
        let disabled = true;
        if (this.userPermissions) {
            const d: Permission | undefined = this.userPermissions.value.permissions?.find(p => p.id.toString() === permissionId);
            disabled = d ? !d.is_granted : true;
        }
        return disabled;
    }

    Reload(): void {
        this.meService.getMe()
            .subscribe(user => {
                this.loggedInUser.next(user);
                this.meService.getUserPermissions().subscribe(userPermissions => {
                    this.userPermissions.next(userPermissions);
                    if (!this.userPermissionDisabled(this.UPIds.VIEW_PROJECTS_DASHBOARD)) {
                        this.projectService.getProjects().subscribe(projects => {
                            this.projects.next(projects);
                            const permissions: ProjectPermissions[] = [];
                            projects.forEach(project => {
                                this.meService.getProjectPermissions(project.id)
                                    .subscribe(permission => permissions.push(permission));
                            });
                            this.projectPermissions.next(permissions);

                            if (!this.userPermissionDisabled(this.UPIds.ADD_COPY_RENAME_PROJECTS)
                                || !this.userPermissionDisabled(this.UPIds.EDIT_DDS)) {

                                this.foDdeService.getDictionaries()
                                    .subscribe(dict => {
                                        console.log(dict);
                                        this.foDataDictionary.next(dict);
                                    });
                                this.assetDdeService.getDictionaries()
                                    .subscribe(dict => {
                                        this.assetDataDictionary.next(dict);
                                    });
                            }
                        });
                    }
                });
            });
    }
}

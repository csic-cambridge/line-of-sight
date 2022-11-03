import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {UserPermissions} from '../user-permissions';
import {MeService} from '../me.service';
import {Permission} from '../permission';
import {User} from '../user';
import {ProjectPermissions} from '../project-permissions';
import {ProjectService} from '../project.service';
import {ProjectPermissionIds} from '../project-permission-ids';
import {UserPermissionIds} from '../user-permission-ids';
import {Project} from '../project';
import {AssetDictionaryService} from "../asset-dictionary.service";
import {FunctionalOutputDictionaryService} from "../functional-output-dictionary.service";
import {AssetDictionary} from "../asset-dictionary";
import {FunctionalOutputDictionary} from "../functional-output-dictionary";

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
    private userPermissions: BehaviorSubject<UserPermissions> = new BehaviorSubject<UserPermissions>({} as UserPermissions);
    public foDataDictionary: BehaviorSubject<FunctionalOutputDictionary[]> = new BehaviorSubject<FunctionalOutputDictionary[]>([]);
    public assetDataDictionary: BehaviorSubject<AssetDictionary[]> = new BehaviorSubject<AssetDictionary[]>([]);
    private projectPermissions: BehaviorSubject<ProjectPermissions[]> = new BehaviorSubject<ProjectPermissions[]>([]);
    public projects: BehaviorSubject<Project[]> = new BehaviorSubject<Project[]>([]);
    public loggedInUser: BehaviorSubject<User> = new BehaviorSubject<User>({} as User);
    public PPIds = ProjectPermissionIds;
    public UPIds = UserPermissionIds;
    constructor(private assetDdeService: AssetDictionaryService, private foDdeService: FunctionalOutputDictionaryService, private projectService: ProjectService, private meService: MeService) {
        this.Reload();
    }

    accountChange(data: UserPermissions): void {
        this.userPermissions.next(data);
    }

    permissionDisabled(id: string, permissionId: string): boolean {
        if (this.loggedInUser.value.is_super_user === true) {
            return false;
        }
        let disabled = true;
        this.projectPermissions
            .subscribe(projectPermission => {
                const permissions: ProjectPermissions | undefined = projectPermission?.find(permission => permission.project_id === id);

                if (permissions){
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
                    if (!this.userPermissionDisabled(this.UPIds.VIEW_PROJECTS_DASHBOARD)){
                        this.projectService.getProjects().subscribe(projects => {
                            this.projects.next(projects);
                            const permissions: ProjectPermissions[] = [];
                            projects.forEach(project => {
                                this.meService.getProjectPermissions(project.id)
                                    .subscribe(permission => permissions.push(permission));
                            });
                            this.projectPermissions.next(permissions);

                            if (!this.userPermissionDisabled(this.UPIds.ADD_COPY_RENAME_PROJECTS)
                                || !this.userPermissionDisabled(this.UPIds.EDIT_DDS)){

                                this.foDdeService.getFunctionalOutputDictionaries()
                                    .subscribe(fo => {
                                        this.foDataDictionary.next(fo);
                                    });
                                this.assetDdeService.getAssetDictionaries()
                                    .subscribe(asset => {
                                        this.assetDataDictionary.next(asset);
                                    });
                            }
                        });
                    }
                });
            });
    }
}

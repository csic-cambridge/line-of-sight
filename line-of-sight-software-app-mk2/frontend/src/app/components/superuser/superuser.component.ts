import {AfterViewInit, Component, Inject, OnInit, ViewChild, ElementRef} from '@angular/core';
import {Router} from '@angular/router';
import {DOCUMENT} from '@angular/common';
import {User} from '../../types/user';
import {UserService} from '../../services/user.service';
import {Project} from '../../types/project';
import {Permission} from '../../types/permission';
import {UserPermissions} from '../../types/user-permissions';
import {UserPermissionService} from '../../services/user-permission.service';
import {ProjectPermissions} from '../../types/project-permissions';
import {ProjectPermissionService} from '../../services/project-permission.service';
import {BaseProjectService} from '../../services/base/base-project-service';
import {IMultiSelectOption, IMultiSelectSettings, IMultiSelectTexts} from 'ngx-bootstrap-multiselect';


@Component({
    selector: 'app-superuser',
    templateUrl: './superuser.component.html',
    styleUrls: ['./superuser.component.scss']
})
export class SuperuserComponent implements OnInit, AfterViewInit {

    constructor(@Inject(DOCUMENT) document: any, private router: Router,
                private userService: UserService,
                private projectService: BaseProjectService,
                private userPermissionService: UserPermissionService,
                private projectPermissionService: ProjectPermissionService) {
    }

    users: Array<User> = [];
    selectedUser: User | undefined = undefined;
    userPermissions: UserPermissions | undefined = undefined;
    projects: Array<Project> = [];
    selectedProject: Project | undefined = undefined;
    projectPermissions: ProjectPermissions | undefined = undefined;

    projectPermissionsIsOpen = false;
    selectedSuperUserStatus = false;
    selectedUserPermissions: Array<string> = [];
    selectedProjectPermissions: Array<string> = [];
    isChangeSelectedForSuperUserStatus = false;
    isChangeSelectedForUserPermissions = false;
    isChangeSelectedForProjectPermissions = false;

    userOptions: IMultiSelectOption[] = [];
    userTexts: IMultiSelectTexts = {defaultTitle: 'Select user', searchEmptyResult: 'No users found ...'};
    mySettings: IMultiSelectSettings = {buttonClasses: 'form-control element-text', enableSearch: true, dynamicTitleMaxItems: 1,
                                        selectionLimit: 1, autoUnselect: true, closeOnSelect: true};
    selectedUserArray: Array<String> = [];
    usersArray: Array<User> = [];

    private errorMessage: ((error: any) => void) | null | undefined;

    @ViewChild('elProjectName') elProjectName!: ElementRef;

    ngOnInit(): void {
    }
    ngAfterViewInit(): void {
        this.getUsers();
        this.getProjects();
    }

    getUsers(): void {
        this.userService.getUsers()
            .subscribe(users => {
                this.users = users;
                this.userOptions = this.users
                       .map((user) => {
                           return {id: user.user_id, name: user.is_super_user === true ? user.email_address + ' - Super User' : user.email_address};
                       });
            });
    }

    getProjects(): void {
        this.projectService.getProjects()
            .subscribe(projects => {
                this.projects = projects;
            });
    }

    userChange(selectedUserId: string): void {
        if (selectedUserId){
            this.selectedUser = this.users.find(user => user.user_id == selectedUserId);
            if (this.selectedUser !== undefined){
                this.setSuperUserStatus();
                this.getUserPermissions();
            }
        }
    }

    projectChange(e: any): void {
        const projectNameString = e.target.value;
        this.selectedProject = this.projects.find(project => project.name === projectNameString);
        this.getProjectPermissions();
        this.enableFormContent(projectNameString, this.elProjectName);
    }

    getUserPermissions(): void {
        if (this.selectedUser !== undefined) {
            this.userPermissionService.getPermissions(this.selectedUser.user_id)
                .subscribe(userPermissions => {
                    this.userPermissions = userPermissions;
                    this.setSelectedUserPermissions(userPermissions);
                });
        }
    }

    getProjectPermissions(): void {
        if (this.selectedUser !== undefined && this.selectedProject !== undefined) {
            this.projectPermissionService.getPermissions(this.selectedUser.user_id, this.selectedProject.id)
                .subscribe(projectPermissions => {
                    this.projectPermissions = projectPermissions;
                    this.setSelectedProjectPermissions(projectPermissions);
                });
        }
    }

    setSuperUserStatus(): void {
        if (this.selectedUser !== undefined){
            this.selectedSuperUserStatus = this.selectedUser.is_super_user;
            this.updateIsChangeSelectedForSuperUserStatus();
        }
    }

    setSelectedUserPermissions(userPermissions: UserPermissions): void {
        this.selectedUserPermissions = [];
        for (const permission of userPermissions.permissions){
            if (!this.selectedUserPermissions.includes(permission.name) && permission.is_granted === true){
                this.selectedUserPermissions.push(permission.name);
            }
        }
        this.updateIsChangeSelectedForUserPermissions();
    }

    setSelectedProjectPermissions(projectPermissions: ProjectPermissions): void {
        this.selectedProjectPermissions = [];
        for (const permission of projectPermissions.permissions){
            if (!this.selectedProjectPermissions.includes(permission.name) && permission.is_granted === true){
                this.selectedProjectPermissions.push(permission.name);
            }
        }
        this.updateIsChangeSelectedForProjectPermissions();
    }

    updateIsChangeSelectedForSuperUserStatus(): void {
        if (this.selectedUser !== undefined){
            this.isChangeSelectedForSuperUserStatus = this.selectedUser.is_super_user !== this.selectedSuperUserStatus;
        } else {
            this.isChangeSelectedForSuperUserStatus = false;
        }
    }

    updateIsChangeSelectedForUserPermissions(): void {
        if (this.userPermissions !== undefined) {
            this.isChangeSelectedForUserPermissions =
                this.checkIfChangeSelectedForPermissions(this.userPermissions.permissions, this.selectedUserPermissions);
        } else {
            this.isChangeSelectedForUserPermissions = false;
        }
    }

    updateIsChangeSelectedForProjectPermissions(): void {
        if (this.projectPermissions !== undefined) {
            this.isChangeSelectedForProjectPermissions =
                this.checkIfChangeSelectedForPermissions(this.projectPermissions.permissions, this.selectedProjectPermissions);
        } else {
            this.isChangeSelectedForProjectPermissions = false;
        }
    }

    saveSuperUserStatus(): void {
        if (this.selectedUser !== undefined && this.isChangeSelectedForSuperUserStatus) {
            let alertMsg = '';
            if (this.selectedSuperUserStatus) {
                alertMsg = 'Confirm granting Super User status to user: ' + this.selectedUser.email_address;
            } else{
                alertMsg = 'Confirm taking away Super User status from user: ' + this.selectedUser.email_address;
            }
            if (confirm(alertMsg)) {
                const userToSave: User = this.selectedUser;
                userToSave.is_super_user = this.selectedSuperUserStatus;

                this.userService.save(userToSave)
                    .subscribe(
                        () => {
                            this.updateIsChangeSelectedForSuperUserStatus();

                            // Get Users to reflect change
                            this.getUsers();
                        },
                        error => {
                            this.handleRestError(error);
                        }
                    );
            }
        }
    }

    saveUserPermissions(): void {
        if (this.userPermissions !== undefined && this.isChangeSelectedForUserPermissions){
            const userPermissionsToSave: UserPermissions = this.userPermissions;
            const newPermissions: Permission[] = [];

            for (const permission of userPermissionsToSave.permissions){
                const newPermission = permission;
                if (this.selectedUserPermissions.includes(permission.name)){
                    newPermission.is_granted = true;
                } else {
                    newPermission.is_granted = false;
                }
                newPermissions.push(newPermission);
            }
            userPermissionsToSave.permissions = newPermissions;

            this.userPermissionService.save(userPermissionsToSave)
                .subscribe(
                    () => {
                        this.updateIsChangeSelectedForUserPermissions();
                    },
                    error => {
                        this.handleRestError(error);
                    }
                );
        }
    }

    saveSuperUserStatusAndUserPermissions(value: any): void {

        this.saveSuperUserStatus();

        // Do not save User Permissions if Super User status is true
        if (!this.selectedSuperUserStatus){
            this.saveUserPermissions();
        }

    }

    saveProjectPermissions(value: any): void {

        if (this.projectPermissions !== undefined && this.isChangeSelectedForProjectPermissions){

            const projectPermissionsToSave: ProjectPermissions = this.projectPermissions;
            const newPermissions: Permission[] = [];

            for (const permission of projectPermissionsToSave.permissions){
                const newPermission = permission;
                if (this.selectedProjectPermissions.includes(permission.name)){
                    newPermission.is_granted = true;
                } else {
                    newPermission.is_granted = false;
                }
                newPermissions.push(newPermission);
            }
            projectPermissionsToSave.permissions = newPermissions;

            this.projectPermissionService.save(projectPermissionsToSave)
                .subscribe(
                    () => {
                        this.updateIsChangeSelectedForProjectPermissions();
                    },
                    error => {
                        this.handleRestError(error);
                    }
                );

        }
    }

    checkIfChangeSelectedForPermissions(permissions: Permission[], selectedPermissionList: string[]): boolean {
        for (const permission of permissions){
            if (permission.is_granted !== selectedPermissionList.includes(permission.name)){
                return true;
            }
        }
        return false;
    }

    onSuperUserStatusChange(): void {
        this.selectedSuperUserStatus = !this.selectedSuperUserStatus;

        if (this.selectedUser !== undefined){
            this.isChangeSelectedForSuperUserStatus = this.selectedUser.is_super_user !== this.selectedSuperUserStatus;
        }
    }

    onUserPermissionsChange(value: string): void {
        if (this.selectedUserPermissions.includes(value)) {
            this.selectedUserPermissions = this.selectedUserPermissions.filter((item) => item !== value);
        } else {
            this.selectedUserPermissions.push(value);
        }

        this.updateIsChangeSelectedForUserPermissions();
    }

    onProjectPermissionChange(value: string): void {
        if (this.selectedProjectPermissions.includes(value)) {
            this.selectedProjectPermissions = this.selectedProjectPermissions.filter((item) => item !== value);
        } else {
            this.selectedProjectPermissions.push(value);
        }

        this.updateIsChangeSelectedForProjectPermissions();
    }

    openProjectModal(isOpen: boolean): void {
        if (isOpen) {
            if (this.selectedUser !== undefined && this.selectedUser.is_super_user !== this.selectedSuperUserStatus){
                const alertMsg = 'You have unsaved changes to Super User Status. Do you want to save them?';
                if (confirm(alertMsg)){
                    this.saveSuperUserStatus();
                    this.projectPermissionsIsOpen = isOpen;
                }
            } else {
                this.projectPermissionsIsOpen = isOpen;
            }
        } else if (!isOpen) {
            this.projectPermissionsIsOpen = isOpen;
            this.clearProjectContent();
        }
    }

    clearUserContent(): void {
        this.selectedUser = undefined;
        this.userPermissions = undefined;
        this.selectedUserArray = [];
        this.updateIsChangeSelectedForSuperUserStatus();
        this.updateIsChangeSelectedForUserPermissions();
    }

    clearProjectContent(): void {
        this.selectedProject = undefined;
        this.projectPermissions = undefined;
        this.resetFormContentState(this.elProjectName);
        this.updateIsChangeSelectedForProjectPermissions();
    }

    enableFormContent(selectedEntityName: string, el: ElementRef): void {
        el.nativeElement.value = selectedEntityName;
        el.nativeElement.classList.remove('is-invalid');
    }

    resetFormContentState(el: ElementRef): void {
        el.nativeElement.value = '';
    }

    reloadComponent(): void {
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.router.onSameUrlNavigation = 'reload';
        this.router.navigate([this.router.url]);
    }

    handleRestError(error: any): void {
        this.errorMessage = error.message;
        let msg = '';
        if (error.error !== undefined && error.error.forUi === true) {
            msg = error.error.error;
        }
        else {
            msg = 'There was an error processing your request, please check and try again';
        }
        if (msg !== '') {
            window.alert(msg);
        }
        this.reloadComponent();
    }
}

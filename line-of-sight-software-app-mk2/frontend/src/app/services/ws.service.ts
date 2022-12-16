import { Injectable,EventEmitter } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { catchError, tap, switchAll } from 'rxjs/operators';
import { EMPTY, Subject } from 'rxjs';
import {environment} from '../../environments/environment';
import {BasePermissionService} from './base/base-permission-service';
import {WebSocketMessage} from '../types/web-socket-message';
import {ProjectDataService} from './project-data.service';
import {BaseOrganisationalObjectiveService} from './base/base-organisational-objective-service';
import {ProjectOrganisationalObjectiveService} from './project-organisational-objective.service';
import {FunctionalRequirementService} from './functional-requirement.service';
import {FunctionalOutputService} from './functional-output.service';
import {AssetService} from './asset.service';
import {BaseProjectService} from './base/base-project-service';
import {BaseMeService} from './base/base-me-service';
import {UserPermissions} from '../types/user-permissions';
import {User} from '../types/user';

export const WS_ENDPOINT = environment.wsEndpoint;

@Injectable({
  providedIn: 'root'
})
export class WsService {
    private socket$: WebSocketSubject<any> | undefined;
    private messagesSubject$ = new Subject();
    // @ts-ignore
    public messages$ = this.messagesSubject$.pipe(switchAll(), catchError(e => { throw e; }));
    ReloadProject = new EventEmitter();
    constructor(private permissionService: BasePermissionService,
                private projectDataService: ProjectDataService,
                private pooService: ProjectOrganisationalObjectiveService,
                private frService: FunctionalRequirementService,
                private foService: FunctionalOutputService,
                private assetService: AssetService,
                public projectService: BaseProjectService,
                private organisationalObjectiveService: BaseOrganisationalObjectiveService,
                private meService: BaseMeService) {

    }

    public connect(): void {
        console.log('connect', this.socket$);
        if (!this.socket$ || this.socket$.closed) {
            this.socket$ = this.getNewWebSocket();
            this.socket$.subscribe({
                next: msg => {

                    setTimeout(() => {
                        this.processMessage(msg);
                        }, 500);

                },
                error: err => console.log(err), // Called if at any point WebSocket API signals some kind of error.
                complete: () => console.log('complete') // Called when connection is closed (for whatever reason).
            });
        }
    }

    private getNewWebSocket(): WebSocketSubject<any> {
        return webSocket(WS_ENDPOINT);
    }

    sendMessage(msg: any): void {
        console.log(msg);
        // @ts-ignore
        this.socket$.next(msg);
    }
    close(): void {
        // @ts-ignore
        this.socket$.complete();
    }

    private processMessage(msg: WebSocketMessage): void {
        console.log('message received: ', msg);
        switch (msg.type) {
            case 100: {
                if (msg.userId !== this.meService.User.value.user_id) {
                    break;
                }
                this.meService.User.next({} as User);
                this.meService.getMe().subscribe(x => {
                });
                this.meService.UserPermissions.next({} as UserPermissions);
                this.meService.getUserPermissions().subscribe(x => {
                    this.meService.UserPermissions.next(x);
                    if (!this.permissionService.userPermissionDisabled(this.permissionService.UPIds.VIEW_PROJECTS_DASHBOARD)){
                        this.projectService.getProjects().subscribe(x => {
                            this.projectService.projects.next(x);
                        });
                    }
                });
                break;
            }
            case 200: {
                if (msg.userId !== this.meService.User.value.user_id) {
                    break;
                }
                const excluded = this.meService.ProjectPermissions.value.filter(x => x.project_id !== msg.projectId);
                this.meService.ProjectPermissions.next(excluded);
                break;
            }
            case 300: {
                if (!this.permissionService.userPermissionDisabled(this.permissionService.UPIds.VIEW_OOS)) {
                    this.organisationalObjectiveService.reload();
                }
                if (location.pathname === '/project') {
                    this.pooService.loadProjectOrganisationalObjectives(this.projectDataService.getProject().id);
                    this.ReloadProject.emit();
                }
                break;
            }
            case 400:
            case 410:
            case 420: {
                this.projectService.getProjects().subscribe(x => {
                    this.projectService.projects.next(x);
                });
                break;
            }
            case 500:
            case 510: {
                this.projectService.getProjects().subscribe(x => {
                    this.projectService.projects.next(x);
                });
                if (location.pathname === '/project') {
                    this.frService.loadFunctionalRequirements(this.projectDataService.getProject().id);
                    this.pooService.loadProjectOrganisationalObjectives(this.projectDataService.getProject().id);
                    this.foService.loadFunctionalOutputs(this.projectDataService.getProject().id);
                    this.assetService.loadAssets(this.projectDataService.getProject().id);
                    this.ReloadProject.emit();
                }
                break;
            }
        }

        setTimeout(() => {

            if (location.pathname === '/oograph' && this.permissionService.userPermissionDisabled(this.permissionService.UPIds.VIEW_OOS)) {
                location.href = '/dashboard';
            }
            if (location.pathname === '/superuser' && !this.meService.User.value.is_super_user) {
                location.href = '/dashboard';
            }

            if (location.pathname === '/project') {
                if (this.permissionService.permissionDisabled(this.projectDataService.getProject().id, this.permissionService.PPIds.VIEW_PROJECT_GRAPH)) {
                    location.href = '/dashboard';
                }
            }
        }, 1500);
    }
}

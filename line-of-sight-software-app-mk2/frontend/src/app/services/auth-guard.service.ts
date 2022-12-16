import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {BaseMeService} from './base/base-me-service';
import {BasePermissionService} from './base/base-permission-service';
import {ProjectDataService} from './project-data.service';
import {Observable, of} from 'rxjs';
import {map, take} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

    constructor(public auth: BaseMeService,
                public router: Router,
                private permissionService: BasePermissionService,
                private projectDataService: ProjectDataService,
                private meService: BaseMeService) {

    }

    canActivate(next: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): | boolean
        | Observable<boolean | UrlTree> {
        let enabled = true;
        console.log('canActivate');

        this.auth.getMe().subscribe(() => {},
            error => {
                this.router.navigate(['/login']);
            });

        switch (state.url) {
            case '/oograph':
                this.meService.getUserPermissions().pipe(
                    map(userPermissions => {
                        const d = userPermissions.permissions.find(p => p.id.toString() === this.permissionService.UPIds.VIEW_OOS);
                        enabled = d ? !d.is_granted : true;
                    }));
                return of(enabled);
                break;
            case '/superuser':

                this.meService.getMe().pipe(
                    map(userPermissions => {
                        enabled = this.meService.User.value.is_super_user;
                    }));
                return of(enabled);
                break;
            case '/project':
                const project = this.projectDataService.getProject();
                if (project.id === undefined) {
                    enabled = false;
                } else {

                    this.meService.getProjectPermissions(project.id).pipe(
                        map(userPermissions => {
                            enabled = !this.permissionService.permissionDisabled(project.id,
                                this.permissionService.PPIds.VIEW_PROJECT_GRAPH);
                        }));
                }
                return of(enabled);
                break;
            default:
                return of(enabled);
                break;
        }
    }

    private checkOO(): | boolean
        | Observable<boolean | UrlTree> {
        return this.meService.getUserPermissions().pipe(
            map(userPermissions => {
                const d = userPermissions.permissions.find(p => p.id.toString() === this.permissionService.UPIds.VIEW_OOS);
                return d ? !d.is_granted : true;
            })
        );
    }
}

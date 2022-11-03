import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from './authentication.service';
import {ProjectDataService} from './project-data.service';
import {PermissionService} from './services/permission.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
    title = 'cdbb';
    constructor(private router: Router,
                private authService: AuthenticationService,
                private projectDataService: ProjectDataService,
                public permissionService: PermissionService){
    }

    getRouter(): Router {
        return this.router;
    }

    logout(): void {
        this.authService.logout().subscribe(
            () => {
                this.authService.login();
            },
            error  => {
                this.authService.login();
            }
        );
    }

    goToProjectsDashboard(): void {
        this.router.navigate(['/dashboard']);
    }

    goToOOGraph(): void {
        this.router.navigate(['/oograph']);
    }

    goToSuperuserDashboard(): void {
        this.router.navigate(['/superuser']);
    }

    getProjectName(): string {
        return this.projectDataService.getProject().name;
    }

    getLoggedInUsername(): string {
        return this.permissionService.loggedInUser.value === undefined ? '' :
            `${this.permissionService.loggedInUser.value.email_address} ${this.permissionService.loggedInUser.value.is_super_user ? '*' : ''}`;
    }
}

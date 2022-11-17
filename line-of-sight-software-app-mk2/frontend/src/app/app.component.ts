import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ProjectDataService} from './services/project-data.service';
import {BasePermissionService} from './services/base/base-permission-service';
import {BaseAuthenticationService} from './services/base/base-authentication.service';
import {AppToastService} from './services/app-toast.service';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

    title = 'cdbb';
    constructor(private router: Router,
                public toastr: AppToastService,
                private sanitizer: DomSanitizer,
                private authService: BaseAuthenticationService,
                private projectDataService: ProjectDataService,
                public permissionService: BasePermissionService){
    }

    getHtml(html: string): SafeHtml {
        return this.sanitizer.bypassSecurityTrustHtml(html);
    }
    ngOnInit(): void {
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

    getProjectName(): string {
        return this.projectDataService.getProject().name;
    }

    getLoggedInUsername(): string {
        return this.permissionService.loggedInUser.value === undefined ? '' :
            `${this.permissionService.loggedInUser.value.email_address} ${this.permissionService.loggedInUser.value.is_super_user ? '*' : ''}`;
    }
}

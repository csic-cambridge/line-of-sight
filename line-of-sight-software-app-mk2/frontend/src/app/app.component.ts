import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ProjectDataService} from './services/project-data.service';
import {BasePermissionService} from './services/base/base-permission-service';
import {BaseAuthenticationService} from './services/base/base-authentication.service';
import {AppToastService} from './services/app-toast.service';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {WsService} from './services/ws.service';
import {MeService} from './services/me.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {

    title = 'cdbb';
    constructor(private router: Router,
                public toastr: AppToastService,
                private sanitizer: DomSanitizer,
                private authService: BaseAuthenticationService,
                private projectDataService: ProjectDataService,
                public permissionService: BasePermissionService,
                public wsService: WsService,
                public meService: MeService){
    }

    getHtml(html: string): SafeHtml {
        return this.sanitizer.bypassSecurityTrustHtml(html);
    }
    ngOnInit(): void {
        this.wsService.connect();
    }

    ngOnDestroy(): void {
        this.wsService.close();
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
        return this.meService.User.value.user_id === undefined ? '' :
            `${this.meService.User.value.email_address} ${this.meService.User.value.is_super_user ? '*' : ''}`;
    }
}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {BasePermissionService} from './services/base/base-permission-service';
import {BaseAuthenticationService} from './services/base/base-authentication.service';
import {AppToastService} from './services/app-toast.service';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {WsService} from './services/ws.service';
import {environment} from '../environments/environment';
import {BaseProjectDataService} from './services/base/base-project-data-service';
import {BaseMeService} from './services/base/base-me-service';
import {ClearShowcaseDialogComponent} from './components/login/clear-showcase-dialog/clear-showcase-dialog.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {

    title = 'cdbb';
    constructor(private router: Router,
                public toastr: AppToastService,
                public meService: BaseMeService,
                public permissionService: BasePermissionService,
                private sanitizer: DomSanitizer,
                private authService: BaseAuthenticationService,
                private projectDataService: BaseProjectDataService,
                private wsService: WsService,
                private modalService: NgbModal){
    }
    public env = environment;
    getHtml(html: string): SafeHtml {
        return this.sanitizer.bypassSecurityTrustHtml(html);
    }
    ngOnInit(): void {
        if (!this.env.offline) {
            this.wsService.connect();
        }
    }

    ngOnDestroy(): void {
        if (!this.env.offline) {
            this.wsService.close();
        }
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

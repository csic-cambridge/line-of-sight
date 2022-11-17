import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { routing } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {FormsModule, NG_VALIDATORS} from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { HttpAuthInterceptor } from './http-auth.interceptor';
import { LoginComponent } from './components/login/login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AngularDraggableModule } from 'angular2-draggable';
import { NgxBootstrapMultiselectModule } from 'ngx-bootstrap-multiselect';
import { DashboardComponent } from './dashboard/dashboard.component';
import { OOGraphComponent } from './oograph/oograph.component';
import { SuperuserComponent } from './components/superuser/superuser.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { DashboardProjectButtonComponent } from './dashboard/dashboard-project-button/dashboard-project-button.component';
import { ProjectExportButtonComponent } from './dashboard/project-export-button/project-export-button.component';
import { AddProjectDialogComponent } from './dashboard/add-project-dialog/add-project-dialog.component';
import { DeleteProjectDialogComponent } from './dashboard/delete-project-dialog/delete-project-dialog.component';
import { RenameProjectDialogComponent } from './dashboard/rename-project-dialog/rename-project-dialog.component';
import { CopyProjectDialogComponent } from './dashboard/copy-project-dialog/copy-project-dialog.component';
import {ImportProjectDialogComponent} from './dashboard/import-project-dialog/import-project-dialog.component';
import { ImportDictionaryDialogComponent } from './dashboard/import-dictionary-dialog/import-dictionary-dialog.component';
import {BaseAuthenticationService} from './services/base/base-authentication.service';
import {OfflineAuthenticationService} from './services/offline/offline-authentication.service';
import {environment} from '../environments/environment';
import {AuthenticationService} from './services/authentication.service';
import {BaseProjectService} from './services/base/base-project-service';
import {BasePermissionService} from './services/base/base-permission-service';
import {OfflineProjectService} from './services/offline/offline-project.service';
import {ProjectService} from './services/project.service';
import {OfflinePermissionService} from './services/offline/offline-permission.service';
import {PermissionService} from './services/permission.service';
import {BaseMeService} from './services/base/base-me-service';
import {OfflineMeService} from './services/offline/offline-me.service';
import {MeService} from './services/me.service';
import {BaseOrganisationalObjectiveService} from './services/base/base-organisational-objective-service';
import {OfflineOrganisationalObjectiveService} from './services/offline/offline-organisational-objective.service';
import {OrganisationalObjectiveService} from './services/organisational-objective.service';
import { OographDialogComponent } from './oograph/oograph-dialog/oograph-dialog.component';
import {BaseIoService} from './services/base/base-io-service';
import {OfflineIoService} from './services/offline/offline-io.service';
import {IoService} from './services/io.service';
import { ToastContainerComponent } from './components/toast-container/toast-container.component';
import {NgxIndexedDBModule} from 'ngx-indexed-db';
import {IndexedDbConfigHelperHelper} from './helpers/indexed-db-config-helper';
import { OographGridviewComponent } from './oograph/oograph-gridview/oograph-gridview.component';
import { OographListviewComponent } from './oograph/oograph-listview/oograph-listview.component';
import {NgxMasonryModule} from 'ngx-masonry';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        DashboardComponent,
        OOGraphComponent,
        SuperuserComponent,
        NotFoundComponent,
        DashboardProjectButtonComponent,
        ProjectExportButtonComponent,
        AddProjectDialogComponent,
        DeleteProjectDialogComponent,
        RenameProjectDialogComponent,
        CopyProjectDialogComponent,
        ImportProjectDialogComponent,
        ImportDictionaryDialogComponent,
        OographDialogComponent,
        ToastContainerComponent,
        OographGridviewComponent,
        OographListviewComponent
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        RouterModule,
        routing,
        NgbModule, BrowserAnimationsModule,
        NgxMasonryModule,
        FormsModule,
        ReactiveFormsModule,
        AngularDraggableModule,
        NgxBootstrapMultiselectModule, NgxIndexedDBModule.forRoot(IndexedDbConfigHelperHelper.dbConfig)
    ],
    providers: [{provide: HTTP_INTERCEPTORS, useClass: HttpAuthInterceptor, multi: true},
        {provide: BaseAuthenticationService, useExisting: environment.offline ? OfflineAuthenticationService : AuthenticationService },
        {provide: BaseProjectService, useExisting: environment.offline ? OfflineProjectService : ProjectService },
        {provide: BaseMeService, useExisting: environment.offline ? OfflineMeService : MeService },
        {provide: BaseIoService, useExisting: environment.offline ? OfflineIoService : IoService },
        {provide: BaseOrganisationalObjectiveService, useExisting: environment.offline
                ? OfflineOrganisationalObjectiveService : OrganisationalObjectiveService },
        {provide: BasePermissionService, useExisting: environment.offline ? OfflinePermissionService : PermissionService }],
    bootstrap: [AppComponent]
})
export class AppModule { }

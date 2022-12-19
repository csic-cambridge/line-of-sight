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
import {BaseProjectDataService} from './services/base/base-project-data-service';
import {ProjectDataService} from './services/project-data.service';
import {OfflineProjectDataService} from './services/offline/offline-project-data.service';
import {BaseAssetService} from './services/base/base-asset-service';
import {OfflineAssetService} from './services/offline/offline-asset.service';
import {AssetService} from './services/asset.service';
import {BaseAirsService} from './services/base/base-airs-service';
import {AirsService} from './services/airs.service';
import {OfflineAirsService} from './services/offline/offline-airs.service';
import {BaseAssetDictionaryEntryService} from './services/base/base-asset-dictionary-entry-service';
import {AssetDataDictionaryEntryService} from './services/asset-data-dictionary-entry.service';
import {OfflineAssetDataDictionaryEntryService} from './services/offline/offline-asset-data-dictionary-entry.service';
import {BaseFunctionalOutputService} from './services/base/base-functional-output-service';
import {FunctionalOutputService} from './services/functional-output.service';
import {OfflineFunctionalOutputService} from './services/offline/offline-functional-output.service';
import {BaseFunctionalRequirementService} from './services/base/base-functional-requirement-service';
import {OfflineFunctionalRequirementService} from './services/offline/offline-functional-requirement.service';
import {FunctionalRequirementService} from './services/functional-requirement.service';
import {BaseProjectOrganisationalObjectiveService} from './services/base/base-project-organisational-objective-service';
import {OfflineProjectOrganisationalObjectiveService} from './services/offline/offline-project-organisational-objective.service';
import {ProjectOrganisationalObjectiveService} from './services/project-organisational-objective.service';
import {BaseFirsService} from './services/base/base-firs-service';
import {FirsService} from './services/firs.service';
import {OfflineFirsService} from './services/offline/offline-firs.service';
import {BaseFunctionalOutputDictionaryService} from './services/base/base-functional-output-dictionary-service';
import {FunctionalOutputDictionaryService} from './services/functional-output-dictionary.service';
import {OfflineFoDictionaryService} from './services/offline/offline-fo-dictionary.service';
import {BaseFunctionalOutputDictionaryEntryService} from './services/base/base-functional-output-dictionary-entry-service';
import {FunctionalOutputDataDictionaryEntryService} from './services/functional-output-data-dictionary-entry.service';
import {OfflineFoDataDictionaryEntryService} from './services/offline/offline-fo-data-dictionary-entry.service';
import { ClearShowcaseDialogComponent } from './components/login/clear-showcase-dialog/clear-showcase-dialog.component';
import {APP_BASE_HREF, HashLocationStrategy, LocationStrategy} from '@angular/common';


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
        OographListviewComponent,
        ClearShowcaseDialogComponent
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
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: LocationStrategy, useClass: HashLocationStrategy },
        {provide: BaseAuthenticationService, useExisting: environment.offline ? OfflineAuthenticationService : AuthenticationService },
        {provide: BaseProjectService, useExisting: environment.offline ? OfflineProjectService : ProjectService },
        {provide: BaseMeService, useExisting: environment.offline ? OfflineMeService : MeService },
        {provide: BaseAssetDictionaryEntryService, useExisting: environment.offline
                ? OfflineAssetDataDictionaryEntryService : AssetDataDictionaryEntryService },
        {provide: BaseAirsService, useExisting: environment.offline ? OfflineAirsService : AirsService },
        {provide: BaseFirsService, useExisting: environment.offline ? OfflineFirsService : FirsService },
        {provide: BaseFunctionalOutputService, useExisting: environment.offline
                ? OfflineFunctionalOutputService : FunctionalOutputService },
        {provide: BaseFunctionalRequirementService, useExisting: environment.offline
                ? OfflineFunctionalRequirementService : FunctionalRequirementService },
        {provide: BaseProjectOrganisationalObjectiveService, useExisting: environment.offline
                ? OfflineProjectOrganisationalObjectiveService : ProjectOrganisationalObjectiveService },
        {provide: BaseFunctionalOutputDictionaryService, useExisting: environment.offline
                ? OfflineFoDictionaryService : FunctionalOutputDictionaryService },
        {provide: BaseFunctionalOutputDictionaryEntryService, useExisting: environment.offline
                ? OfflineFoDataDictionaryEntryService : FunctionalOutputDataDictionaryEntryService },
        {provide: BaseAssetService, useExisting: environment.offline ? OfflineAssetService : AssetService },
        {provide: BaseProjectDataService, useExisting: environment.offline ? OfflineProjectDataService : ProjectDataService },
        {provide: BaseIoService, useExisting: environment.offline ? OfflineIoService : IoService },
        {provide: BaseOrganisationalObjectiveService, useExisting: environment.offline
                ? OfflineOrganisationalObjectiveService : OrganisationalObjectiveService },
        {provide: BasePermissionService, useExisting: environment.offline ? OfflinePermissionService : PermissionService }],
    bootstrap: [AppComponent]
})
export class AppModule { }

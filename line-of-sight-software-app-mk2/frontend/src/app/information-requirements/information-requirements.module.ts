import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {IRGraphComponent} from './irgraph/irgraph.component';
import {InformationRequirementsRoutingModule} from './information-requirements-routing.module';
import {EntityLinkComponent} from './irgraph/entity-link/entity-link.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AngularDraggableModule} from 'angular2-draggable';
import {NgxBootstrapMultiselectModule} from 'ngx-bootstrap-multiselect';
import { IrgraphAssetDialogComponent } from './irgraph/irgraph-asset-dialog/irgraph-asset-dialog.component';
import { IrgraphFoDialogComponent } from './irgraph/irgraph-fo-dialog/irgraph-fo-dialog.component';
import { IrgraphFrDialogComponent } from './irgraph/irgraph-fr-dialog/irgraph-fr-dialog.component';
import { IrgraphOoDialogComponent } from './irgraph/irgraph-oo-dialog/irgraph-oo-dialog.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { IrgraphDeleteDialogComponent } from './irgraph/irgraph-delete-dialog/irgraph-delete-dialog.component';
import { ConfirmProjectImportDialogComponent } from './irgraph/confirm-project-import-dialog/confirm-project-import-dialog.component';
import {NgxPanZoomModule} from "ngx-panzoom";
import {IrgraphOirDialogComponent} from "./irgraph/irgraph-oir-dialog/irgraph-oir-dialog.component";
import {IrgraphAirDialogComponent} from './irgraph/irgraph-air-dialog/irgraph-air-dialog.component'
import {IrgraphBulkAirDialogComponent} from "./irgraph/irgraph-bulk-air-dialog/irgraph-bulk-air-dialog.component";


@NgModule({
  declarations: [IRGraphComponent, IrgraphAirDialogComponent, EntityLinkComponent, IrgraphAssetDialogComponent, IrgraphFoDialogComponent, IrgraphFrDialogComponent, IrgraphOoDialogComponent,IrgraphOirDialogComponent, IrgraphDeleteDialogComponent, ConfirmProjectImportDialogComponent, IrgraphBulkAirDialogComponent],
    imports: [
        CommonModule,
        InformationRequirementsRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        NgbModule,
        AngularDraggableModule,
        NgxBootstrapMultiselectModule,
        NgxPanZoomModule
    ]
})
export class InformationRequirementsModule { }

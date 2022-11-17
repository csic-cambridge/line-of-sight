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



@NgModule({
  declarations: [IRGraphComponent, EntityLinkComponent, IrgraphAssetDialogComponent, IrgraphFoDialogComponent, IrgraphFrDialogComponent, IrgraphOoDialogComponent, IrgraphDeleteDialogComponent],
    imports: [
        CommonModule,
        InformationRequirementsRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        NgbModule,
        AngularDraggableModule,
        NgxBootstrapMultiselectModule
    ]
})
export class InformationRequirementsModule { }

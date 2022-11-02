import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IRGraphComponent } from './irgraph/irgraph.component';
import {InformationRequirementsRoutingModule} from './information-requirements-routing.module';
import { EntityLinkComponent } from './irgraph/entity-link/entity-link.component';
import {FormsModule} from "@angular/forms";
import {AngularDraggableModule} from "angular2-draggable";
import {NgxBootstrapMultiselectModule} from "ngx-bootstrap-multiselect";



@NgModule({
  declarations: [IRGraphComponent, EntityLinkComponent],
    imports: [
        CommonModule,
        InformationRequirementsRoutingModule,
        FormsModule,
        AngularDraggableModule,
        NgxBootstrapMultiselectModule
    ]
})
export class InformationRequirementsModule { }

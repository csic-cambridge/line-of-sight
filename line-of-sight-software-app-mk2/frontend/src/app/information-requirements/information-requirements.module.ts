import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IRGraphComponent } from './irgraph/irgraph.component';
import {InformationRequirementsRoutingModule} from './information-requirements-routing.module';
import { EntityLinkComponent } from './irgraph/entity-link/entity-link.component';
import {FormsModule} from "@angular/forms";



@NgModule({
  declarations: [IRGraphComponent, EntityLinkComponent],
    imports: [
        CommonModule,
        InformationRequirementsRoutingModule,
        FormsModule
    ]
})
export class InformationRequirementsModule { }

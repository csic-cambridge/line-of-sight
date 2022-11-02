import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IRGraphComponent } from './irgraph/irgraph.component';
//import { DashboardComponent } from '../dashboard/dashboard.component'
//import { OOGraphComponent } from '../oograph/oograph.component'

const routes: Routes = [
    //{path: 'dashboard', component: DashboardComponent},
    //{path: 'oograph', component: OOGraphComponent},
    {path: '', component: IRGraphComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class InformationRequirementsRoutingModule { }

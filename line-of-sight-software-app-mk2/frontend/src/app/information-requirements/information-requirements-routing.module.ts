import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {IRGraphComponent} from './irgraph/irgraph.component';


const routes: Routes = [
    {path: '', component: IRGraphComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class InformationRequirementsRoutingModule { }

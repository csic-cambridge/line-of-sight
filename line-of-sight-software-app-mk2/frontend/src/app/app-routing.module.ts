import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {LoginComponent} from "./login/login.component";

const routes: Routes = [
    { path: 'login', component: LoginComponent},
    { path: '', loadChildren: () => import('./information-requirements/information-requirements.module')
            .then(m => m.InformationRequirementsModule)}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

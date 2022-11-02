import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from "./login/login.component";
import { DashboardComponent } from "./dashboard/dashboard.component"
import { OOGraphComponent } from './oograph/oograph.component';
import { NotFoundComponent } from './not-found/not-found.component';

const routes: Routes = [
    {path: 'login', component: LoginComponent},
    {path: 'dashboard', component: DashboardComponent},
    {path: 'oograph', component: OOGraphComponent},
    {path: '', loadChildren: () => import('./information-requirements/information-requirements.module')
    .then(m => m.InformationRequirementsModule)},
    {path: 'project', loadChildren: () => import('./information-requirements/information-requirements.module')
            .then(m => m.InformationRequirementsModule)},
    { path: '**', component: NotFoundComponent },
];

export const routing = RouterModule.forRoot(routes);

import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { OOGraphComponent } from './oograph/oograph.component';
import { SuperuserComponent } from './components/superuser/superuser.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import {AuthGuardService} from './services/auth-guard.service';

const routes: Routes = [
{path: 'login', component: LoginComponent},
{path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuardService]},
{path: 'oograph', component: OOGraphComponent, canActivate: [AuthGuardService]},
{path: 'superuser', component: SuperuserComponent, canActivate: [AuthGuardService]},
{path: 'project', loadChildren: () => import('./information-requirements/information-requirements.module')
.then(m => m.InformationRequirementsModule)},
{path: '', redirectTo: '/dashboard', pathMatch: 'full'},
{ path: '**', component: NotFoundComponent },
];

export const routing = RouterModule.forRoot(routes);

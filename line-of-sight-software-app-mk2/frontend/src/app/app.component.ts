import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { AuthenticationService } from "./authentication.service";
import { ProjectDataService } from './project-data.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor(private router: Router, private authService: AuthenticationService,
                private projectDataService : ProjectDataService){}

  title = 'cdbb';

  getRouter () : Router {
      return this.router;
  }

  logout(): void {
    this.authService.logout().subscribe();
    this.router.navigate(['/login']);
  }

  goToProjectsDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  goToOOGraph(): void {
    this.router.navigate(['/oograph']);
  }

  getProjectName() : String {
      return this.projectDataService.getProject().name;
  }
}

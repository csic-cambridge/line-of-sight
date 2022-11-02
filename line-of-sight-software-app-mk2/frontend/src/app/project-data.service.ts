import { Injectable } from '@angular/core';
import { Project } from './project';


@Injectable({
  providedIn: 'root'
})
export class ProjectDataService {
  private noProject : Project = {
      id: '',
      name: '',
      fo_dd_id: '',
      asset_dd_id: ''
  };
  private project : Project = this.noProject;


  constructor() {}

  setProject(project : Project) {
      //console.log("ProjectDataService: save project", project);
      if (project === null) {
          this.project = this.noProject;
      }
      else {
            this.project = project;
      }
      sessionStorage.setItem("project", JSON.stringify(project));

  }

  getProject() : Project {
      //console.log("ProjectDataService: get project entry", this.project);
      if (this.project.id === '') {
          // read from session storage as may have a browser page refresh
          let json = sessionStorage.getItem('project');
          if (json != null) {
            this.project = JSON.parse(json) as Project;
          }
      };
      //console.log("ProjectDataService: get project exit", this.project);
      return this.project;
  }
}

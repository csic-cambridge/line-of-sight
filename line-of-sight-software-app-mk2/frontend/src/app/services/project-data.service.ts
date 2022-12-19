import { Injectable } from '@angular/core';
import { Project } from '../types/project';
import {BaseProjectDataService} from './base/base-project-data-service';

@Injectable({
  providedIn: 'root'
})
export class ProjectDataService extends BaseProjectDataService {

  private noProject: Project = {
      id: '',
      name: '',
      fo_dd_id: '',
      asset_dd_id: ''
  };

  private project: Project = this.noProject;

  constructor() {
      super();
  }

  setProject(project: Project): void{
      if (project === null) {
          this.project = this.noProject;
      }
      else {
            this.project = project;
      }
      sessionStorage.setItem('project', JSON.stringify(project));
  }

  getProject(): Project {
      if (this.project.id === '') {
          // read from session storage as may have a browser page refresh
          const json = sessionStorage.getItem('project');
          if (json != null) {
            this.project = JSON.parse(json) as Project;
          }
      }
      return this.project;
  }
}

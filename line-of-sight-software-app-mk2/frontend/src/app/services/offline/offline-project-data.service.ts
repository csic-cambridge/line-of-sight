import { Injectable } from '@angular/core';
import {BaseProjectDataService} from '../base/base-project-data-service';
import {Project} from '../../types/project';
import {GuidHelper} from '../../helpers/guid-helper';

@Injectable({
  providedIn: 'root'
})
export class OfflineProjectDataService extends BaseProjectDataService {

    constructor() {
        super();
    }

    getProject(): Project {
        const projectData = localStorage.getItem('projectData');
        if (projectData) {
            return JSON.parse(projectData) as Project;
        }
        return {id: GuidHelper.getGuid(), name: 'showcase'} as Project;
    }

    setProject(project: Project): void {
        const jsonData = JSON.stringify(project);
        localStorage.setItem('projectData', jsonData);
    }
}

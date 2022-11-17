import {HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Project} from '../../types/project';

export abstract class BaseIoService {

    constructor() { }

    abstract exportProject(id: string | undefined): Promise<any>;

    abstract importProject(projectData: string): Promise<any>;

    abstract importDictionary(data: string): Promise<any>;

    abstract importFODictionary(data: string): Promise<any>;

    abstract importFirs(data: string, projectId: string): Promise<any>;

    abstract importAirs(data: string, projectId: string): Promise<any>;
}

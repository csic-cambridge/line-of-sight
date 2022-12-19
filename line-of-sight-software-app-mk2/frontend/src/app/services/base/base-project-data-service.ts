import {Project} from '../../types/project';

export abstract class BaseProjectDataService {
    abstract setProject(project: Project): void;

    abstract getProject(): Project;
}

import {BaseProjectService} from './base-project-service';
import {BehaviorSubject, Observable} from 'rxjs';
import {FunctionalOutput} from '../../types/functional-output';

export abstract class BaseFunctionalOutputService {
    protected projectService: BaseProjectService;
    public functionalOutputs: BehaviorSubject<FunctionalOutput[]> =
        new BehaviorSubject<FunctionalOutput[]>([]);

    constructor(ps: BaseProjectService) {
        this.projectService = ps;
    }

    abstract getFunctionalOutputs(projectId: string): Observable<Array<FunctionalOutput>>;
    abstract loadFunctionalOutputs(projectId: string): void;
    abstract save(functionalOutput: FunctionalOutput, projectId: string): Observable<FunctionalOutput>;
    abstract delete(foId: string, projectId: string): Observable<any>;
}

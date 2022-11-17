import {BehaviorSubject, Observable} from 'rxjs';
import {Oir, OrganisationalObjective} from '../../types/organisational-objective';
import {OrganisationalObjectiveVersion} from '../../types/organisational-objective-version';

export abstract class BaseOrganisationalObjectiveService {

    public organisationalObjectives: BehaviorSubject<OrganisationalObjective[]> = new BehaviorSubject<OrganisationalObjective[]>([]);
    public oirs: BehaviorSubject<Oir[]> = new BehaviorSubject<Oir[]>([]);
    public versions: BehaviorSubject<OrganisationalObjectiveVersion[]> = new BehaviorSubject<OrganisationalObjectiveVersion[]>([]);
    constructor() {}

    public getOrganisationalObjectives(): BehaviorSubject<OrganisationalObjective[]> {
        return this.organisationalObjectives;
    }

    abstract save(organisationalObjective: OrganisationalObjective): Observable<OrganisationalObjective>;

    abstract delete(ooId: string): Observable<any>;

}

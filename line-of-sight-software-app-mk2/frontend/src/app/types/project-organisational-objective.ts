import { OrganisationalObjectiveVersion } from './organisational-objective-version';
import { Oir } from './organisational-objective';
import {FormControl} from "@angular/forms";

export interface ProjectOrganisationalObjective {
    id: string;
    project_id: string;
    name: string;
    oo_version_id: string;
    oo_is_deleted: boolean;
    oo_versions: OrganisationalObjectiveVersion[];
    oirs: Oir[];
    deleted_oirs:Oir[];
    frs: string[];

}

export interface ProjectOrganisationalObjectiveAir {
    id: string;
    project_id: string;
    name: string;
    air_oirs: Oir[];
}

export interface ProjectOrganisationalObjectiveUpdate {
    id: string;
    oo_version_id: string;
    oo_is_deleted: boolean;
    oo_versions: OrganisationalObjectiveVersion[];
    oirs: Oir[];
    frs: string[];
}

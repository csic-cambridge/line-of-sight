export interface OrganisationalObjective {
    id: string | null;
    name: string;
    oirs: Oir[];
    is_deleted: boolean;
}

export interface Oir {
    id: string;
    oir: string;
    oo_id?: string | null;
}

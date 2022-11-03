import {Permission} from "./permission";

export interface ProjectPermissions {
    user_id: string;
    project_id: string;
    permissions: Permission[];
}

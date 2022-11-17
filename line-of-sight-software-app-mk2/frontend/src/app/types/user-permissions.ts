import {Permission} from "./permission";

export interface UserPermissions {
    user_id: string;
    permissions: Permission[];
}

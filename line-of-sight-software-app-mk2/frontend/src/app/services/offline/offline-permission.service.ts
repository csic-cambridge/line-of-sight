import { Injectable } from '@angular/core';
import {BasePermissionService} from '../base/base-permission-service';
import {UserPermissions} from '../../types/user-permissions';

@Injectable({
  providedIn: 'root'
})
export class OfflinePermissionService extends BasePermissionService {

    constructor() {
        super();
    }

    accountChange(data: UserPermissions): void {
    }

    permissionDisabled(id: string, permissionId: string): boolean {
        return false;
    }

    userPermissionDisabled(permissionId: string): boolean {
        return false;
    }
}

import {UserPermissions} from '../../types/user-permissions';
import {ProjectPermissionIds} from '../../types/project-permission-ids';
import {UserPermissionIds} from '../../types/user-permission-ids';
import {BehaviorSubject} from 'rxjs';
import {Project} from '../../types/project';
import {FunctionalOutputDictionary} from '../../types/functional-output-dictionary';
import {AssetDictionary} from '../../types/asset-dictionary';
import {User} from '../../types/user';

export abstract class BasePermissionService {
    constructor() {
    }
    public PPIds = ProjectPermissionIds;
    public UPIds = UserPermissionIds;
    public foDataDictionary: BehaviorSubject<FunctionalOutputDictionary[]> = new BehaviorSubject<FunctionalOutputDictionary[]>([]);
    public assetDataDictionary: BehaviorSubject<AssetDictionary[]> = new BehaviorSubject<AssetDictionary[]>([]);
    abstract accountChange(data: UserPermissions): void;

    abstract permissionDisabled(id: string, permissionId: string): boolean;

    abstract userPermissionDisabled(permissionId: string): boolean;
}

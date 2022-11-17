import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BasePermissionService} from '../../services/base/base-permission-service';
import {OrganisationalObjective} from '../../types/organisational-objective';
import {Observable, of} from 'rxjs';
import {BaseOrganisationalObjectiveService} from '../../services/base/base-organisational-objective-service';

@Component({
  selector: 'app-oograph-listview',
  templateUrl: './oograph-listview.component.html',
  styleUrls: ['./oograph-listview.component.scss']
})
export class OographListviewComponent implements OnInit {

    objectives!: Observable<OrganisationalObjective[]>;
    @Output() opened = new EventEmitter<OrganisationalObjective>();

    constructor(private ooService: BaseOrganisationalObjectiveService,
                public permissionService: BasePermissionService) {
    }

    ngOnInit(): void {
        this.objectives = this.ooService.getOrganisationalObjectives();
    }

    open(objective: OrganisationalObjective): void {
        this.opened.emit(objective);
    }
}

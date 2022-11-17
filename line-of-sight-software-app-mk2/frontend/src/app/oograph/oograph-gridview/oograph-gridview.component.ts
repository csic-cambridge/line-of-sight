import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {OrganisationalObjective} from '../../types/organisational-objective';
import {BaseOrganisationalObjectiveService} from '../../services/base/base-organisational-objective-service';
import {BasePermissionService} from '../../services/base/base-permission-service';
import {Observable} from "rxjs";

@Component({
  selector: 'app-oograph-gridview',
  templateUrl: './oograph-gridview.component.html',
  styleUrls: ['./oograph-gridview.component.scss']
})
export class OographGridviewComponent implements OnInit, AfterViewInit {
    objectives!: Observable<OrganisationalObjective[]>;
    @Output() opened = new EventEmitter<OrganisationalObjective>();

    @Input() updateMasonryLayout!: boolean;

    constructor(private ooService: BaseOrganisationalObjectiveService,
                public permissionService: BasePermissionService) {
    }

    ngOnInit(): void {
        this.objectives = this.ooService.getOrganisationalObjectives();
    }

    open(objective: OrganisationalObjective): void {
        this.opened.emit(objective);
    }

    ngAfterViewInit(): void {
    }

}

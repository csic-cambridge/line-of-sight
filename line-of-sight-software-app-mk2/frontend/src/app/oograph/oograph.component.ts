import {OrganisationalObjective} from '../types/organisational-objective';
import {Component, OnInit} from '@angular/core';
import {BasePermissionService} from '../services/base/base-permission-service';
import {BaseOrganisationalObjectiveService} from '../services/base/base-organisational-objective-service';
import {CookieService} from 'ngx-cookie-service';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {OographDialogComponent} from './oograph-dialog/oograph-dialog.component';
import {AppToastService} from '../services/app-toast.service';

@Component({
    selector: 'app-oograph',
    templateUrl: './oograph.component.html',
    styleUrls: ['./oograph.component.scss'],
    providers: [CookieService]
})
export class OOGraphComponent {
    activeItem: OrganisationalObjective | undefined;
    isListview =  this.cookieService.get('app-oograph-isListview') === '0' ? false : true;
    updateGridview = false;
    constructor(private ooService: BaseOrganisationalObjectiveService,
                private modalService: NgbModal,
                public toastr: AppToastService,
                public permissionService: BasePermissionService,
                private cookieService: CookieService) {
        this.ooService.reload();
    }

    openModal(): void {
        const oo = {
            id: null,
            name: '',
            oirs: [],
            is_deleted: false
        } as OrganisationalObjective;
        this.openPrePopulatedModal(oo);
    }

    openPrePopulatedModal(oo: OrganisationalObjective): void {
        const modalRef = this.modalService.open(OographDialogComponent, { scrollable: true, centered: true, size: 'lg' });
        modalRef.componentInstance.activeItem = oo;
        modalRef.componentInstance.closed.subscribe(($event: any) => {
            modalRef.close();
            if ($event) {
                this.updateGridview = !this.updateGridview;
            }
        });
        modalRef.componentInstance.hasError.subscribe(($event: any) => {
            this.toastr.show($event.message, $event.name);
        });
    }

    toggleView(): void {
        this.isListview = !this.isListview;
        this.cookieService.set('app-oograph-isListview', this.isListview ? '1' : '0');
    }
}

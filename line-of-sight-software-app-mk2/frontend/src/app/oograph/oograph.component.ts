import {OrganisationalObjective} from '../organisational-objective';
import {OrganisationalObjectiveService} from '../organisational-objective.service';
import {Component, ViewChild, ElementRef} from '@angular/core';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {IMultiSelectSettings} from 'ngx-bootstrap-multiselect';
import {Oir} from '../organisational-objective';
import {PermissionService} from '../services/permission.service';

@Component({
    selector: 'app-oograph',
    templateUrl: './oograph.component.html',
    styleUrls: ['./oograph.component.scss']
})
export class OOGraphComponent {
    objectives: Array<OrganisationalObjective> = [];

    constructor(private ooService: OrganisationalObjectiveService,
                private modalService: NgbModal,
                public permissionService: PermissionService) {

        this.ooService.getOrganisationalObjectives()
            .subscribe(objectives => {
                this.objectives = objectives;
            });
    }

    optionsModel: Array<number> = [];
    mySettings: IMultiSelectSettings = {buttonClasses: 'form-control element-text', enableSearch: true, dynamicTitleMaxItems: 0};

    private errorMessage: ((error: any) => void) | null | undefined;

    selectedOOName = '';
    selectedOOId: string | null = null;
    orgObjectivesIsOpen = false;

    /* Organisation Objectives Modal Values */
    ooDeselectedOirValues: Oir[] = [];

    closeResult: string | undefined;
    assetForm: any;

    @ViewChild('elOOId') elOOId!: ElementRef;
    @ViewChild('elOOSubmitBtn') elOOSubmitBtn!: ElementRef;

    private static getDismissReason(reason: any): string {
        if (reason === ModalDismissReasons.ESC) {
            return 'by pressing ESC';
        } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
            return 'by clicking on a backdrop';
        } else {
            return `with: ${reason}`;
        }
    }

    getOrganisationalObjectiveWithId(id: string): OrganisationalObjective | undefined {
        return this.objectives.find(x => x.id === id);
    }

    logDeselectedOirs(title: string): void {
        this.ooDeselectedOirValues.forEach((oir, index) =>
            console.log('deselected oir: index =' + index + 'id= ' + oir.id + ',text=' + oir.oir));
    }

    saveOrganisationalObjective(value: any): void {
        this.logDeselectedOirs('saveOrganisationalObjective');

        const checkExistingOOId = 'ooMainId' in value;
        let ooToUpdate = null;
        let updatedOirs: Oir[] = [];
        if (checkExistingOOId) {
            ooToUpdate = this.getOrganisationalObjectiveWithId(value.ooMainId);
            if (ooToUpdate !== undefined) {
                updatedOirs = [...ooToUpdate.oirs];
                if (this.ooDeselectedOirValues.length > 0) {
                    updatedOirs = updatedOirs.filter( (e1: Oir) => !this.ooDeselectedOirValues.includes( e1 ));
                }
            }
        }
        if (value.oirs !== '') {
            const newOir = {
                id: '',
                oir: value.oirs
            };
            updatedOirs.push(newOir);
        }

        const oo = {
            id: ooToUpdate == null ? null : ooToUpdate.id,
            name: this.selectedOOName,
            oirs: updatedOirs.flat(),
            is_deleted: false
        };

        this.ooService.save(oo)
            .subscribe(
                () => {
                    window.location.reload();
                },
                error => {
                    this.errorMessage = error.message;
                    console.error('There was an error!', error);
                }
            );
        /* Closing Modal after sending Save */
        this.openModal(false);
        this.selectedOOId = null;
        this.selectedOOName = '';
    }

    ooChange(e: any): void {
        this.getOOsValue(e.target.value);
    }

    getOOsValue(ooName: string): void {
        if (ooName.length > 0) {
            this.selectedOOName = ooName;
            this.enableSubmitBtn(ooName, this.elOOId, this.elOOSubmitBtn);
        }
        this.updateOIRsForOo();
    }

    updateOIRsForOo(): void {
        let selectedOO: OrganisationalObjective | undefined;
        for (const oo of this.objectives){
            if (oo.name === this.selectedOOName){
                selectedOO = oo;
                this.selectedOOId = oo.id;
                break;
            }
        }
    }

    onOOOIRChange(value: Oir): void {
        if (this.ooDeselectedOirValues.includes(value)) {
            this.ooDeselectedOirValues = this.ooDeselectedOirValues.filter((item) => item !== value);
        } else {
            this.ooDeselectedOirValues.push(value);
        }
    }

    open(content: any): void {
        this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', centered: true}).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
        }, (reason) => {
            this.closeResult = `Dismissed ${OOGraphComponent.getDismissReason(reason)}`;
        });
    }

    openModal(isOpen: boolean): void {
        if (isOpen) {
            this.clearContent();
            this.orgObjectivesIsOpen = isOpen;
        } else if (!isOpen) {
            this.orgObjectivesIsOpen = isOpen;
            this.clearContent();
        }

    }

    openPrePopulatedModal(selectedOOId: string, selectedOOText: string, disabled: boolean): void {
        if (!disabled && selectedOOId !== '' && selectedOOText !== '') {
            this.openModal(true);
            this.selectedOOId = selectedOOId;
            this.getOOsValue(selectedOOText);
        }
    }

    /* Clear the form once pop-up Window is closed */
    clearContent(): void {
        this.resetFormContentState(this.elOOId, this.elOOSubmitBtn);
        this.selectedOOId = null;
        this.getOOsValue('');
    }

    deleteOrganisationalObjective(ooId: string, ooName: string): void {
        this.ooService.delete(ooId)
            .subscribe(
                () => {
                    window.location.reload();
                },
                error => {
                    this.errorMessage = error.message;
                    console.log('There was an error!', error);
                });

        /* Closing Modal after sending Delete */
        this.modalService.dismissAll();
        this.openModal(false);
    }

    /* Enable Popup Window Submit Button (on Title input) */
    enableSubmitBtn(selectedOOName: string, el: ElementRef, btnEl: ElementRef): void {
        el.nativeElement.value = selectedOOName;
        el.nativeElement.classList.remove('is-invalid');
        btnEl.nativeElement.disabled = false;
    }

    /* Reset Popup Window Form Contents (incl. CSS) */
    resetFormContentState(el: ElementRef, btnEl: ElementRef): void {
        el.nativeElement.value = '';
        el.nativeElement.classList.add('is-invalid');
        btnEl.nativeElement.disabled = true;
    }

    reloadWindow(): void {
        window.location.reload();
    }
}

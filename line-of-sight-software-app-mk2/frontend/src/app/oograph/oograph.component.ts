import { OrganisationalObjective } from '../organisational-objective';
import { OrganisationalObjectiveService } from "../organisational-objective.service";
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ModalDismissReasons, NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { IMultiSelectSettings, IMultiSelectOption, IMultiSelectTexts} from "ngx-bootstrap-multiselect";
import { Oir } from '../organisational-objective';

@Component({
    selector: 'app-oograph',
    templateUrl: './oograph.component.html',
    styleUrls: ['./oograph.component.scss']
})
export class OOGraphComponent implements OnInit, AfterViewInit {

    optionsModel: Array<number> = [];
    mySettings: IMultiSelectSettings = {buttonClasses: 'form-control element-text', enableSearch: true, dynamicTitleMaxItems: 0};

    static darkblue = '#4974a7';
    private errorMessage: ((error: any) => void) | null | undefined;

    objectives: Array<OrganisationalObjective> = [];
    selectedOOName: string = "";
    selectedOOId : string | null = null;
    orgObjectivesIsOpen : boolean = false;

    /* Organisation Objectives Modal Values */
    oo_deselected_oir_values: Oir[] = [];

    closeResult: string | undefined;
    assetForm: any;

    constructor(private ooService : OrganisationalObjectiveService,
                private modalService: NgbModal) {
    }

    getOrganisationalObjectives(): void {
        this.ooService.getOrganisationalObjectives()
            .subscribe(objectives => {
                this.objectives = objectives;
            });
    }

    getOrganisationalObjectiveWithId(id: string) {
        return this.objectives.find(x => x.id === id);
    }

    logDeselectedOirs(title: string) {
        console.count("logDeselectedOirs:" + title + " - count = " + this.oo_deselected_oir_values.length);
        this.oo_deselected_oir_values.forEach((oir, index)=>console.log("deselected oir: index =" + index + "id= " + oir.id + ",text="+oir.oir))
    }

    saveOrganisationalObjective(value: any): void {
        console.log("Saving OO: ", value, "selected oirs = ", this.oo_deselected_oir_values);
        this.logDeselectedOirs("saveOrganisationalObjective");

        let checkExistingOOId = 'ooMainId' in value;
        let ooToUpdate = null;
        let updatedOirs: Oir[] = [];
        if(checkExistingOOId) {
            ooToUpdate = this.getOrganisationalObjectiveWithId(value.ooMainId);
            if (ooToUpdate != undefined) {
                updatedOirs = [...ooToUpdate.oirs];
                if(this.oo_deselected_oir_values.length > 0) {
                    updatedOirs = updatedOirs.filter( (e1: Oir) => !this.oo_deselected_oir_values.includes( e1 ));
                }
            }
        }
        if (value.oirs !== "") {
            let newOir = {
                id:'',
                oir:value.oirs
            }
            updatedOirs.push(newOir);
        }

        let oo = {
            id: ooToUpdate == null ? null : ooToUpdate.id,
            name: this.selectedOOName,
            oirs: updatedOirs.flat(),
            is_deleted: false
        };

        console.log("saving oo ", oo);
        this.ooService.save(oo)
            .subscribe(
                () => {
                    window.location.reload()
                },
                error => {
                    this.errorMessage = error.message;
                    console.error('There was an error!', error)
                }
            );
        /* Closing Modal after sending Save */
        this.openModal(false);
        this.selectedOOId = null;
        this.selectedOOName = '';
    }

    ooChange(e:  any): void {
        console.log("ooChange = ", e.target.value);
        this.getOOsValue(e.target.value);
    }

    getOOsValue(ooName: string): void {
        if(ooName.length > 0) {
            this.selectedOOName = ooName;
            this.enableSubmitBtn(ooName, 'ooId','ooSubmitBtn');
        }
        this.updateOIRsForOo();
        /* Log */
        console.log("Selection OO Name : ", ooName);
    }

    updateOIRsForOo(): void {
        let selectedOO: OrganisationalObjective | undefined;
        for (let oo of this.objectives){
            if (oo.name === this.selectedOOName){
                console.log("Found OO: ", this.selectedOOName);
                selectedOO = oo;
                this.selectedOOId = oo.id;
                break;
            };
        };

        console.log("SelectedOO Value : ", selectedOO);

        console.log('Updated OO Options');
    }


    onOOOIRChange(value: Oir): void {
        //this.logDeselectedOirs("onOOOIRChange - entry");
        if (this.oo_deselected_oir_values.includes(value)) {
            this.oo_deselected_oir_values = this.oo_deselected_oir_values.filter((item) => item !== value);
        } else {
            this.oo_deselected_oir_values.push(value);
        }
        //this.logDeselectedOirs("onOOOIRChange - exit");
    }

    ngOnInit(): void {}

    ngAfterViewInit(): void {
        this.getOrganisationalObjectives();
        console.log('Objectives', this.objectives);
    }

    open(content: any) {
        this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', centered: true}).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
        }, (reason) => {
            this.closeResult = `Dismissed ${OOGraphComponent.getDismissReason(reason)}`;
        });
    }

    openModal(isOpen : boolean) : void {
        if(isOpen) {
            this.clearContent();
            this.orgObjectivesIsOpen = isOpen;
        } else if(!isOpen) {
            this.orgObjectivesIsOpen = isOpen;
            this.clearContent();
        }

        console.log('OO open status is : ' + isOpen );
    }

    openPrePopulatedModal( selectedOOId: string, selectedOOText: string): void {
        console.log("I clicked OO with Id: " + selectedOOId + " and Text: " + selectedOOText);

        if (selectedOOId !== "" && selectedOOText !== "") {
            this.openModal(true);
            this.selectedOOId = selectedOOId;
            this.getOOsValue(selectedOOText);
        }

    }

    /* Clear the form once pop-up Window is closed */
    clearContent(): void {
        console.log("Clearing OO fields");
        this.resetFormContentState('ooId','ooSubmitBtn');
        this.selectedOOId = null;
        this.getOOsValue('');
    }

    deleteOrganisationalObjective(ooId: string, ooName: string): void {
        console.log("Processing delete request for name: " ,ooName, "id:", ooId);

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
    enableSubmitBtn(selectedOOName: string, elementId: string, btnText: string) : void {
        console.log("selectedOOName : ", selectedOOName, ", selected OOId=", this.selectedOOId);
        (<HTMLInputElement>document.getElementById(elementId)).value = selectedOOName;
        (<HTMLInputElement>document.getElementById(elementId)).classList.remove("is-invalid");
        (<HTMLInputElement>document.getElementById(btnText)).disabled = false;
    }

    /* Reset Popup Window Form Contents (incl. CSS) */
    resetFormContentState(id: string, btnText: string) : void {
        (<HTMLInputElement> document.getElementById(id)).value = "";
        (<HTMLInputElement>document.getElementById(id)).classList.add("is-invalid");
        (<HTMLInputElement>document.getElementById(btnText)).disabled = true;
    }

    private static getDismissReason(reason: any): string {
        if (reason === ModalDismissReasons.ESC) {
            return 'by pressing ESC';
        } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
            return 'by clicking on a backdrop';
        } else {
            return `with: ${reason}`;
        }
    }

    reloadWindow(): void {
        window.location.reload();
    }
}

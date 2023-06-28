import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {IrGraphDialogs} from '../../../types/ir-graph-dialogs';
import {Project} from '../../../types/project';
import {FunctionalRequirement} from '../../../types/functional-requirement';
import {
    ProjectOrganisationalObjective,
    ProjectOrganisationalObjectiveAir
} from '../../../types/project-organisational-objective';
import {AbstractControl, Form, FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {Oir} from '../../../types/organisational-objective';
import {BasePermissionService} from '../../../services/base/base-permission-service';
import {environment} from '../../../../environments/environment';
import {Asset} from "../../../types/asset";
import {Airs} from "../../../types/airs";
import {BaseOirsService} from "../../../services/base/base-oirs-service";
import {merge, Observable, Subject} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, map, switchMap} from 'rxjs/operators';
import {NgbTypeahead, NgbTypeaheadSelectItemEvent} from "@ng-bootstrap/ng-bootstrap";
import {DataDictionaryEntry} from "../../../types/data-dictionary-entry";
import {
    BaseProjectOrganisationalObjectiveService
} from "../../../services/base/base-project-organisational-objective-service";
import {forbiddenNameValidator} from "../../../dashboard/copy-project-dialog/copy-project-dialog.component";

@Component({
  selector: 'app-irgraph-air-dialog',
  templateUrl: './irgraph-air-dialog.component.html',
  styleUrls: ['./irgraph-air-dialog.component.scss']
})

// Unlink existing links
// Link new Oirs using multiselect dropdown
export class IrgraphAirDialogComponent implements OnInit {
    @Output() closed = new EventEmitter<{ updated: boolean, dialog: IrGraphDialogs }>();
    @Output() hasError = new EventEmitter<any>();
    @Input() selectedPoo!: ProjectOrganisationalObjective;
    @Input() project!: Project;
    @Input() assets!: Asset[];
    @Input() selectedAir!: Airs;
    @Input() poosLinkedAirs!: ProjectOrganisationalObjectiveAir[]
    @Input() frs!: FunctionalRequirement[];
    @Input() poos!: ProjectOrganisationalObjective[];
    @Input() assetLinks!: string[]

    @ViewChild('instanceOir', { static: true }) instanceOir: NgbTypeahead | undefined;

    oirForm = new FormGroup({})
    candidateNewOirs: Oir[] = []
    newOirs: { link: boolean;  oir: string; }[] = []
    oirSearchItems = [] as Oir[]

    focusOir$ = new Subject<string>();
    clickOir$ = new Subject<string>();
    public env = environment;

    formatter = (result: DataDictionaryEntry) => result.text;
    resultFormatter = (result: Oir) => result.oir

    poosLinkedAirsControl(): FormArray {
        return this.oirForm.get('poosLinkedAirsControl') as FormArray;
    }
    pooNames(): FormArray {
        return this.oirForm.get('pooNames') as FormArray;
    }
    oirNames(): FormArray {
        return this.oirForm.get('oirNames') as FormArray;
    }


    oirs(): FormArray {
        return this.oirForm.get('oirs') as FormArray;
    }

    controlsArray(group: FormGroup): FormArray {
        return new FormArray(Object.keys(group.controls).map(key => group.get(key) as FormControl));
    }

    constructor(private fb: FormBuilder,
                private oirService: BaseOirsService,
                public permissionService: BasePermissionService,
                private pooService: BaseProjectOrganisationalObjectiveService,

    ) {


    }

    ngOnInit(): void {
        this.buildForm();
    }

    buildForm(): void {
        if (!this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.LINK_OIR_AIR)) {
            this.getOirs();
        }

        this.oirForm = new FormGroup({
            id: this.fb.control(''),
            newOirLink: this.fb.control({
                value: '',
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.LINK_OIR_AIR)
            }, [forbiddenNameValidator(this.selectedAir?.oirs.map(x => x.oir), false)]),
            oirs: this.fb.array([]),
            oirNames: this.fb.array([]),
            pooNames: this.fb.array([]),
        })
        this.poosLinkedAirs?.map(poo => this.pooNames().push(this.fb.control(poo.name)));
        this.poosLinkedAirs?.map(poo => poo.air_oirs.map(oir => this.oirNames().push(this.fb.control(`${oir.oir} (${(poo.name.length > 30) ? poo.name.slice(0,29) : poo.name}...)`))))
        this.poosLinkedAirs?.map(poo => poo.air_oirs.map(oir => this.oirs().push(this.fb.control({
            value: true,
            disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.LINK_OIR_AIR)
        }))))

        this.oirForm.markAsPristine()

    }

    oirChange(event: Event): void {
        const target = event.target || event.srcElement || event.currentTarget;
        const checkbox = (target as HTMLInputElement);
            const index = checkbox.attributes.getNamedItem('index')?.value;
            if (index) {
                this.oirs().controls[Number(index)].setValue(checkbox.checked)
            }
    }

    closeModal(): void {
        this.closed.emit({updated: false, dialog: IrGraphDialogs.AIR_DIALOG});
    }

    save(): void {
        let linkOirs: { link: boolean;  oir: string; }[] = []
        this.oirs().getRawValue().map((v, i) => {
            if (!v) {
                linkOirs.push({oir: this.selectedAir.oirs[i].id, link:false})
            }
        });

        linkOirs = [...linkOirs, ...this.newOirs]
        for (let link of linkOirs) {
            this.oirService.linkOirAir(this.project.id, link.oir, this.selectedAir.id,link.link).subscribe(() => {
                        this.closed.emit({updated: true, dialog: IrGraphDialogs.AIR_DIALOG});
                        this.pooService.loadProjectOrganisationalObjectives(this.project.id)
                    },
                    error => {
                        this.hasError.emit(error);
            })
        }

    }

    searchOir: (text$: Observable<string>) => Observable<Oir[]> = (text$: Observable<string>) => {
        const debouncedText$ = text$.pipe(
            debounceTime(200),
            distinctUntilChanged());
        const clicksWithClosedPopup$ = this.clickOir$.pipe(filter(() => !this.instanceOir?.isPopupOpen()));
        const inputFocus$ = this.focusOir$;
        return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
            map((term) => {
                    const items = this.candidateNewOirs.filter((v) =>
                        v.oir.toLowerCase().indexOf(term.toLowerCase()) > -1);
                    this.oirSearchItems = items;
                    return items
                }
            ),
        );
    }

    getOirs(): void {
        this.pooService.getProjectOrganisationalObjectives(this.project.id).subscribe((x: any[]) => {
            let oirCandidates: any[] = []
            x.forEach((poo) => {
                this.assetLinks.forEach((link) => {
                    if (link === poo.id){
                        poo.oirs.forEach((oir: { oir: any; id: string}) => {
                            oirCandidates.push({oir:`${oir.oir} (${(poo.name.length > 30) ? poo.name.slice(0,29) : poo.name}...)`, id: oir.id})
                        })
                    }
                })
            })
            const existingLinks = this.poosLinkedAirs.map((oir)=> oir.air_oirs).flat()
            this.candidateNewOirs= oirCandidates.filter(oir => !existingLinks.map(a => a.id).includes(oir.id));
        });
    }

    selectOir(item: any) {
       if(!this.newOirs.find((oi => oi.oir.includes(item.item.id)))) {
           this.oirs().push((this.fb.control({
               value: true,
               disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.LINK_OIR_AIR)
           })))
           this.oirNames().push(this.fb.control(item.item.oir))
           this.newOirs.push({oir: item.item.id, link:true})
           const newCandistateNewOirs = this.candidateNewOirs.filter((x => x.id !== item.item.id ))
           this.candidateNewOirs = newCandistateNewOirs
       }

    }

}

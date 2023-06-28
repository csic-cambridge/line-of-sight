import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {IrGraphDialogs} from '../../../types/ir-graph-dialogs';
import {Project} from '../../../types/project';
import {FunctionalRequirement} from '../../../types/functional-requirement';
import {ProjectOrganisationalObjective} from '../../../types/project-organisational-objective';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {Oir} from '../../../types/organisational-objective';
import {BasePermissionService} from '../../../services/base/base-permission-service';
import {environment} from '../../../../environments/environment';
import {Asset} from "../../../types/asset";
import {Airs} from "../../../types/airs";
import {BaseAssetService} from "../../../services/base/base-asset-service";
import {merge, Observable, Subject} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs/operators';
import {NgbTypeahead} from "@ng-bootstrap/ng-bootstrap";
import {DataDictionaryEntry} from "../../../types/data-dictionary-entry";
import {forbiddenNameValidator} from "../../../dashboard/copy-project-dialog/copy-project-dialog.component";
import {BaseOirsService} from "../../../services/base/base-oirs-service";

@Component({
    selector: 'app-irgraph-oir-dialog',
    templateUrl: './irgraph-oir-dialog.component.html',
    styleUrls: ['./irgraph-oir-dialog.component.scss']
})

// Unlink existing links
// Link new Oirs using multiselect dropdown
export class IrgraphOirDialogComponent implements OnInit {
    @Output() closed = new EventEmitter<{ updated: boolean, dialog: IrGraphDialogs }>();
    @Output() hasError = new EventEmitter<any>();
    @Input() selectedPoo!: ProjectOrganisationalObjective;
    @Input() project!: Project;
    @Input() assets!: Asset[];
    @Input() selectedOir!: Oir & {airs: Airs[]};
    @Input() assetLinkedOirs!: any[]
    @Input() linkedAirs!: Airs[]
    @Input() possibleAirs!: Airs[]
    @Input() frs!: FunctionalRequirement[];
    @Input() poos!: ProjectOrganisationalObjective[];
    @Input() assetLinks!: string[]

    @ViewChild('instanceOir', { static: true }) instanceOir: NgbTypeahead | undefined;

    airForm = new FormGroup({})
    candidateNewAirs: (Airs & {airsWAssetName: string})[] = []
    airNameOnly: Airs[] =[]
    newAirs: { link: boolean;  air: string; }[] = []
    airSearchItems = [] as Airs[]

    focusOir$ = new Subject<string>();
    clickOir$ = new Subject<string>();
    public env = environment;

    formatter = (result: DataDictionaryEntry) => result.text;
    resultFormatter = (result: Airs & {airsWAssetName: string}) => result.airsWAssetName

    poosLinkedAirsControl(): FormArray {
        return this.airForm.get('poosLinkedAirsControl') as FormArray;
    }
    airNames(): FormArray {
        return this.airForm.get('airNames') as FormArray;
    }
    airIds(): FormArray {
        return this.airForm.get('airIds') as FormArray;
    }
    airNamesWithAssetName(): FormArray {
        return this.airForm.get('airNamesWithAssetName') as FormArray
    }

    airs(): FormArray {
        return this.airForm.get('airs') as FormArray;
    }

    constructor(private fb: FormBuilder,
                private oirService: BaseOirsService,
                private assetService: BaseAssetService,
                public permissionService: BasePermissionService
    ) {


    }

    ngOnInit(): void {
        this.buildForm();
    }

    selectAllAirsByName(airName: any) {
        let x = this.airNameOnly.filter((air) => air.airs === airName)
        x.forEach((air) => {
            this.selectAir(air)
        })
    }
    moreAirsWithName(airName: string) {
        let y = this.candidateNewAirs.filter((air) => air.airs === airName)
        return !(y.length > 0)
    }
    buildForm(): void {
        if (!this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.LINK_OIR_AIR)) {
            this.getAirs();
        }

        this.airForm = new FormGroup({
            id: this.fb.control(''),
            newOirLink: this.fb.control({
                value: '',
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.LINK_OIR_AIR)
            }, [forbiddenNameValidator(this.selectedOir?.airs.map(x => x.airs), false)]),
            airs: this.fb.array([]),
            airNames: this.fb.array([]),
            airIds: this.fb.array([]),
            airNamesWithAssetName: this.fb.array([]),
            pooNames: this.fb.array([]),
        })

        // this.poosLinkedAirs?.map(poo => this.pooNames().push(this.fb.control(poo.name)));
        this.assetLinkedOirs?.map(asset => asset.linkedAirs.map((air: any) => this.airNames().push(this.fb.control(`${air.airs}`))))
        this.assetLinkedOirs?.map(asset => asset.linkedAirs.map((air: any) => this.airIds().push(this.fb.control(`${air.id}`))))
        this.assetLinkedOirs?.map(asset => asset.linkedAirs.map((air: any) => this.airNamesWithAssetName().push(this.fb.control(`${air.airs} (${(asset.data_dictionary_entry.text.length > 30) ? asset.data_dictionary_entry.text.slice(0,29) : asset.data_dictionary_entry.text}...)`))))
        this.assetLinkedOirs?.map(asset => asset.linkedAirs.map((air: any) => this.airs().push(this.fb.control({
            value: true,
            disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.LINK_OIR_AIR)
        }))))

        this.airForm.markAsPristine()

    }

    oirChange(event: Event): void {
        const target = event.target || event.srcElement || event.currentTarget;
        const checkbox = (target as HTMLInputElement);
        const index = checkbox.attributes.getNamedItem('index')?.value;
        if (index) {
            this.airs().controls[Number(index)].setValue(checkbox.checked)
        }
    }

    closeModal(): void {
        this.closed.emit({updated: false, dialog: IrGraphDialogs.OIR_DIALOG});
    }

    savable(): boolean {
        let linkAirs: { link: boolean;  air: string; }[] = []
        this.airs().getRawValue().map((v, i) => {
            if (!v) {
                linkAirs.push({air: this.airIds().getRawValue()[i], link:false})
            }
        });
        linkAirs = [...linkAirs, ...this.newAirs]
        return linkAirs.length <= 0
    }
    save(): void {
        let linkAirs: { link: boolean;  air: string; }[] = []
        this.airs().getRawValue().map((v, i) => {
            if (!v) {
                linkAirs.push({air: this.airIds().getRawValue()[i], link:false})
            }
        });

        linkAirs = [...linkAirs, ...this.newAirs]
        console.log(linkAirs)
        for (let link of linkAirs) {
            this.oirService.linkOirAir(this.project.id, this.selectedOir.id,link.air,  link.link).subscribe(() => {
                    this.closed.emit({updated: true, dialog: IrGraphDialogs.OIR_DIALOG});
                },
                error => {
                    this.hasError.emit(error);
                })
        }

    }

    searchAir: (text$: Observable<string>) => Observable<Airs[]> = (text$: Observable<string>) => {
        const debouncedText$ = text$.pipe(
            debounceTime(200),
            distinctUntilChanged());
        const clicksWithClosedPopup$ = this.clickOir$.pipe(filter(() => !this.instanceOir?.isPopupOpen()));
        const inputFocus$ = this.focusOir$;
        return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
            map((term) => {
                    const items = this.candidateNewAirs.filter((v) =>
                        v.airsWAssetName.toLowerCase().indexOf(term.toLowerCase()) > -1);
                    this.airSearchItems = items;
                    return items
                }
            ),
        );
    }

    getAirs(): void {
        let airCandidates: any[] = []
        let airNameOnly: any[] = []
        this.assetLinkedOirs?.map(asset => asset.nonLinkedAirs.map((air: any) => {
            airCandidates.push({airs: air.airs, airsWAssetName:`${air.airs} (${(asset.data_dictionary_entry.text.length > 30) ? asset.data_dictionary_entry.text.slice(0,29) : asset.data_dictionary_entry.text}...)`,id: air.id})
            airNameOnly.push({airs:`${air.airs}`,id: air.id})

        }))
        this.candidateNewAirs = airCandidates
        this.airNameOnly = airNameOnly

        this.assetService.getAssets(this.project.id).subscribe((x: any[]) => {

        });
    }

    selectAir(air: any) {
        if(!this.newAirs.find((oi => oi.air.includes(air.id)))) {
            this.airs().push((this.fb.control({
                value: true,
                disabled: this.permissionService.permissionDisabled(this.project.id, this.permissionService.PPIds.LINK_OIR_AIR)
            })))

            if (air) {
                this.airNames().push(this.fb.control(`${air.airs}`))
            }

            this.assetLinkedOirs?.map(asset => asset.airs.map((a: any) => {
                if (a.id === air.id) {
                    this.airNamesWithAssetName().push(this.fb.control(`${air.airs} (${(asset.data_dictionary_entry.text.length > 30) ? asset.data_dictionary_entry.text.slice(0, 29) : asset.data_dictionary_entry.text}...)`))
                }
            }))

            this.newAirs.push({air: air.id, link:true})
            const newCadidiateNewAirs = this.candidateNewAirs.filter((x => x.id !== air.id ))
            this.candidateNewAirs = newCadidiateNewAirs
        }

    }


}

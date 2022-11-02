import {AfterViewInit, Component, Inject, OnInit} from '@angular/core';
import {Router} from "@angular/router"
import {DOCUMENT} from "@angular/common";
import {Project} from "../project";
import {ProjectService} from "../project.service";
import {AuthenticationService} from "../authentication.service";
import {FunctionalOutputDictionary} from "../functional-output-dictionary";
import {FunctionalOutputDictionaryService} from "../functional-output-dictionary.service";
import {AssetDictionary} from "../asset-dictionary";
import {AssetDictionaryService} from "../asset-dictionary.service";
import { ProjectDataService } from '../project-data.service';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, AfterViewInit {

    projects: Array<Project> = [];
    renameProjectArray: Array<String> = [];
    foDataDictionary: Array<FunctionalOutputDictionary> = [];
    assetDataDictionary: Array<AssetDictionary> = [];

    addProjectIsOpen : boolean = false;
    renameProjectIsOpen : boolean = false;
    copyProjectIsOpen : boolean = false;
    deleteConfirmationModalIsOpen : boolean = false;
    copyConfirmationModalIsOpen : boolean = false;

    projectToSave : Project | undefined;
    projectToRename : Project | undefined;
    projectToCopy : Project | undefined;
    projectToDelete : Project | undefined;

    selectedFODD : string = "";
    selectedAssetDD : string = "";

    private errorMessage: ((error: any) => void) | null | undefined;

    constructor(@Inject(DOCUMENT) document: any, private router : Router, private projectService : ProjectService,
                private assetDdeService: AssetDictionaryService, private foDdeService: FunctionalOutputDictionaryService,
                private authService: AuthenticationService, private projectDataService : ProjectDataService) {
    }

    ngOnInit(): void {
        this.getProjects();
        this.getFODataDictionaries();
        this.getAssetDataDictionaries();
    }

    ngAfterViewInit(): void {
        this.getProjects();
        this.getFODataDictionaries();
        this.getAssetDataDictionaries();
    }

    getProjects(): void {
        this.projectService.getProjects()
            .subscribe(projects => {
                this.projects = projects;
                console.log("Projects Retrieved : ", this.projects);
            });
    }

    getFODataDictionaries(): void {
        this.foDdeService.getFunctionalOutputDictionaries()
            .subscribe(fo => {
                this.foDataDictionary = fo;
                console.log("FO DDs Retrieved : ", this.foDataDictionary);
            });
    }

    getAssetDataDictionaries() : void {
        this.assetDdeService.getAssetDictionaries()
            .subscribe(asset => {
                this.assetDataDictionary = asset;
                console.log("Asset DDs Retrieved ngAfterViewInit : ", this.assetDataDictionary);
            });
    }

    getFODDValue() : void {
        this.selectedFODD = (<HTMLInputElement>document. getElementById("foDDId")).value;
        console.log("selectedFO : ", this.selectedFODD);
    }

    getAssetDDValue() : void {
        this.selectedAssetDD = (<HTMLInputElement>document. getElementById("assetDDId")).value;
        console.log("selectedAssetDD : ", this.selectedAssetDD);
    }

    getProjectIdForRename(sourceProject : Project) : void {
        console.log("Getting Project Id of the Renamed Project - source project id = ", sourceProject.id);

        this.projectToRename = sourceProject;

        this.openModal('renameProjectContent', true);
    }

    getProjectIdForCopy(sourceProject : Project) : void {
        console.log("Project Id that needs to be copied : ", sourceProject.id);

        this.projectToCopy = sourceProject;

        this.openModal('copyProjectContent', true);
    }

    createProject(value: any): void {
        console.log("Creating Project");

        let projectName = value.projectName;
        let foDDId = value.foDDId;
        let assetDDId = value.assetDDId;

        console.log("Project Name: " + projectName + " -- foDDId : " + foDDId + " -- assetDDId : " + assetDDId);

        this.projectToSave = {
            id : '',
            name: projectName,
            fo_dd_id: foDDId,
            asset_dd_id: assetDDId
        };

        let project = this.projectToSave;

        console.log("Values sending to Project Service to Save", project);

        this.projectService.save(project)
            .subscribe(
                () => this.reloadComponent(),
                error => {
                    this.handleRestError(error);
                }
            );
        /* Closing Modal after sending Save */
        this.openModal('addProjectContent', false);
    }

    renameProject(value : any): void {
        console.log("Rename project - Value : ", value);
        if (this.projectToRename !== undefined) {
            this.projectToRename.name = value.renameName;

            let project = this.projectToRename;

            console.log("Project values sent for Rename Request:", project);

            this.projectService.rename(project)
                .subscribe(() => {
                       window.location.reload();
                    },
                    error => {
                        this.handleRestError(error);
                    }
                );
        }

        /* Closing Modal after sending Save */
        this.openModal('renameProjectContent', false);
        this.projectToRename = undefined;
    }

    copyProject(value : any) : void {
        console.log("Copying project with Id: " + this.projectToCopy + " as: " + value.copyName);
        if (this.projectToCopy !== undefined) {
            this.projectService.copy(this.projectToCopy.id, value.copyName)
                .subscribe(
                    () => this.reloadComponent(),
                    error => {
                        this.handleRestError(error);
                    }
                );
        }
        /* Closing Modal after sending Save */
        this.openModal('copyProjectContent', false);
        this.projectToCopy = undefined;
    }

    deleteProject(): void {
        console.log("Deleting Project with ID: ", this.projectToDelete);

        let project = this.projectToDelete;

        if (project != undefined) {
            this.projectService.delete(project.id)
                .subscribe(() => this.reloadComponent(),
                    error => {
                        this.handleRestError(error);
                    });

            /* Clear Project Information after Deletion */
            this.projectToDelete = undefined;
            project = undefined;
        }

        /* Closing Modal after Delete Confirmation */
        this.openDeleteConfirmationModal(undefined, false)

        /* Refreshing Screen */
        setTimeout(() => {

            },
            2000);
    }

    openModal(contentType : string, isOpen : boolean) : void {
        console.log("Selected Modal Content : ", contentType);

        if(isOpen) {
            if(contentType === 'addProjectContent') {
                this.addProjectIsOpen = isOpen;
            } else if (contentType === 'renameProjectContent') {
                this.renameProjectIsOpen = isOpen;
            } else if (contentType === 'copyProjectContent') {
                this.copyProjectIsOpen = isOpen;
            }
        } else if (!isOpen) {
            if(contentType === 'addProjectContent') {
                this.addProjectIsOpen = isOpen;
            } else if (contentType === 'renameProjectContent') {
                this.renameProjectIsOpen = isOpen;
            } else if (contentType === 'copyProjectContent') {
                this.copyProjectIsOpen = isOpen;
            }
        }
    }

    openDeleteConfirmationModal(project: Project | undefined, isOpen : boolean) : void {
        console.log("Opening delete confirmation Modal for Project Id: ", project);

        if(isOpen) {
            this.projectToDelete = project;
            this.deleteConfirmationModalIsOpen = isOpen;

        } else if (!isOpen) {
            this.deleteConfirmationModalIsOpen = isOpen;
        }
    }

    goToGraph(project: Project): void {
        this.projectDataService.setProject(project);
        console.log("Navigating to Graph page for project " + project);
        this.router.navigate(['/project']);
    }

    reloadComponent() {
        let currentUrl = this.router.url;
            this.router.routeReuseStrategy.shouldReuseRoute = () => false;
            this.router.onSameUrlNavigation = 'reload';
            this.router.navigate([currentUrl]);
    }

    handleRestError(error: any) {
        this.errorMessage = error.message;
        console.error('There was an error!', error);
        let msg = "";
        if (error.error.forUi == true) {
            msg = error.error.error;
        }
        else {
            // treat as generic backend error
            msg = "There was an error processing your request, please check and try again"
        }
        if (msg != "") {
            window.alert(msg);
        }
        this.reloadComponent();
    }
}

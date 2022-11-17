import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DashboardDialog} from '../../types/dashboard-dialog';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DictionaryType} from '../../types/dictionary-type';
import {BaseIoService} from '../../services/base/base-io-service';
import {forbiddenNameValidator} from "../copy-project-dialog/copy-project-dialog.component";

@Component({
  selector: 'app-import-dictionary-dialog',
  templateUrl: './import-dictionary-dialog.component.html',
  styleUrls: ['./import-dictionary-dialog.component.scss']
})
export class ImportDictionaryDialogComponent implements OnInit {

    @Output() closed = new EventEmitter<{ updated: boolean, dialog: DashboardDialog.IMPORT_DICTIONARY_DIALOG }>();
    @Output() hasError = new EventEmitter<any>();
    @Input() importData!: string;
    @Input() importFilename!: string;
    @Input() importType!: DictionaryType;
    importName!: string;

    dictionaryForm = new FormGroup({
        name: this.fb.control('', Validators.required)
    });

    constructor(private fb: FormBuilder,
                private ioService: BaseIoService) {
    }

    ngOnInit(): void {
        this.importName = DictionaryType[this.importType].replace('_', ' ');
        this.dictionaryForm = new FormGroup({
            name: this.fb.control(this.importFilename.split('.')[0], [Validators.required])
        });
        this.dictionaryForm.markAsDirty();
    }

    import(): void {
        if (this.dictionaryForm.invalid) {
            return;
        }
        let lines = this.importData.split('\r\n');
        lines = [this.dictionaryForm.controls.name.value, ...lines];
        this.importData = lines.join('\n');
        switch (this.importType){
            case DictionaryType.ASSET: {
                this.ioService.importDictionary(this.importData)
                    .then((data) => this.closed.emit({updated: true, dialog: DashboardDialog.IMPORT_DICTIONARY_DIALOG}))
                    .catch(err => this.hasError.emit(err));
                break;
            }
            default: {
                this.ioService.importFODictionary(this.importData)
                    .then((data) => this.closed.emit({updated: true, dialog: DashboardDialog.IMPORT_DICTIONARY_DIALOG}))
                    .catch(err => this.hasError.emit(err));
                break;
            }
        }

    }

    close(): void {
        this.closed.emit({updated: false, dialog: DashboardDialog.IMPORT_DICTIONARY_DIALOG});
    }
}

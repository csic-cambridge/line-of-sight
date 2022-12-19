import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-confirm-project-import-dialog',
  templateUrl: './confirm-project-import-dialog.component.html',
  styleUrls: ['./confirm-project-import-dialog.component.scss']
})
export class ConfirmProjectImportDialogComponent implements OnInit {

    @Output() closed = new EventEmitter<boolean>();
    constructor(public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
    }

    confirm(): void {
        this.closed.emit(true);
    }

    closeModal(): void {
        this.closed.emit(false);
    }
}

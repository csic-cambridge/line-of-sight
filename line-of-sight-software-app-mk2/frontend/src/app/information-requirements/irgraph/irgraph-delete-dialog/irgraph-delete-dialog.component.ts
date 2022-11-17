import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-irgraph-delete-dialog',
  templateUrl: './irgraph-delete-dialog.component.html',
  styleUrls: ['./irgraph-delete-dialog.component.scss']
})
export class IrgraphDeleteDialogComponent implements OnInit {

    @Input() message!: string;
    @Input() title!: string;
  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

    closeModal(): void {
        this.activeModal.close(false);
    }

    ok(): void {
        this.activeModal.close(true);
    }
}

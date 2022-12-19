import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-clear-showcase-dialog',
  templateUrl: './clear-showcase-dialog.component.html',
  styleUrls: ['./clear-showcase-dialog.component.scss']
})
export class ClearShowcaseDialogComponent implements OnInit {

    @Output() closed = new EventEmitter<boolean>();
  constructor() { }

  ngOnInit(): void {
  }

    delete(): void {
        localStorage.clear();
        this.closed.emit(true);
    }

    closeModal(): void {
        this.closed.emit(false);
    }
}

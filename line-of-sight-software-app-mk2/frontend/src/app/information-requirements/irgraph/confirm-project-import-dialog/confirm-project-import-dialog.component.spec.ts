import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmProjectImportDialogComponent } from './confirm-project-import-dialog.component';

describe('ConfirmProjectImportDialogComponent', () => {
  let component: ConfirmProjectImportDialogComponent;
  let fixture: ComponentFixture<ConfirmProjectImportDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfirmProjectImportDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmProjectImportDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

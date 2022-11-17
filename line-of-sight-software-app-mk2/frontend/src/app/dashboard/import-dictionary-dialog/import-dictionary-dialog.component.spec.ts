import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportDictionaryDialogComponent } from './import-dictionary-dialog.component';

describe('ImportDictionaryDialogComponent', () => {
  let component: ImportDictionaryDialogComponent;
  let fixture: ComponentFixture<ImportDictionaryDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImportDictionaryDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportDictionaryDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

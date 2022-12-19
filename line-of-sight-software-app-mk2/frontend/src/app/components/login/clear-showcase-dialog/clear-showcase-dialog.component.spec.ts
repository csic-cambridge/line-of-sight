import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClearShowcaseDialogComponent } from './clear-showcase-dialog.component';

describe('ClearShowcaseDialogComponent', () => {
  let component: ClearShowcaseDialogComponent;
  let fixture: ComponentFixture<ClearShowcaseDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClearShowcaseDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClearShowcaseDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OographDialogComponent } from './oograph-dialog.component';

describe('OographDialogComponent', () => {
  let component: OographDialogComponent;
  let fixture: ComponentFixture<OographDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OographDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OographDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

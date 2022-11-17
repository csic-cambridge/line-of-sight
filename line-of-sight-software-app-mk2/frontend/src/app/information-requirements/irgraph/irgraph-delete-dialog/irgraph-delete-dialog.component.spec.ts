import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IrgraphDeleteDialogComponent } from './irgraph-delete-dialog.component';

describe('IrgraphDeleteDialogComponent', () => {
  let component: IrgraphDeleteDialogComponent;
  let fixture: ComponentFixture<IrgraphDeleteDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IrgraphDeleteDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IrgraphDeleteDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

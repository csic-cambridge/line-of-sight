import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RenameProjectDialogComponent } from './rename-project-dialog.component';

describe('RenameProjectDialogComponent', () => {
  let component: RenameProjectDialogComponent;
  let fixture: ComponentFixture<RenameProjectDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RenameProjectDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RenameProjectDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

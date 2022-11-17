import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProjectDialogComponent } from './add-project-dialog.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ReactiveFormsModule} from '@angular/forms';

describe('AddProjectDialogComponent', () => {
  let component: AddProjectDialogComponent;
  let fixture: ComponentFixture<AddProjectDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, ReactiveFormsModule],
      declarations: [ AddProjectDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddProjectDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

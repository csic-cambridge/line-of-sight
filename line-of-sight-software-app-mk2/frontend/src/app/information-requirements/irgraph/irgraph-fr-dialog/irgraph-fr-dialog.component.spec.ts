import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IrgraphFrDialogComponent } from './irgraph-fr-dialog.component';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NgxBootstrapMultiselectModule} from 'ngx-bootstrap-multiselect';

describe('IrgraphFrDialogComponent', () => {
  let component: IrgraphFrDialogComponent;
  let fixture: ComponentFixture<IrgraphFrDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, FormsModule, ReactiveFormsModule, NgxBootstrapMultiselectModule],
      declarations: [ IrgraphFrDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IrgraphFrDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

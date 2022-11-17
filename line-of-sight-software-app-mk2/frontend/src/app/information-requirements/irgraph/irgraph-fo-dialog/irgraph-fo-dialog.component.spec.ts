import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IrgraphFoDialogComponent } from './irgraph-fo-dialog.component';
import {IrgraphFrDialogComponent} from '../irgraph-fr-dialog/irgraph-fr-dialog.component';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NgxBootstrapMultiselectModule} from 'ngx-bootstrap-multiselect';

describe('IrgraphFoDialogComponent', () => {
  let component: IrgraphFoDialogComponent;
  let fixture: ComponentFixture<IrgraphFoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, FormsModule, ReactiveFormsModule, NgxBootstrapMultiselectModule],
      declarations: [ IrgraphFoDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IrgraphFoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IrgraphOoDialogComponent } from './irgraph-oo-dialog.component';
import {IrgraphFrDialogComponent} from '../irgraph-fr-dialog/irgraph-fr-dialog.component';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NgxBootstrapMultiselectModule} from 'ngx-bootstrap-multiselect';

describe('IrgraphOoDialogComponent', () => {
  let component: IrgraphOoDialogComponent;
  let fixture: ComponentFixture<IrgraphOoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, FormsModule, ReactiveFormsModule, NgxBootstrapMultiselectModule],
      declarations: [ IrgraphOoDialogComponent]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IrgraphOoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

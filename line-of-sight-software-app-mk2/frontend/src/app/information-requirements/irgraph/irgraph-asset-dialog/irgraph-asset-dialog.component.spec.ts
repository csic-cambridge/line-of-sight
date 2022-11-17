import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IrgraphAssetDialogComponent } from './irgraph-asset-dialog.component';
import {IrgraphFrDialogComponent} from '../irgraph-fr-dialog/irgraph-fr-dialog.component';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NgxBootstrapMultiselectModule} from 'ngx-bootstrap-multiselect';

describe('IrgraphAssetDialogComponent', () => {
  let component: IrgraphAssetDialogComponent;
  let fixture: ComponentFixture<IrgraphAssetDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, FormsModule, ReactiveFormsModule, NgxBootstrapMultiselectModule],
      declarations: [ IrgraphAssetDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IrgraphAssetDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

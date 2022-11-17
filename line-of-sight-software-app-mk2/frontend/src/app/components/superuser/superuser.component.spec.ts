import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuperuserComponent } from './superuser.component';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormsModule} from '@angular/forms';

describe('SuperuserComponent', () => {
  let component: SuperuserComponent;
  let fixture: ComponentFixture<SuperuserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [RouterTestingModule, HttpClientTestingModule, FormsModule  ],
      declarations: [ SuperuserComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SuperuserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

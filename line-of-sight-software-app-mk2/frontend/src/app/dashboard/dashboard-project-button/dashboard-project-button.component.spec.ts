import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardProjectButtonComponent } from './dashboard-project-button.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ReactiveFormsModule} from '@angular/forms';

describe('DashboardProjectButtonComponent', () => {
  let component: DashboardProjectButtonComponent;
  let fixture: ComponentFixture<DashboardProjectButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, ReactiveFormsModule],
      declarations: [ DashboardProjectButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardProjectButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

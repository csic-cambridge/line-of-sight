import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectExportButtonComponent } from './project-export-button.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('ProjectExportButtonComponent', () => {
  let component: ProjectExportButtonComponent;
  let fixture: ComponentFixture<ProjectExportButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      declarations: [ ProjectExportButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectExportButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

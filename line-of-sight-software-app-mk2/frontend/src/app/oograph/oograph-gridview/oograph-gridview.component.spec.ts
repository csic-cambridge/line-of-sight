import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OographGridviewComponent } from './oograph-gridview.component';

describe('OographGridviewComponent', () => {
  let component: OographGridviewComponent;
  let fixture: ComponentFixture<OographGridviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OographGridviewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OographGridviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

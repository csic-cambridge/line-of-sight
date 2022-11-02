import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OOGraphComponent } from './oograph.component';

describe('OOGraphComponent', () => {
  let component: OOGraphComponent;
  let fixture: ComponentFixture<OOGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OOGraphComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OOGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

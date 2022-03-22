import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IRGraphComponent } from './irgraph.component';

describe('IRGraphComponent', () => {
  let component: IRGraphComponent;
  let fixture: ComponentFixture<IRGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IRGraphComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IRGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

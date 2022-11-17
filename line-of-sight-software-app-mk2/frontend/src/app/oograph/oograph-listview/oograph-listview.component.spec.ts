import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OographListviewComponent } from './oograph-listview.component';

describe('OographListviewComponent', () => {
  let component: OographListviewComponent;
  let fixture: ComponentFixture<OographListviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OographListviewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OographListviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

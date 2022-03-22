import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityLinkComponent } from './entity-link.component';

describe('EntityLinkComponent', () => {
  let component: EntityLinkComponent;
  let fixture: ComponentFixture<EntityLinkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityLinkComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EntityLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

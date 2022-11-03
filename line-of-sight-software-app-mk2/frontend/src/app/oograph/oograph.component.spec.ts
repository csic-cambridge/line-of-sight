import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OOGraphComponent } from './oograph.component';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('OOGraphComponent', () => {
    let component: OOGraphComponent;
    let fixture: ComponentFixture<OOGraphComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, FormsModule],
            declarations: [OOGraphComponent]
        }).compileComponents();
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

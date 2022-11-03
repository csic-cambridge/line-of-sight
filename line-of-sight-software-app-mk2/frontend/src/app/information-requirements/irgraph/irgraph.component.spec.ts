import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IRGraphComponent } from './irgraph.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NgxBootstrapMultiselectModule} from 'ngx-bootstrap-multiselect';

describe('IRGraphComponent', () => {
    let component: IRGraphComponent;
    let fixture: ComponentFixture<IRGraphComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, FormsModule, ReactiveFormsModule, NgxBootstrapMultiselectModule],
            declarations: [IRGraphComponent]
        }).compileComponents();
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

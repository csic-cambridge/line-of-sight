import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

//import { AppRoutingModule } from './app-routing.module';
import { RouterModule } from "@angular/router";
import { routing } from "./app-routing.module";
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from "@angular/forms";
//import { CommonModule } from "@angular/common";

import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http";
import { HttpAuthInterceptor } from "./http-auth.interceptor";
import { LoginComponent } from './login/login.component';
import { ReactiveFormsModule } from "@angular/forms";
import { AngularDraggableModule } from "angular2-draggable";
import { NgxBootstrapMultiselectModule } from 'ngx-bootstrap-multiselect';
import { DashboardComponent } from './dashboard/dashboard.component';
import { OOGraphComponent } from './oograph/oograph.component';
import { NotFoundComponent } from './not-found/not-found.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    OOGraphComponent,
    NotFoundComponent
  ],
    imports: [
        BrowserModule,
        //AppRoutingModule,
        RouterModule, routing,
        NgbModule,
        FormsModule,
        HttpClientModule,
        ReactiveFormsModule,
        AngularDraggableModule,
        NgxBootstrapMultiselectModule
    ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: HttpAuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }

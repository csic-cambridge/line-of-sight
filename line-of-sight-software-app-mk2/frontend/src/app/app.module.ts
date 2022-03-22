import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from "@angular/forms";

import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {HttpAuthInterceptor} from "./http-auth.interceptor";
import { LoginComponent } from './login/login.component';
import {ReactiveFormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NgbModule,
        FormsModule,
        HttpClientModule,
        ReactiveFormsModule
    ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: HttpAuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }

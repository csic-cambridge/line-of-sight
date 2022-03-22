import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Observable, of} from "rxjs";
import {delay} from "rxjs/operators";
import {AuthenticationService} from "../authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
    loginFormGroup: FormGroup = this.formBuilder.group({
        username: ['', Validators.required],
        password: ['', Validators.required]
    })
    running = false;
    submitted = false;

    constructor(
        private formBuilder: FormBuilder,
        private authService: AuthenticationService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loginFormGroup = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        })
    }

    onSubmit(): void {
        this.submitted = true;

        if(this.loginFormGroup?.invalid) {
            return;
        }

        this.running = true;

        // Do the login and then set running to false on completion
        this.authService.login(this.loginFormGroup.value).subscribe(_ => {
            this.running = false;
            console.log('Logged in', _);
            this.router.navigate(['/']);
        }, error => {
            this.running = false;
            // error.
            console.log('Failed to log in', error);
        });
    }

    get loginForm() {
        return this.loginFormGroup?.controls;
    }

}

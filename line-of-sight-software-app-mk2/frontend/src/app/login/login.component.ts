import {Component} from '@angular/core';
import {AuthenticationService} from '../authentication.service';
import {LoginProvider} from '../login-provider';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})

export class LoginComponent {
    providerData : Array<LoginProvider> | undefined;

    constructor(private authService: AuthenticationService) {
        authService.getProvider().subscribe(
            (oauthProviderData : Array<LoginProvider>) => {
                this.providerData = oauthProviderData;
            }
        );
    }
}

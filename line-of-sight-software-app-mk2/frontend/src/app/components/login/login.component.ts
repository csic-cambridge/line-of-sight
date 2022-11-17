import {Component} from '@angular/core';
import {AuthenticationService} from '../../services/authentication.service';
import {LoginProvider} from '../../types/login-provider';
import {BaseAuthenticationService} from '../../services/base/base-authentication.service';
import {OfflineAuthenticationService} from '../../services/offline/offline-authentication.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
    providers: []
})

export class LoginComponent {
    providerData: Array<LoginProvider> | undefined;

    constructor(private authService: BaseAuthenticationService) {
        authService.getProvider().subscribe(
            (oauthProviderData: Array<LoginProvider>) => {
                this.providerData = oauthProviderData;
            }
        );
    }
}

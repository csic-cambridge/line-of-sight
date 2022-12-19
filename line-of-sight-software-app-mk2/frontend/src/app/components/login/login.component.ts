import {Component} from '@angular/core';
import {LoginProvider} from '../../types/login-provider';
import {BaseAuthenticationService} from '../../services/base/base-authentication.service';
import {environment} from '../../../environments/environment';
import {Router} from '@angular/router';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
    providers: []
})

export class LoginComponent {
    providerData: Array<LoginProvider> | undefined;
    public env = environment;

    constructor(private router: Router,
                private authService: BaseAuthenticationService) {
        authService.getProvider().subscribe(
            (oauthProviderData: Array<LoginProvider>) => {
                this.providerData = oauthProviderData;
            }
        );
    }

    offlineLogin(): void {
        this.router.navigate(['/project']);
    }
}

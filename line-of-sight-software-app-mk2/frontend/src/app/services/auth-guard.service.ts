import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {BaseMeService} from './base/base-me-service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

    constructor(public auth: BaseMeService,
                public router: Router) {

    }

    canActivate(next: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): boolean {
        let enabled = true;
        this.auth.getMe().subscribe( () => enabled = true, error => {
            this.router.navigate(['/login']);
            enabled = false;
        });
        return enabled;
    }
}

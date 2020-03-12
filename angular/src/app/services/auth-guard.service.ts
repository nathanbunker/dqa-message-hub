import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from './authentication.service';
import { tap, map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthenticatedGuard implements CanActivate {

  constructor(private auth: AuthenticationService, private router: Router) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> {
    return this.auth.checkStatus()
      .pipe(
        map((auth) => {
          if (!auth.loggedIn) {
            this.router.navigate(['/login']);
          }
          return auth.loggedIn;
        })
      );
  }
}

@Injectable({
  providedIn: 'root',
})
export class NotAuthenticatedGuard implements CanActivate {

  constructor(private auth: AuthenticationService, private router: Router) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> {
    return this.auth.isAuthenticated$()
      .pipe(
        map((logged) => {
          if (logged) {
            this.router.navigate(['/home']);
          }
          return !logged;
        }),
      );
  }
}

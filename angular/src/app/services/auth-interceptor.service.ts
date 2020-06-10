import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { AuthenticationService, MessageStatus } from './authentication.service';
import { Observable, throwError } from 'rxjs';
import { catchError, take, flatMap } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {

  readonly skipURL = [
    'api/login',
    'api/logout',
    'api/logged',
  ];

  constructor(
    public auth: AuthenticationService,
    private router: Router) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((err) => {
        if (!this.skipURL.includes(request.url) && (err.status === 403 || err.status === 401)) {
          return this.auth.isAuthenticated$().pipe(
            take(1),
            flatMap((loggedIn) => {
              if (loggedIn) {
                this.auth.clearAuth('Session Timeout', MessageStatus.ERROR);
                this.router.navigate(['/', 'login']);
              }
              return throwError(err);
            }),
          );
        } else {
          return throwError(err);
        }
      }),
    );
  }

}

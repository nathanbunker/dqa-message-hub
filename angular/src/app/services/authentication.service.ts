import { Injectable } from '@angular/core';
import { Observable, Subject, BehaviorSubject, of, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { map, catchError, flatMap } from 'rxjs/operators';

export interface UserId {
  username: string;
  facilityId: string;
}

export enum MessageStatus {
  SUCCESS = 'SUCCESS',
  ERROR = 'ERROR',
  WARNING = 'WARNING',
}

export interface Authentication {
  checked: boolean;
  loggedIn: boolean;
  user: UserId;
  message: MessageHolder;
}

export interface MessageHolder {
  isDefined: boolean;
  status: MessageStatus;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private userSubject: BehaviorSubject<Authentication>;

  constructor(private http: HttpClient) {
    this.userSubject = new BehaviorSubject<Authentication>({
      checked: false,
      loggedIn: false,
      user: undefined,
      message: {
        isDefined: false,
        status: undefined,
        message: '',
      },
    });
  }

  clearAuth(message: string, status: MessageStatus) {
    this.userSubject.next({
      checked: true,
      loggedIn: false,
      user: undefined,
      message: {
        isDefined: true,
        message,
        status,
      }
    });
  }

  checkStatus(): Observable<Authentication> {
    return this.authStatus$().pipe(
      flatMap((auth) => {
        if (auth.checked) {
          return of(auth);
        } else {
          return this.http.get<UserId>('api/logged').pipe(
            map((user) => {
              const authToken = {
                checked: true,
                loggedIn: true,
                user,
                message: {
                  isDefined: true,
                  message: 'Login Success',
                  status: MessageStatus.SUCCESS,
                },
              };
              this.userSubject.next(authToken);
              return authToken;
            }),
            catchError((error) => {
              const authToken = {
                checked: true,
                loggedIn: false,
                user: undefined,
                message: {
                  isDefined: true,
                  message: 'Login Required',
                  status: MessageStatus.ERROR,
                },
              };
              this.userSubject.next(authToken);
              return of(authToken);
            })
          );
        }
      })
    );
  }

  logout() {
    const subject = new Subject<any>();
    this.http.get('api/logout').pipe(
      map((result) => {
        this.clearAuth('Logout Success', MessageStatus.SUCCESS);
        subject.next(result);
        subject.complete();
      }),
      catchError((error) => {
        subject.error(error);
        subject.complete();
        return error;
      })
    ).subscribe();

    return subject.asObservable();
  }

  authenticate(username: string, facilityId: string, password: string): Observable<UserId> {
    const subject = new Subject<UserId>();
    this.http.post<UserId>('api/login', { username, facilityId, password }).pipe(
      map((result) => {
        this.userSubject.next({
          checked: true,
          loggedIn: true,
          user: result,
          message: {
            isDefined: true,
            message: 'Login Success',
            status: MessageStatus.SUCCESS,
          },
        });

        subject.next(result);
        subject.complete();
      }),
      catchError((error) => {
        this.userSubject.next({
          checked: true,
          loggedIn: false,
          user: undefined,
          message: {
            isDefined: true,
            message: error.error.message,
            status: MessageStatus.ERROR,
          },
        });
        subject.error(error);
        subject.complete();
        return error;
      })
    ).subscribe();

    return subject.asObservable();
  }

  isAuthenticated$(): Observable<boolean> {
    return this.userSubject.asObservable().pipe(
      map((user) => {
        return user.loggedIn;
      })
    );
  }

  isAuthenticated(): boolean {
    return !!this.userSubject.getValue().loggedIn;
  }

  user$(): Observable<UserId> {
    return this.userSubject.asObservable().pipe(
      map((auth) => {
        return auth.user;
      })
    );
  }

  user(): UserId {
    return this.userSubject.getValue().user;
  }

  authStatus$(): Observable<Authentication> {
    return this.userSubject.asObservable();
  }

  authStatus(): Authentication {
    return this.userSubject.getValue();
  }

  message$(): Observable<MessageHolder> {
    return this.authStatus$().pipe(
      map((status) => {
        return status.message;
      })
    );
  }

}

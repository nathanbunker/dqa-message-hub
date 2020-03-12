import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthenticationService, MessageHolder } from '../services/authentication.service';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  message$: Observable<MessageHolder>;

  constructor(private auth: AuthenticationService, private router: Router) {
    this.loginForm = new FormGroup({
      username: new FormControl('', [Validators.required, Validators.minLength(5)]),
      facilityId: new FormControl('', [Validators.required, Validators.minLength(3)]),
      password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    });
    this.message$ = this.auth.message$();
  }

  login() {
    const formValue = this.loginForm.value;
    this.auth.authenticate(formValue.username, formValue.facilityId, formValue.password).pipe(
      map((result) => {
        console.log(result);
        this.router.navigate(['/', 'dashboard']);
      })
    ).subscribe();
  }

  ngOnInit() {
  }

}

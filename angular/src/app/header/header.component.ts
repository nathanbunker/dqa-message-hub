import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../services/authentication.service';
import { Observable } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(
    private auth: AuthenticationService,
    private router: Router,
  ) { }


  isLogged(): boolean {
    return this.auth.isAuthenticated();
  }

  logout() {
    this.auth.logout().pipe(
      map((result) => {
        this.router.navigate(['.', 'login']);
      })
    ).subscribe();
  }

  ngOnInit() {
  }

}

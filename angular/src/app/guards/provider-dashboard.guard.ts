import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { ProviderDashboardTab } from '../dashboard/provider/provider.component';

@Injectable()
export class ProviderDashboardGuard implements CanActivate {

  constructor(private router: Router, private active: ActivatedRoute) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean | UrlTree {
    if (route.params.tab && [
      ProviderDashboardTab.MESSAGES,
      ProviderDashboardTab.CODES,
      ProviderDashboardTab.DETECTIONS,
      ProviderDashboardTab.VACCINES,
    ].includes(route.params.tab)) {
      return true;
    } else {
      return false;
    }
  }
}

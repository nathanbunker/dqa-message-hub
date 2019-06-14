import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {concat, interval, Observable} from 'rxjs';
import { CalendarInfo, MessageDate } from "src/app/dashboard/calendar/calendar.component";

@Injectable({
  providedIn: 'root'
})
export class FacilityService {
  facilitiesUrl = 'api/facilities/';
  
  facilityList:Observable<string[]>;
  constructor(private $http: HttpClient) { 
    this.facilityList = this.getFacilityList();
   }

  getFacilityList(): Observable<string[]> {
    return this.$http.get<string[]>(this.facilitiesUrl);
  }

  getFacilityHistory(facilityName:string, year:number): Observable<CalendarInfo>  {
    return this.$http.get<CalendarInfo>('api/provider/'+facilityName+'/counts/year/'+year);;
  }
}
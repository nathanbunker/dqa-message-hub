import { Component, OnInit } from '@angular/core';
import { FacilityService } from '../facility.service';
import { Observable } from 'rxjs';
import { CalendarInfo } from 'src/app/dashboard/calendar/calendar.component';
import { Router } from '@angular/router';
import * as moment from 'moment';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  facilityList: Observable<string[]>;
  year1: Observable<CalendarInfo>;
  year2: Observable<CalendarInfo>;
  chosenFacility = '';

  constructor(private facilityService: FacilityService, private router: Router) { }

  dateSelected(date: Date) {
    this.router.navigate(['dashboard', this.chosenFacility, 'date', moment(date).format('YYYYMMDD')], {
      queryParams: {
        page: 1,
      }
    });
  }

  ngOnInit() {
    this.facilityList = this.facilityService.getFacilityList();
  }

  newFacility(facilityName) {
    console.log(facilityName);
    console.log('Using above line as facility');
    this.chosenFacility = facilityName;
    this.year1 = this.facilityService.getFacilityHistory(this.chosenFacility, 2019);
    this.year2 = this.facilityService.getFacilityHistory(this.chosenFacility, 2018);
  }
}

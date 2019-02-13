import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor() { }

  calendarCurrent: CalendarInfo = {
    year: '2019'
  };
  
  ngOnInit() {
  }

}

export interface CalendarInfo {
  year: string;
}

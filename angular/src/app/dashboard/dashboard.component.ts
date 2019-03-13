import { Component, OnInit } from '@angular/core';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor() { }

  calendarCurrent: CalendarInfo = {'provider': '', 'messageHistory':
    [{'day': '2019-02-13', 'count': 15}, {'day': '2019-02-14', 'count': 11}], 'year': 2019};



  ngOnInit() {
  }

}

export interface CalendarInfo {
  year: number;
  provider: string;
  messageHistory: any[];
}

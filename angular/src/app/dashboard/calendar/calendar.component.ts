import { Component, OnInit } from '@angular/core';
import { CalendarInfo } from 'src/app/dashboard/dashboard.component';
import { Input } from '@angular/core';


@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

  @Input()
  calendarInfo: CalendarInfo;

  constructor() { }

  ngOnInit() {
  }

}


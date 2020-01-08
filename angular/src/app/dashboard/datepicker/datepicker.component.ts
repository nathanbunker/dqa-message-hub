import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbDate } from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';

@Component({
  selector: 'app-datepicker',
  templateUrl: './datepicker.component.html',
  styleUrls: ['./datepicker.component.css']
})
export class DatepickerComponent implements OnInit {

  date: NgbDate;

  @Input()
  set dateString(value: string) {
    const _parsed = moment(value, 'YYYYMMDD');
    console.log(value);
    console.log(_parsed.year(), _parsed.month() + 1, _parsed.date());
    this.date = new NgbDate(_parsed.year(), _parsed.month() + 1, _parsed.date());
  }

  @Output()
  dateStringChange: EventEmitter<string>;

  ngOnInit() {
  }

  constructor() {
    this.dateStringChange = new EventEmitter<string>();
  }

  handleDateSelection(selected: NgbDate) {
    const str = moment({
      year: selected.year,
      month: selected.month - 1,
      day: selected.day,
    }).format('YYYYMMDD');
    this.dateStringChange.emit(str);
  }
}

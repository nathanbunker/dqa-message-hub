import { Component, OnInit, Input, Output } from '@angular/core';
import { EventEmitter } from 'events';

@Component({
  selector: 'app-datepicker',
  templateUrl: './datepicker.component.html',
  styleUrls: ['./datepicker.component.css']
})
export class DatepickerComponent implements OnInit {
  @Input()
  dateString: string;

  @Output()
  outputEmitter: EventEmitter<Date>;

  ngOnInit() {
  }

  constructor () {
  }

  handleDateSelection(selectedDate: Date) {
    this.outputEmitter.emit(selectedDate);
  }
}

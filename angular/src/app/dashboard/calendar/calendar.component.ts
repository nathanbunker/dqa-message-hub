import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Input } from '@angular/core';
import { GoogleChartInterface } from 'ng2-google-charts/google-charts-interfaces';
import * as moment from 'moment';


@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

  start: moment.Moment;
  end: moment.Moment;
  activity: CalendarInfo;
  chartData = [];
  selectedColors = { colors: ['#a2c0f3', '#9AF3BF'] };
  heatmapColors = { colors: ['#9AF3BF', '#259253'] };

  @Input()
  set startDate(date: string) {
    this.start = moment(date, 'YYYYMMDD');
    this.calendarChart.options.colorAxis = this.selectedColors;
  }

  @Input()
  set endDate(date: string) {
    this.end = moment(date, 'YYYYMMDD');
    this.calendarChart.options.colorAxis = this.selectedColors;
  }

  @Input()
  set calendarInfo(info: CalendarInfo) {
    this.activity = info;
    this.populateCalendar();
  }

  @Output()
  select: EventEmitter<Date>;
  header = [
    'Date',
    'Count',
    {
      id: 'tooltip',
      type: 'string',
      role: 'tooltip',
      'p': { 'html': true }
    }
  ];
  calendarChart = {
    chartType: 'Calendar',
    dataTable: [],
    options: {
      title: '',
      height: 250,
      calendar: {
        cellSize: 20,
        focusedCellColor: {
          stroke: '#d3362d',
          strokeOpacity: 1,
          strokeWidth: 1,
        },
        monthOutlineColor: {
          stroke: 'grey',
          strokeOpacity: 0.5,
          strokeWidth: 1
        }
      },
      legend: 'none',
      colorAxis: this.heatmapColors,
      tooltip: { isHtml: true },
    }
  };

  constructor() {
    this.select = new EventEmitter<Date>();
  }

  getTooltip(date: moment.Moment, activity: boolean, count: number, selected: boolean) {
    return '<div style="width: 200px; padding: 5px; text-align: center;">' +
      '<span style="font-weight: bold; font-size: 20px; text-align: center;">' + date.format('MM · DD · YYYY') + '</span><br>' +
      '<span style="font-weight: 500; font-size: 18px; text-align: center; margin-top: 5px;">' +
      (activity ? ('RECEIVED ' + count) : ('NO ACTIVITY')) + '</span><br>' +
      (selected ? '<span style="font-size: 15px; text-align: center; margin-top: 5px;" > (SELECTED) </span>' : '') +
      '</div>';
  }

  populateCalendar() {
    // Clear Chart Data
    this.calendarChart.dataTable = [
      this.header,
    ];
    // Initialize year variables
    const yearStart = moment(this.activity.year + '0101', 'YYYYMMDD');
    const yearEnd = moment(this.activity.year + '1231', 'YYYYMMDD');

    // start cursor at the beginning of the year
    let cursor = moment(this.activity.year + '0101', 'YYYYMMDD');
    let firstDate = true;

    // If we have data for the year
    if (this.activity.messageHistory && this.activity.messageHistory.length > 0) {

      // for each piece of data
      for (const data of this.activity.messageHistory) {
        const date = moment(data.day, 'YYYY-MM-DD');
        const value = data.count;

        // if the date is after star of the year
        if (yearStart.isBefore(date) && firstDate) {
          firstDate = false;
          // populate start of the year node
          this.pushToCalendar(yearStart);
        }

        // if the date is after previous populated date
        const days = cursor.diff(date, 'days');
        if (days < 1) {
          // fill the empty space between date and previous
          this.fillInEmptyDates(moment(cursor).add(1, 'days'), moment(date).subtract(1, 'days'));
        }

        // fill the current date
        this.pushToCalendar(date, value);

        // advance cursor to next date
        cursor = date;
      }

      // if last populate date is not the end of year date then populate empty space between last date and year end
      if (cursor.isBefore(yearEnd)) {
        this.fillInEmptyDates(moment(cursor).add(1, 'days'), yearEnd);
      }
    } else {
      // fill empty calendar
      this.fillInEmptyDates(yearStart, yearEnd);
      this.calendarChart.dataTable.push([
        yearStart.toDate(),
        NaN,
        this.getTooltip(yearStart, false, NaN, false),
      ]);
    }

    this.calendarChart = {
      ...this.calendarChart,
    };

    console.log(this.calendarChart);
  }

  fillInEmptyDates(start: moment.Moment, end: moment.Moment) {
    const dates = this.getAllDates(start, end);
    for (const date of dates) {
      this.pushToCalendar(date);
    }
  }

  pushToCalendar(date: moment.Moment, count?: number) {
    const selected = this.start && this.end && date.isBetween(this.start, this.end, 'day', '[]');
    const tooltip = this.getTooltip(date, count && count > 0, count, selected);
    const rangeValue = (selected ? -100 + (count >= 1 ? 140 : 0) : (count >= 1 ? 100 : null));

    this.calendarChart.dataTable.push(
      [
        date.toDate(),
        rangeValue,
        tooltip,
      ]
    );
  }

  getAllDates(_start: moment.Moment, _end: moment.Moment): moment.Moment[] {
    const dates: moment.Moment[] = [
      _start.clone(),
    ];

    while (_start.isBefore(_end)) {
      _start.add(1, 'days');
      dates.push(_start.clone());
    }

    return dates;
  }

  // So...  for some reason google charts sends the date back as if it were in UTC time...
  convertChartDateToLocalDate(chartDate) {
    const m = moment.utc(chartDate);
    const offsetMinutes = m.toDate().getTimezoneOffset();
    m.add(offsetMinutes, 'minutes');
    return m.toDate();
  }

  handleChartClick(selectedItem) {
    if (selectedItem && selectedItem.selectedRowValues[0]) {
      const selectedDate = this.convertChartDateToLocalDate(selectedItem.selectedRowValues[0]);
      this.select.emit(selectedDate);
    }
  }

  ngOnInit() {
  }
}

export interface CalendarInfo {
  year: number;
  provider: string;
  messageHistory: MessageDate[];
}

export interface MessageDate {
  day: string;
  count: number;
}

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

  searchOptions: {
    date: Date
  };
  resultsMetaData: {
    page: number
  };
  calendarDisplayYear: number;

  @Output()
  select: EventEmitter<Date>;
  _calendarInfo: CalendarInfo;
  @Input()
  set calendarInfo(info: CalendarInfo) {
    this.setDataSeries(info);
    this._calendarInfo = info;
  }
  resizeTimer: any;
  calendarChart: GoogleChartInterface = {
    chartType: 'Calendar',
    dataTable: [
      ['Date', 'Count'],
    ],
    options:
    {
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

      colorAxis: { colors: ['#9AF3BF', '#259253'] },
      tooltip: { isHtml: true },
    }
  };

  myChartObject = {
    type: 'Calendar', data: {
      'cols': [
        { id: 'Date', label: 'Day', type: 'date' },
        { id: 'count', label: 'Slices', type: 'number' },
        {
          id: 'tooltip',
          type: 'string',
          role: 'tooltip',
          'p': { 'html': true }
        }
      ],
      'rows': []
    }
  };

  constructor() {
    this.select = new EventEmitter<Date>();
    this.searchOptions = {
      date: null,
    };
    this.resultsMetaData = {
      page: 1,
    };
  }

  setDataSeries(calendarInfo: CalendarInfo) {

    this.calendarChart.dataTable = [['Date', 'Count']];

    if (!calendarInfo.messageHistory || calendarInfo.messageHistory.length < 1) {
      this.calendarChart.dataTable.push([new Date(calendarInfo.year, 0, 1), 0]);
    }

    calendarInfo.messageHistory.forEach((msgDate) => {
      this.calendarChart.dataTable.push(
        [this.convertChartDateToLocalDate(msgDate.day), msgDate.count]
      );
    });
    this.calendarChart = {
      ...this.calendarChart,
    };
  }

  // So...  for some reason google charts sends the date back as if it were in UTC time...
  convertChartDateToLocalDate(chartDate) {
    const m = moment.utc(chartDate);
    const offsetMinutes = m.toDate().getTimezoneOffset();
    m.add(offsetMinutes, 'minutes');
    return m.toDate();
  }

  handleChartClick(selectedItem) {
    console.log('handleChartClick');
    console.log(selectedItem);
    if (selectedItem) {
      const selectedDate = this.convertChartDateToLocalDate(selectedItem.selectedRowValues[0]);
      this.searchOptions.date = selectedDate;
      this.calendarDisplayYear = moment(
        this.searchOptions.date).year();
      this.resultsMetaData.page = 1;
      this.select.emit(selectedDate)
    }
  }

  ngOnInit() {
  }




  // onResized($event) {
  //   clearTimeout(this.resizeTimer);
  //   this.resizeTimer = setTimeout(function() {
  //     console.log("div is resized...");
  //     console.log(this.calendarChart);
  //     //force a redraw
  //         }, 500);
  // }
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

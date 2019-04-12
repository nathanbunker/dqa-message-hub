import { Component, OnInit } from '@angular/core';
import { Input } from '@angular/core';
import { GoogleChartInterface } from 'ng2-google-charts/google-charts-interfaces';
import * as moment from 'moment';


@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

  constructor() { }

  ngOnInit() {
    this.setDataSeries();
  }

  @Input()
  calendarInfo: CalendarInfo;

  resizeTimer:any;
  // onResized($event) {
  //   clearTimeout(this.resizeTimer);
  //   this.resizeTimer = setTimeout(function() {
  //     console.log("div is resized...");
  //     console.log(this.calendarChart);
  //     //force a redraw
  //         }, 500);
  // }
  
  setDataSeries() {
    this.calendarChart.dataTable = [['Date', 'Count']];
    // this.calendarChart.dataTable.push([ new Date(2019, 3, 13), 37032 ]);
    this.calendarInfo.messageHistory.forEach((msgDate) => {
      this.calendarChart.dataTable.push(
        [this.convertChartDateToLocalDate(msgDate.day),msgDate.count]
      )      
    });
  }

  // So...  for some reason google charts sends the date back as if it were in UTC time...
  convertChartDateToLocalDate(chartDate) {
    var m = moment.utc(chartDate);
    var offsetMinutes = m.toDate().getTimezoneOffset();
    m.add(offsetMinutes, 'minutes');
    return m.toDate();
  }

  handleChartClick = function (selectedItem) {
    console.log('handleChartClick');
    console.log(selectedItem);
    if (selectedItem) {
//        		&& selectedItem.row >= 0) {
      var selectedDate = this.convertChartDateToLocalDate(selectedItem.selectedRowValues[0]);
      this.searchOptions.date = selectedDate;
      this.calendarDisplayYear = moment(
          this.searchOptions.date).year();
      this.resultsMetaData.page = 1;
      // this.reloadPageData();
    }
  };

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

}

export interface CalendarInfo {
  year: number;
  provider: string;
  messageHistory: MessageDate[];
}

export interface MessageDate {
  day: string;
  count:number;
}

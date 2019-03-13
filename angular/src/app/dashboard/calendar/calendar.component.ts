import { Component, OnInit } from '@angular/core';
import { CalendarInfo } from 'src/app/dashboard/dashboard.component';
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

  @Input()
  calendarInfo: CalendarInfo;

  calendarChart: GoogleChartInterface = {
    chartType: 'Calendar',
    dataTable: [
      ['Date', 'Attendance'],
      [ new Date(2012, 3, 13), 37032 ],
      [ new Date(2012, 3, 14), 38024 ],
      [ new Date(2012, 3, 15), 38024 ],
      [ new Date(2012, 3, 16), 38108 ],
      [ new Date(2012, 3, 17), 38229 ]
    ],
    // opt_firstRowIsData: true,
    options: 
    //{'title': 'Date'},
    {
      title: '',
    
    //	        height: 220,
      calendar: {
        cellSize: 20,
    //	        	yearLabel : {display:'none', color:'grey', /*fontSize: 100*/},
    //	        forceIFrame: true, //This doesn't seem to work.  
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
    
      colorAxis: {colors: ['#9AF3BF', '#259253']},
      tooltip: {isHtml: true},
    }
  };

  handleChartClick = function (selectedItem) {
    console.log("handleChartClick");
    console.log(selectedItem);
    if (selectedItem) {
//        		&& selectedItem.row >= 0) {
      var selectedDate = this.convertChartDateToLocalDate(selectedItem.selectedRowValues[0]);
      this.searchOptions.date = selectedDate;
      this.calendarDisplayYear = moment(
          this.searchOptions.date).year();
      this.resultsMetaData.page = 1;
      //this.reloadPageData();
    }
  }

  ngOnInit() {
    //this.calendarChart.dataTable = this.calendarInfo;
  }

//So...  for some reason google charts sends the date back as if it were in UTC time...
convertChartDateToLocalDate(chartDate) {
  var m = moment.utc(chartDate);
  var offsetMinutes = m.toDate().getTimezoneOffset();
  m.add(offsetMinutes, 'minutes');
  return m.toDate();
}

myChartObject = {type:'Calendar', data: {
  "cols": [
    {id: "Date", label: "Day", type: "date"},
    {id: "count", label: "Slices", type: "number"},
    {
      id: "tooltip",
      type: 'string',
      role: 'tooltip',
      'p': {'html': true}
    }
  ],
  "rows": []
}
}

}


import { Component, OnInit } from '@angular/core';
import { ProviderReport } from '../dashboard/report/model';
import reportJson from '../../assets/exampleReport.json';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {

  report: ProviderReport;

  constructor() { }

  ngOnInit() {
    this.report = reportJson;
  }

  formatMessage(text: string) {
    return text.replace('\n', '<br/>');
  }

}

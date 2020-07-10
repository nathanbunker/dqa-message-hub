import { Component, OnInit, Input } from '@angular/core';
import { ProviderReport, CodeCount } from '../dashboard/report/model';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {

  report: ProviderReport;
  administered: CodeCount[];
  historical: CodeCount[];

  @Input()
  set data(data: ProviderReport) {
    this.report = data;
    this.administered = data.vaccinationCodes.filter((v) => v.attribute === 'Administered');
    this.historical = data.vaccinationCodes.filter((v) => v.attribute === 'Historical');
  }

  toggleShowMessage(error: any) {
    error.showMessage = !error.showMessage;
  }

  constructor() { }

  ngOnInit() {

  }

}

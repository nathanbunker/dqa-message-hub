import { Component, OnInit, Input } from '@angular/core';
import { ProviderReport, CodeCount, DetectionCount } from '../dashboard/report/model';
import { ReportingService } from '../services/reporting.service';
import { HighlightType } from '../hl7-view/hl7-part';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {

  @Input()
  provider: string;

  @Input()
  dateStart: string;

  @Input()
  dateEnd: string;


  report: ProviderReport;
  administered: CodeCount[];
  historical: CodeCount[];


  set data(data: ProviderReport) {
    this.report = data;
    this.administered = data.vaccinationCodes.filter((v) => v.attribute === 'Administered');
    this.historical = data.vaccinationCodes.filter((v) => v.attribute === 'Historical');
  }

  toggleExampleMessageForCode(code: CodeCount) {
    if (!code.showMessage) {
      if (!code.exampleMessage) {
        this.reportingService
          .getExampeMessageByCode(code.typeCode, code.value, this.provider, this.dateStart, this.dateEnd).subscribe((example) => {
            code.exampleMessage = example;
            code.exampleMessage.highlights = [{
              query: {
                genericPath: code.source,
                value: code.value,
              },
              type: HighlightType.ERROR,
            }];
            code.showMessage = true;
          });
      } else {
        code.showMessage = true;
      }
    } else {
      code.showMessage = false;
    }
  }

  toggleExampleMessageForDetection(detection: DetectionCount) {
    if (!detection.showMessage) {
      if (!detection.exampleMessage) {
        this.reportingService
          .getExampeMessageByDetection(detection.mqeCode, this.provider, this.dateStart, this.dateEnd).subscribe((example) => {
            detection.exampleMessage = example;
            detection.exampleMessage.highlights = example.locations.map((location) => {
              return {
                query: {
                  instancePath: location
                },
                type: HighlightType.ERROR,
              };
            });
            detection.showMessage = true;
          });
      } else {
        detection.showMessage = true;
      }
    } else {
      detection.showMessage = false;
    }
  }

  constructor(private reportingService: ReportingService) { }

  ngOnInit() {
    this.reportingService.getProviderReport(this.provider, this.dateStart, this.dateEnd).subscribe((data) => {
      this.data = data;
    });
  }

}

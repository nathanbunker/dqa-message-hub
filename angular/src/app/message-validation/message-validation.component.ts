import { Component, OnInit } from '@angular/core';
import { ValidationService, MqeMessage, MqeMessageEvaluation } from '../services/validation.service';
import { Observable, Subscription } from 'rxjs';
import { DetectionCount } from '../dashboard/report/model';

@Component({
  selector: 'app-message-validation',
  templateUrl: './message-validation.component.html',
  styleUrls: ['./message-validation.component.css']
})
export class MessageValidationComponent implements OnInit {

  exampleMessage: MqeMessage;
  exampleMessageSubscription: Subscription;
  messageText: string;
  validationResult: Observable<MqeMessageEvaluation>;

  warnArray: DetectionCount[][] = [];
  errorArray: DetectionCount[][] = [];
  infoArray: DetectionCount[][] = [];
  acceptArray: DetectionCount[][] = [];

  constructor(private validationService: ValidationService) { }

  getExample() {
    this.exampleMessageSubscription = this.validationService.getExampleMessage().subscribe(
      (message) => {
        this.exampleMessage = message;
        this.messageText = message.message;
      }
    );
  }

  submit() {
    this.validationResult = this.validationService.validateMessage(this.exampleMessage);

    this.validationResult.forEach(
      (rule) => {
        rule.mqeResponse.validationResults.forEach(
          (issue: { issues: DetectionCount[]; }) => this.issueFilter(issue.issues, 'WARN', this.warnArray),
          (issue: { issues: DetectionCount[]; }) => this.issueFilter(issue.issues, 'ERROR', this.errorArray)
        );
        rule.mqeResponse.validationResults.forEach(
          (issue: { issues: DetectionCount[]; }) => this.issueFilter(issue.issues, 'INFO', this.infoArray),
          (issue: { issues: DetectionCount[]; }) => this.issueFilter(issue.issues, 'ACCEPT', this.acceptArray)
        );
      }
    );
  }

  issueFilter(issues: DetectionCount[], severity: string, resultArray: DetectionCount[][]) {
    if (!issues) {
      return [];
    } else if (issues.length === 0) {
      return [];
    } else {
      resultArray.push(issues.filter(
        (issue) => issue.severity === severity
      ));
      return resultArray;
    }
  }

  ngOnInit() {
  }

}

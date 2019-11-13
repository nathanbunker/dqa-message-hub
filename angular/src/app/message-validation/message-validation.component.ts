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
  }

  issueFilter(issues: DetectionCount[], severity: string) {
    if (!issues) {
      return [];
    }
    return issues.filter(
      (issue) => issue.severity === severity
      );
  }

  ngOnInit() {
  }

}

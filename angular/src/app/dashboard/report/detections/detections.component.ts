import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Report, DetectionCount } from '../model';
import { MessageFilter } from '../../../services/reporting.service';

@Component({
  selector: 'app-detections',
  templateUrl: './detections.component.html',
  styleUrls: ['./detections.component.css']
})
export class DetectionsComponent implements OnInit {

  errors: DetectionCount[];
  warnings: DetectionCount[];

  @Output()
  filterBy: EventEmitter<string>;
  @Input()
  filters: MessageFilter;

  @Input()
  set report(report: Report) {
    if (report) {
      this.errors = report.detectionCounts.filter((detection) => detection.severity === 'ERROR');
      this.warnings = report.detectionCounts.filter((detection) => detection.severity === 'WARN');
    }
  }

  constructor() {
    this.filterBy = new EventEmitter<string>();
  }

  filter(code: string) {
    this.filterBy.emit(code);
  }

  ngOnInit() {
  }

}

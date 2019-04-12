import { Component, OnInit } from '@angular/core';
import { DocumentationService, DetectionDocumentation } from '../../services/documentation.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-detection-doc',
  templateUrl: './detection-doc.component.html',
  styleUrls: ['./detection-doc.component.css']
})
export class DetectionDocComponent implements OnInit {

  detections: Observable<DetectionDocumentation[]>;
  exportType: 'Table' | 'Document';
  activeFilter: boolean;

  constructor(private doc: DocumentationService) {
    this.exportType = 'Document';
  }

  ngOnInit() {
    this.detections = this.doc.getDocumentation();
  }

}

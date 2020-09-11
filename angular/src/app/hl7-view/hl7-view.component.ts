import { Component, Input, OnChanges, Output, EventEmitter, ViewChild, AfterViewInit } from '@angular/core';
import { HL7Part, HL7PartHighlight, HL7PartHighlightQuery } from './hl7-part';
import { HL7ParserService } from '../services/hl7-parser.service';

@Component({
  selector: 'app-hl7-view',
  templateUrl: './hl7-view.component.html',
  styleUrls: ['./hl7-view.component.css']
})

export class Hl7ViewComponent implements OnChanges, AfterViewInit {

  viewerId: string;

  @Input()
  highlight: HL7PartHighlightQuery[];
  @Input()
  hl7Text: string;
  @Output()
  textClicked = new EventEmitter();
  @ViewChild('container') container;

  testThings: HL7Part[][] = [];
  partsHighlight: HL7PartHighlight[];

  constructor(private parser: HL7ParserService) {
    this.viewerId = new Date().getTime() + '';
  }

  clickPart(part: HL7Part) {
    this.textClicked.emit(part);
  }

  ngOnChanges() {
    this.testThings = this.parser.parseLines(this.hl7Text);
    this.partsHighlight = [];
    if (this.highlight) {
      for (const elm of this.highlight) {
        this.partsHighlight = [
          ...this.partsHighlight,
          ...this.parser.query(elm.query, this.testThings).map((parts) => {
            return {
              hl7Path: parts.hl7PathString,
              type: elm.type,
            };
          }),
        ];
      }
    }
  }

  ngAfterViewInit() {
    const container = this.container.nativeElement;
    const [firstElement] = this.partsHighlight;
    if (firstElement) {
      const target = document.getElementById(`${firstElement.hl7Path}@${this.viewerId}`);
      container.scrollTo({
        top: target.offsetTop - container.offsetTop,
        left: target.offsetLeft - container.offsetLeft - (container.clientWidth / 2),
        behavior: 'smooth'
      });
    }
  }

  getHighlightClass(part: HL7Part) {
    if (this.partsHighlight) {
      const highlight = this.partsHighlight.find((hl) => {
        return hl.hl7Path === part.hl7PathString;
      });

      if (highlight) {
        return 'hl-' + highlight.type.toLowerCase();
      } else {
        return '';
      }
    } else {
      return '';
    }
  }
}

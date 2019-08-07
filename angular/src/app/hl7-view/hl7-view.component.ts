import { Component, Input, OnChanges, OnInit, Output, EventEmitter } from '@angular/core';
import { HL7Part } from './hl7-part';
import { Hl7Reference } from '../hl7-reference';

@Component({
  selector: 'app-hl7-view',
  templateUrl: './hl7-view.component.html',
  styleUrls: ['./hl7-view.component.css']
})

export class Hl7ViewComponent implements OnChanges {

  private fieldSplitList: string[] = ['|', '~', '^', '&'];

  @Input()
  hl7Text: string;

  @Output()
  textClicked = new EventEmitter();

  testThings: HL7Part[][] = [];

  constructor(private hl7Ref: Hl7Reference) {}

  clickPart(part: HL7Part) {
    this.textClicked.emit(part);
  }

  ngOnChanges() {
    console.log('reparsing!');
    this.testThings = this.parseLines(this.hl7Text);
  }

parseLines(message: string) {
    const lines: string[] = this.hl7Text.split(/[\n\r]{1,2}/);
    const parts: HL7Part[][] = [];
    let segIdx = 0;
    let textIdx = 0;
    lines.forEach(line => { if (line.length > 0) {parts.push(this.parseLine(line, segIdx++, textIdx)); textIdx += (line.length + 1);}});
    return parts;
  }

  parseSeparators(message: string) {

  }

  parseLine(line: string, lineNumber: number, lineStartIdx: number) {
    const firstPipeIdx:number = line.indexOf('|');
    const segment: string = line.substr(firstPipeIdx + 1);
    const segName: string = line.substr(0, firstPipeIdx);

    const segmentStart: HL7Part = new HL7Part();
    segmentStart.value = segName;
    segmentStart.location = segName + '-0';
    segmentStart.locationDescription = 'Segment Identifier';
    segmentStart.textStart = lineStartIdx;
    segmentStart.textEnd = (lineStartIdx + firstPipeIdx + 1);

    let lineParts: HL7Part[] = [];
    lineParts.push(segmentStart);

    if (segName !== 'MSH') {
      const segmentPipe: HL7Part = new HL7Part();
      segmentPipe.value = '|';
      lineParts.push(segmentPipe);
    }
    const thisLine: HL7Part = new HL7Part();
    thisLine.value = segment;
    thisLine.location = segName;
    thisLine.segmentIndex = lineNumber;
    lineParts = lineParts.concat(this.splitComponents(thisLine, this.fieldSplitList.slice(0), (lineStartIdx + firstPipeIdx)));
     return lineParts;
  }

  splitComponents(inputObj: HL7Part, splitStack: string[], partsStartIdx: number) {
    // Get the splitter for this sub-part. 
    const splitter: string = splitStack.shift();
    // Split the sub part using this splitter. 
    const subPart: string[] = inputObj.value.split(splitter);
    // If the split resulted in multiple parts, you need to process each part. 
    if (subPart.length > 1) {


      let idx = 1;
      let currentTextIdx: number = partsStartIdx;
      let partsList: HL7Part[] = [];

      // MSH-1 is special.  Handle it differently. 
      if (inputObj.location === 'MSH') {
        const newPart: HL7Part = Object.assign({}, inputObj);
        newPart.location = 'MSH-1';
        idx = 2;
        newPart.value = '|';
        newPart.textStart = currentTextIdx;
        newPart.textEnd = currentTextIdx + 1;
        newPart.locationDescription = this.hl7Ref.getDescriptionFor(newPart.location);
        partsList.push(newPart);
      }
      currentTextIdx += 1;

      let current: number = 0;

      subPart.forEach(sub => {
        //shallow copy
        const newPart: HL7Part = Object.assign({}, inputObj);

        newPart.value = sub;
        newPart.textStart = currentTextIdx;
        newPart.textEnd = currentTextIdx + sub.length;
        currentTextIdx += sub.length + 1/*for the splitter*/;

        if ('~' === splitter) {
          newPart.fieldRepetition = idx++;
        } else {
          newPart.location += ('-' + idx++);
        }

        let desc: string = this.hl7Ref.getDescriptionFor(newPart.location);
        if (!desc) {
          desc = newPart.location;
        }
        // console.log(desc);
        newPart.locationDescription = desc;

        if (newPart.location === 'MSH-2' || splitStack.length === 0) {
          // it's MSH-2 (which defines the splitters, so we are not going
          // to attempt to split it.  
          // or it's not MSH-2 and there's no more splits to make. 
          partsList.push(newPart);
        } else {
          // There are splits, and its not MSH-2. 
          // debugger;
          partsList = partsList.concat(this.splitComponents(newPart, splitStack.slice(0), newPart.textStart-1));
        }
        if (current < subPart.length - 1) {
          const sep: HL7Part = new HL7Part();
          sep.value = splitter;
          partsList.push(sep);
        }
        current++;
      });
      return partsList;
    } else if (splitStack.length > 0) {
      // There are more splitters to try.  The previous ones weren't found. 
      return this.splitComponents(inputObj, splitStack.slice(0), partsStartIdx);
    } else {
      // We tried to split it, and there were no splitters left to try.  This is the root level value. 
      return [inputObj];
    }
  }

}

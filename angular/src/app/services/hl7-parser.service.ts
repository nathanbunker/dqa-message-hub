import { Injectable } from '@angular/core';
import { HL7Path, HL7Part } from '../hl7-view/hl7-part';
import { Hl7Reference } from '../hl7-reference';

// const pathElements = query.genericPath
//   .match('(?<segment>[a-zA-Z0-9]{3})(-(?<field>\\d)(\\.(?<component>\\d)(\\.(?<subcomponent>\\d))?)?)?');

export interface IHL7Query {
  instancePath?: string;
  genericPath?: string;
  value?: string;
}

@Injectable({
  providedIn: 'root'
})
export class HL7ParserService {

  readonly fieldSplitList: string[] = ['|', '~', '^', '&'];

  constructor(private hl7Ref: Hl7Reference) { }

  query(query: IHL7Query, message: HL7Part[][]): HL7Part[] {
    const parts = this.getPathParts(query.instancePath || query.genericPath, message, !query.instancePath);
    if (query.value) {
      return parts.filter((part) => part.value === query.value);
    } else {
      return parts;
    }
  }

  partIsDataElement(part: HL7Part) {
    return !!part.location;
  }

  getPath(part: HL7Part, generic: boolean = false) {
    return generic ? part.location : part.hl7PathString;
  }

  getPathParts(path: string, message: HL7Part[][], generic: boolean = false): HL7Part[] {
    let cursor;
    const capture = [];

    for (const segment of message) {
      for (const element of segment) {

        if (this.partIsDataElement(element)) {
          const elmPath = this.getPath(element, generic);
          if (path.startsWith(elmPath)) {
            cursor = element;
          }
          if (elmPath.startsWith(path)) {
            capture.push(element);
          }
        }

      }
    }

    return capture.length > 0 ? capture : cursor ? [cursor] : [];
  }


  parseLines(message: string): HL7Part[][] {
    const lines: string[] = message.split(/[\n\r]{1,2}/);
    const parts: HL7Part[][] = [];
    let segIdx = 0;
    let textIdx = 0;
    const segmentInstanceMap = {};
    lines.forEach(line => {
      if (line.length > 0) {
        parts.push(this.parseLine(line, segIdx++, textIdx, segmentInstanceMap)); textIdx += (line.length + 1);
      }
    });
    return parts;
  }

  parseLine(line: string, lineNumber: number, lineStartIdx: number, segmentInstanceMap) {
    const firstPipeIdx: number = line.indexOf('|');
    const segment: string = line.substr(firstPipeIdx + 1);
    const segName: string = line.substr(0, firstPipeIdx);

    const segmentStart: HL7Part = new HL7Part();
    segmentStart.value = segName;
    segmentStart.location = segName + '-0';
    segmentStart.locationDescription = 'Segment Identifier';
    segmentStart.textStart = lineStartIdx;
    segmentStart.textEnd = (lineStartIdx + firstPipeIdx + 1);

    this.setPath(segmentStart, {
      segment: segName,
      segmentInstance: this.getInstanceOfSegment(segName, segmentInstanceMap),
    });

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
    thisLine.hl7Path = segmentStart.hl7Path;
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
        this.setPath(newPart, {
          ...inputObj.hl7Path,
          field: 1,
          fieldInstance: 1,
        });

        partsList.push(newPart);
      }
      currentTextIdx += 1;

      let current = 0;

      subPart.forEach(sub => {
        // shallow copy
        const newPart: HL7Part = Object.assign({}, inputObj);

        newPart.value = sub;
        newPart.textStart = currentTextIdx;
        newPart.textEnd = currentTextIdx + sub.length;
        currentTextIdx += sub.length + 1/*for the splitter*/;

        if ('~' === splitter) {
          newPart.fieldRepetition = idx;
        } else {
          newPart.location += ('-' + idx);
        }

        this.setPath(newPart, this.nextPath(splitter, newPart.hl7Path, idx));

        idx++;

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
          partsList = partsList.concat(this.splitComponents(newPart, splitStack.slice(0), newPart.textStart - 1));
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

  getInstanceOfSegment(segment: string, segmentInstanceMap): number {
    return segmentInstanceMap[segment] = segmentInstanceMap[segment] ? segmentInstanceMap[segment] + 1 : 1;
  }

  setPath(part: HL7Part, path: HL7Path) {
    part.hl7Path = path;
    part.hl7PathString = this.hl7PathToString(path);
  }

  hl7PathToString(path: HL7Path): string {
    const segment = `${path.segment}[${path.segmentInstance}]`;
    const field = path.field ? `-${path.field}[${path.fieldInstance}]` : '';
    const component = path.component ? `.${path.component}` : '';
    const subComponent = path.subComponent ? `.${path.subComponent}` : '';

    return segment + field + component + subComponent;
  }

  nextPath(splitter: string, parentPath: HL7Path, position: number): HL7Path {
    switch (splitter) {
      case '~':
        return {
          ...parentPath,
          fieldInstance: position,
        };
      case '|':
        return {
          ...parentPath,
          field: position,
          fieldInstance: 1,
        };
      case '^':
        return {
          ...parentPath,
          component: position,
        };
      case '&':
        return {
          ...parentPath,
          subComponent: position,
        };
    }
  }

}

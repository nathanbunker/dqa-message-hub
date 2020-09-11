import { IHL7Query } from '../services/hl7-parser.service';
export interface HL7PartHighlight {
  hl7Path: string;
  type: HighlightType;
}

export interface HL7PartHighlightQuery {
  query: IHL7Query;
  type: HighlightType;
}

export enum HighlightType {
  ERROR = 'ERROR',
  WARNING = 'WARNING'
}

export class HL7Part {
  value: string;
  location: string;
  fieldRepetition: number;
  segmentIndex: number;
  valueIndex: number;
  locationDescription: string;
  textStart: number;
  textEnd: number;
  hl7Path: HL7Path;
  hl7PathString: string;
}

export interface HL7Path {
  segment: string;
  segmentInstance: number;
  field?: number;
  fieldInstance?: number;
  component?: number;
  subComponent?: number;
}

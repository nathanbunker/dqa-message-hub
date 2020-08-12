import { HL7PartHighlight } from '../../hl7-view/hl7-part';
export interface Report {
  firstMessageReceived?: any;
  lastMessageReceived?: any;
  reportScore: ReportScore;
  detectionCounts: DetectionCount[];
  codes: Codes;
  messageCount: number;
  reportlabel?: any;
  scoreGroups: ScoreGroup[];
  timelinessCount: any;
  vaccineGroupCounts: any;
}

export interface MessageDetail {
  messageMetaData: any;
  providerKey: string;
  messageReceived: string;
  messageResponse: string;
  detections: any[];
  codes: any[];
}

export interface ScoreGroup {
  label: string;
  scores: Score[];
  objectCount: number;
  sectionObject?: any;
  requirementScores: any;
  sectionScore: ReportScore;
}


export interface Score {
  reportFieldDefinition: ReportFieldDefinition;
  issueScores: (IssueScore | IssueScore)[];
  fieldScore: ReportScore;
  expectedCount: number;
  presentCount: number;
}

export interface IssueScore {
  issueCount: number;
  issuePercentDemerit: number;
  issueDemerit: number;
  detectionType: string;
}

export interface ReportFieldDefinition {
  field: string;
  requirement: string;
  weight: number;
  issues: Issue[];
  checkForPresent: boolean;
  label: string;
  hl7Field: string;
}

export interface Issue {
  label?: any;
  type: string;
  upperPercentBoundary: number;
  lowerPercentBoundary: number;
  multiplierPercent: number;
}

export interface Codes {
  codeCount: CodeCount[];
}

export interface CodeCount {
  typeCode: string;
  typeName?: any;
  source?: any;
  exampleMessage?: IExampleMessage;
  attribute: string;
  value: string;
  count: number;
  status?: any;
  label?: any;
  showMessage?: boolean;
}

export interface DetectionCount {
  severity: string;
  reportedMessage: string;
  hl7LocationList: Hl7LocationList[];
  hl7ErrorCode: Hl7ErrorCode;
  applicationErrorCode: Hl7ErrorCode;
  exampleMessage?: IExampleMessage;
  howToFix?: string;
  whyToFix?: string;
  count: number;
  source: string;
  diagnosticMessage: string;
  showMessage?: boolean;
  mqeCode: string;
}

export interface IExampleMessage {
  message: string;
  locations: string[];
  highlights?: HL7PartHighlight[];
}

export interface Hl7ErrorCode {
  identifier: string;
  text: string;
  nameOfCodingSystem: string;
  alternateIdentifier: string;
  alternateText: string;
  nameOfAlternateCodingSystem: string;
}

export interface Hl7LocationList {
  line: number;
  segmentId: string;
  segmentSequence: number;
  fieldPosition: number;
  fieldRepetition: number;
  componentNumber: number;
  subComponentNumber: number;
  abbreviated: string;
  fieldLoc: string;
  baseReference: string;
}

export interface ReportScore {
  potential: number;
  scored: number;
}

export interface IMessageFilter {
  messageSearchText?: string;
  detectionId?: string;
  vaccineGroupAge?: string;
  vaccineGroup?: string;
  codeType?: string;
  codeValue?: string;
}

export interface Messages {
  pageNumber: number;
  itemsPerPage: number;
  totalMessages: number;
  totalPages: number;
  messageList: MessageList[];
}

export interface MessageList {
  id: number;
  messageControlId: string;
  cvxList: string;
  received: number;
  messageTime: number;
  ackStatus: string;
  patientName: string;
  errorsCount: number;
  warningsCount: number;
}

export interface VaccinationExpectedMap {
  map: {
    [vaccine: string]: VaccinationExpected,
  };
}

export interface VaccinationExpected {
  [ageGroup: string]: VaccinationExpectedData;
}

export interface VaccinationExpectedData {
  vaccinationVisits: number;
  count: number;
  status: string;
  vaccine: string;
  percent: number;
  age: AgeGroup;
  vaccineReportStatus: string;
  reportStyleClass: string;
  ageOrder: number;
}

export interface AgeGroup {
  label: string;
  ageLow: number;
  ageHigh: number;
  order: number;
}

export interface CodesMap {
  map: {
    label: CodeCount[]
  };
}

export interface ProviderReport {
  provider: string;
  startDate: number;
  endDate: number;
  numberOfMessage: number;
  numberOfErrors: number;
  errors: DetectionCount[];
  codeIssues: CodeCount[];
  countSummary?: SummaryReport;
  siteIdentifiers?: SiteIdentifier[];
  commonMessage?: string;
  vaccinationCodes?: CodeCount[];
}

export interface SummaryReport {
  messages: {
    errors: number;
    warnings: number;
    total: number;
  };
  patients: {
    children: number;
    adults: number;
    total: number;
  };
  vaccinations: {
    administered: number;
    historical: number;
    refusals: number;
    deletes: number;
    other: number;
    total: number;
  };
}

export interface SiteIdentifier {
  location: string;
  values: {
    value: string;
    count: number;
  }[];
}

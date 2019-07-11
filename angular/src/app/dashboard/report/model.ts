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
  attribute: string;
  value: string;
  count: number;
  status?: any;
  label?: any;
}

export interface DetectionCount {
  severity: string;
  reportedMessage: string;
  hl7LocationList: Hl7LocationList[];
  hl7ErrorCode: Hl7ErrorCode;
  applicationErrorCode: Hl7ErrorCode;
  count: number;
  source: string;
  diagnosticMessage: string;
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

import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ValidationService {

  constructor(private $http: HttpClient) { }

getExampleMessage(): Observable<MqeMessage> {
  return this.$http.get<MqeMessage>('api/messages/json/example');
}

validateMessage(messageText: MqeMessage): Observable<MqeMessageEvaluation> {
    // const form: FormData = new FormData();
    // form.append('submission', messageText);
    return this.$http.post<MqeMessageEvaluation>('api/messages/json/notsaved', messageText);
  }
}

export interface MqeMessage {
  message: string;
  user: string;
  password: string;
  sendingOrganization: string;
}

export interface MqeMessageEvaluation {
  vxu: string,
  ack: string,
  mqeResponse: {
    "messageObjects": any,
    "validationResults": {
      "issues": any,
      "rulePassed": true,
      "possible": string[],
      "ruleClass": string,
      "targetId": string,
      "targetType": string,
      "validationDetections": MqeDetection[]
    }
  }
}

export interface MqeDetection {
    "detection": string,
    "positionId": string,
    "valueReceived": string,
    "hl7LocationList": any[],
    "message": string,
    "error": boolean,
    "source": string,
    "severity": string,
    "reportedMessage": string,
    "diagnosticMessage": string,
    "applicationErrorCode": {
        "identifier": string,
        "text": string,
        "nameOfCodingSystem": string,
        "alternateIdentifier": string,
        "alternateText": string,
        "nameOfAlternateCodingSystem": string
    },
    "hl7ErrorCode": {
        "identifier": string,
        "text": string,
        "nameOfCodingSystem": string,
        "alternateIdentifier": string,
        "alternateText": string,
        "nameOfAlternateCodingSystem": string
    }
}

interface RootObject {
  vxu: string;
  ack: string;
  mqeResponse: MqeResponse;
  sender: string;
}

interface MqeResponse {
  messageObjects: MessageObjects;
  validationResults: ValidationResult[];
}

interface ValidationResult {
  issues: (Issue | Issues2 | Issues3 | Issues4 | Issues5 | Issues6)[];
  rulePassed: boolean;
  possible: string[];
  ruleClass: string;
  targetId?: any;
  targetType?: string;
  validationDetections: (Issue | Issues2 | Issues3 | Issues4 | Issues5 | Issues6)[];
}

interface Issues6 {
  detection: string;
  positionId: number;
  valueReceived: string;
  hl7LocationList: Hl7Location[];
  message: string;
  error: boolean;
  severity: string;
  source: string;
  hl7ErrorCode: Hl7ErrorCode;
  reportedMessage: string;
  diagnosticMessage?: any;
  applicationErrorCode: Hl7ErrorCode;
}

interface Issues5 {
  detection: string;
  positionId: number;
  valueReceived?: string;
  hl7LocationList: Hl7Location[];
  message: string;
  error: boolean;
  severity: string;
  source: string;
  hl7ErrorCode: Hl7ErrorCode;
  reportedMessage: string;
  diagnosticMessage?: any;
  applicationErrorCode: Hl7ErrorCode;
}

interface Issues4 {
  detection: string;
  positionId: number;
  valueReceived?: any;
  hl7LocationList: Hl7Location[];
  message: string;
  error: boolean;
  severity: string;
  source: string;
  hl7ErrorCode: Hl7ErrorCode;
  reportedMessage: string;
  diagnosticMessage?: any;
  applicationErrorCode: Hl7ErrorCode;
}

interface Issues3 {
  detection: string;
  positionId: number;
  valueReceived: string;
  hl7LocationList: Hl7Location[];
  message: string;
  error: boolean;
  severity: string;
  source: string;
  hl7ErrorCode: Hl7ErrorCode;
  reportedMessage: string;
  diagnosticMessage: string;
  applicationErrorCode: Hl7ErrorCode;
}

interface Issues2 {
  detection: string;
  positionId: number;
  valueReceived?: any;
  hl7LocationList: any[];
  message: string;
  error: boolean;
  severity: string;
  source: string;
  hl7ErrorCode: Hl7ErrorCode;
  reportedMessage: string;
  diagnosticMessage?: any;
  applicationErrorCode: Hl7ErrorCode;
}

interface Issue {
  detection: string;
  positionId: number;
  valueReceived: string;
  hl7LocationList: any[];
  message: string;
  error: boolean;
  severity: string;
  source: string;
  hl7ErrorCode: Hl7ErrorCode;
  reportedMessage: string;
  diagnosticMessage: string;
  applicationErrorCode: Hl7ErrorCode;
}

interface Hl7ErrorCode {
  identifier: string;
  text: string;
  nameOfCodingSystem: string;
  alternateIdentifier: string;
  alternateText: string;
  nameOfAlternateCodingSystem: string;
}

interface MessageObjects {
  receivedDate: number;
  messageHeader: MessageHeader;
  patient: Patient;
  nextOfKins: NextOfKin[];
  vaccinations: Vaccination[];
}

interface Vaccination {
  metaFieldInfoMap: MetaFieldInfoMap5;
  detectionList: string[];
  positionId: number;
  action: string;
  adminCodeList: any[];
  adminNdc: string;
  cvxDerived: string;
  adminDate: number;
  adminDatestring: string;
  adminDateEnd?: any;
  adminDateEndstring?: any;
  systemEntryDatestring?: any;
  systemEntryDate?: any;
  administered: boolean;
  amount: string;
  amountUnit: string;
  bodyRoute: string;
  bodySite: string;
  completion: string;
  enteredBy: Physician;
  expirationDate?: any;
  expirationDatestring?: any;
  facility: Facility2;
  facilityType: string;
  givenBy: Physician;
  idPlacer: string;
  idSubmitter: string;
  informationSource: string;
  lotNumber: string;
  manufacturer: string;
  observations: Observation[];
  orderControl: string;
  orderedBy: Physician;
  positionSubId: number;
  product: string;
  refusal: string;
  vaccinationId: number;
  refusalReason: string;
  vaccinationVis?: VaccinationVi;
  tradeName: string;
  vaccineValidity: string;
  vaccineGroupsDerived: string[];
  id?: any;
  actionCode: string;
  completionCode: string;
  adminCvxCode: string;
  adminCptCode: string;
  manufacturerCode: string;
  financialEligibilityCode: string;
  facilityIdNumber?: any;
  facilityName: string;
  facilityTypeCode: string;
  tradeNameCode: string;
  vaccineValidityCode: string;
  fundingSourceCode: string;
  amountUnitCode: string;
  bodyRouteCode: string;
  bodySiteCode: string;
  confidentialityCode: string;
  enteredByNameFirst: string;
  enteredByNameLast: string;
  enteredByNumber: string;
  givenByNameFirst: string;
  givenByNameLast: string;
  givenByNumber: string;
  informationSourceCode: string;
  orderControlCode: string;
  orderedByNameFirst: string;
  orderedByNameLast: string;
  orderedByNumber: string;
  refusalCode: string;
  actionAdd: boolean;
  actionDelete: boolean;
  actionUpdate: boolean;
  completionCompleted: boolean;
  completionNotAdministered: boolean;
  completionPartiallyAdministered: boolean;
  completionCompletedOrPartiallyAdministered: boolean;
  completionRefused: boolean;
}

interface VaccinationVi {
  visId: number;
  vaccination?: any;
  positionId: number;
  skipped: boolean;
  document: string;
  publishedDatestring: string;
  presentedDatestring: string;
  cvxCode: string;
  documentCode: string;
  publishedDate: number;
  presentedDate: number;
}

interface Observation {
  metaFieldInfoMap: MetaFieldInfoMap6;
  detectionList: any[];
  positionId: number;
  observationIdentifierDescription: string;
  observationValueDesc: string;
  observationDatestring: string;
  observationMethodCode: string;
  value: string;
  subId: string;
  identifierCode: string;
  valueTypeCode: string;
}

interface MetaFieldInfoMap6 {
  OBSERVATION_DATE_TIME_OF_OBSERVATION: MESSAGETRIGGER;
  OBSERVATION_VALUE_TYPE: MESSAGETRIGGER;
  OBSERVATION_VALUE_DESC: MESSAGETRIGGER;
  OBSERVATION_SUB_ID: MESSAGETRIGGER;
  OBSERVATION_IDENTIFIER_DESC: MESSAGETRIGGER;
  OBSERVATION_IDENTIFIER_CODE: MESSAGETRIGGER;
  OBSERVATION_VALUE: MESSAGETRIGGER;
}

interface Facility2 {
  name: string;
  id?: any;
  idNumber?: any;
}

interface MetaFieldInfoMap5 {
  VACCINATION_ADMIN_DATE: MESSAGETRIGGER;
  VACCINATION_CVX_CODE?: MESSAGETRIGGER;
  VACCINATION_GIVEN_BY: MESSAGETRIGGER;
  VACCINATION_ORDER_CONTROL_CODE: MESSAGETRIGGER;
  VACCINATION_FACILITY_NAME: MESSAGETRIGGER;
  VACCINATION_ADMINISTERED_AMOUNT: MESSAGETRIGGER;
  VACCINATION_SYSTEM_ENTRY_TIME: MESSAGETRIGGER;
  VACCINATION_FACILITY_ID: MESSAGETRIGGER;
  VACCINATION_ADMINISTERED_UNIT: MESSAGETRIGGER;
  VACCINATION_ADMIN_CODE: MESSAGETRIGGER;
  VACCINATION_REFUSAL_REASON: MESSAGETRIGGER;
  VACCINATION_FINANCIAL_ELIGIBILITY_CODE?: MESSAGETRIGGER;
  VACCINATION_MANUFACTURER_CODE: MESSAGETRIGGER;
  VACCINATION_COMPLETION_STATUS: MESSAGETRIGGER;
  VACCINATION_ACTION_CODE: MESSAGETRIGGER;
  VACCINATION_PLACER_ORDER_NUMBER: MESSAGETRIGGER;
  VACCINATION_BODY_ROUTE?: MESSAGETRIGGER;
  VACCINATION_LOT_NUMBER: MESSAGETRIGGER;
  VACCINATION_FILLER_ORDER_NUMBER: MESSAGETRIGGER;
  VACCINATION_ADMIN_DATE_END: MESSAGETRIGGER;
  VACCINATION_BODY_SITE?: MESSAGETRIGGER;
  VACCINATION_INFORMATION_SOURCE: MESSAGETRIGGER;
  VACCINATION_LOT_EXPIRATION_DATE: MESSAGETRIGGER;
  VACCINATION_NDC_CODE?: MESSAGETRIGGER;
}

interface NextOfKin {
  metaFieldInfoMap: MetaFieldInfoMap4;
  detectionList: string[];
  positionId: number;
  address: PatientAddressList;
  name: Alias;
  nextOfKinId: number;
  phone: Phone;
  relationship: string;
  skipped: boolean;
  primaryLanguageCode: string;
  email: string;
  namePrefix: string;
  relationshipCode: string;
  phoneNumber: string;
  nokRelationship: string;
  responsibleRelationship: boolean;
  childRelationship: boolean;
  nameFirst: string;
  nameLast: string;
  nameMiddle: string;
  nameSuffix: string;
  nameTypeCode: string;
}

interface MetaFieldInfoMap4 {
  NEXT_OF_KIN_ADDRESS_STREET: MESSAGETRIGGER;
  NEXT_OF_KIN_NAME_SUFFIX: MESSAGETRIGGER;
  NEXT_OF_KIN_RELATIONSHIP: MESSAGETRIGGER;
  NEXT_OF_KIN_PHONE_AREA_CODE: MESSAGETRIGGER;
  NEXT_OF_KIN_ADDRESS_ZIP: MESSAGETRIGGER;
  NEXT_OF_KIN_PHONE_TEL_EQUIP_CODE: MESSAGETRIGGER;
  NEXT_OF_KIN_PHONE_LOCAL_NUMBER: MESSAGETRIGGER;
  NEXT_OF_KIN_ADDRESS_STREET2: MESSAGETRIGGER;
  NEXT_OF_KIN_PHONE_TEL_USE_CODE: MESSAGETRIGGER;
  NEXT_OF_KIN_NAME_FIRST: MESSAGETRIGGER;
  NEXT_OF_KIN_ADDRESS_COUNTRY: MESSAGETRIGGER;
  NEXT_OF_KIN_NAME_MIDDLE: MESSAGETRIGGER;
  NEXT_OF_KIN_ADDRESS_STATE: MESSAGETRIGGER;
  NEXT_OF_KIN_ADDRESS_TYPE: MESSAGETRIGGER;
  NEXT_OF_KIN_PHONE: MESSAGETRIGGER;
  NEXT_OF_KIN_NAME_LAST: MESSAGETRIGGER;
  NEXT_OF_KIN_ADDRESS_COUNTY: MESSAGETRIGGER;
  NEXT_OF_KIN_PRIMARY_LANGUAGE: MESSAGETRIGGER;
  NEXT_OF_KIN_ADDRESS_CITY: MESSAGETRIGGER;
}

interface Patient {
  metaFieldInfoMap: MetaFieldInfoMap2;
  detectionList: string[];
  positionId: number;
  patientAddressList: PatientAddressList[];
  alias: Alias;
  birthDate: number;
  birthDatestring: string;
  birthMultipleInd: string;
  birthPlace: string;
  birthCounty: string;
  systemEntryDatestring: string;
  systemEntryDate?: any;
  deathDate?: any;
  deathDatestring: string;
  deathIndicator: string;
  ethnicity: string;
  facility: Facility;
  financialEligibility: string;
  financialEligibilityDate?: any;
  financialEligibilityDatestring?: any;
  idMedicaid: IdMedicaid;
  idRegistry: IdMedicaid;
  idSsn: IdMedicaid;
  idSubmitter: IdMedicaid;
  idWic: IdMedicaid;
  motherMaidenName: string;
  name: Alias;
  patientId: number;
  phone: Phone;
  physician: Physician;
  primaryLanguage: string;
  protection: string;
  publicity: string;
  race: string;
  registryStatus: string;
  patientClass: string;
  sex: string;
  skipped: boolean;
  registryStatusUniversal?: any;
  patientImmunityList: any[];
  responsibleParty: ResponsibleParty;
  email: string;
  namePrefix: string;
  patientAddress: PatientAddressList;
  financialEligibilityCode: string;
  underAged: boolean;
  nameFirst: string;
  nameLast: string;
  nameMiddle: string;
  sexCode: string;
  idSubmitterNumber: string;
  aliasFirst: string;
  aliasLast: string;
  aliasMiddle: string;
  aliasPrefix: string;
  aliasSuffix: string;
  aliasTypeCode: string;
  birthOrder: string;
  ethnicityCode: string;
  facilityIdNumber: string;
  facilityName: string;
  idMedicaidNumber: string;
  idRegistryNumber: string;
  idSsnNumber: string;
  idSubmitterAssigningAuthorityCode: string;
  idSubmitterTypeCode: string;
  nameSuffix: string;
  nameTypeCode: string;
  phoneNumber: string;
  physicianNameFirst: string;
  physicianNameLast: string;
  physicianNumber: string;
  primaryLanguageCode: string;
  protectionCode: string;
  publicityCode: string;
  raceCode: string;
  registryStatusCode: string;
  patientClassCode: string;
  idWicNumber: string;
}

interface ResponsibleParty {
  metaFieldInfoMap: MetaFieldInfoMap3;
  detectionList: any[];
  positionId: number;
  address: PatientAddressList;
  name: Alias;
  nextOfKinId: number;
  phone: Phone;
  relationship: string;
  skipped: boolean;
  primaryLanguageCode?: any;
  email: string;
  namePrefix: string;
  relationshipCode: string;
  phoneNumber: string;
  nokRelationship: string;
  responsibleRelationship: boolean;
  childRelationship: boolean;
  nameFirst: string;
  nameLast: string;
  nameMiddle: string;
  nameSuffix: string;
  nameTypeCode: string;
}

interface MetaFieldInfoMap3 {
  PATIENT_GUARDIAN_ADDRESS_STREET: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_STATE: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE_AREA_CODE: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_ZIP: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_CITY: MESSAGETRIGGER;
  PATIENT_GUARDIAN_RELATIONSHIP: MESSAGETRIGGER;
  PATIENT_GUARDIAN_NAME_FIRST: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE_LOCAL_NUMBER: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_STREET2: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE_TEL_EQUIP_CODE: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_COUNTRY: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE_TEL_USE_CODE: MESSAGETRIGGER;
  PATIENT_GUARDIAN_NAME_LAST: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE: MESSAGETRIGGER;
}

interface Physician {
  assigningAuthority: string;
  name: Alias;
  number: string;
  type: string;
  empty: boolean;
  typeCode: string;
  assigningAuthorityCode: string;
}

interface Phone {
  singleFieldinput: string;
  telUse: string;
  telEquip: string;
  email: string;
  countryCode: string;
  areaCode: string;
  localNumber: string;
  extension: string;
  telUseCode: string;
  telEquipCode: string;
  formattedNumber: string;
}

interface IdMedicaid {
  assigningAuthority: string;
  name: Alias;
  number: string;
  type: string;
  idNumberId: number;
  positionId: number;
  skipped: boolean;
  empty: boolean;
  typeCode: string;
  assigningAuthorityCode: string;
}

interface Facility {
  name: string;
  id: string;
  idNumber: string;
}

interface Alias {
  last: string;
  first: string;
  middle: string;
  suffix: string;
  prefix: string;
  type: string;
  typeCode: string;
  fullName: string;
}

interface PatientAddressList {
  street: string;
  street2: string;
  city: string;
  stateCode: string;
  zip: string;
  type: string;
  cleansingResultCode: string;
  lattitude: number;
  longitude: number;
  clean: boolean;
  cleansingAttempted: boolean;
  empty: boolean;
  typeCode: string;
  countryCode: string;
  countyParishCode: string;
}

interface MetaFieldInfoMap2 {
  PATIENT_PROTECTION_INDICATOR: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_STATE: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE_AREA_CODE: MESSAGETRIGGER;
  PATIENT_BIRTH_DATE: MESSAGETRIGGER;
  PATIENT_BIRTH_ORDER: MESSAGETRIGGER;
  PATIENT_SUBMITTER_ID_AUTHORITY: MESSAGETRIGGER;
  PATIENT_CLASS: MESSAGETRIGGER;
  PATIENT_ADDRESS_ZIP: MESSAGETRIGGER;
  PATIENT_PHONE_LOCAL_NUMBER: MESSAGETRIGGER;
  PATIENT_PHONE_TEL_USE_CODE: MESSAGETRIGGER;
  PATIENT_GUARDIAN_RELATIONSHIP: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_STREET2: MESSAGETRIGGER;
  PATIENT_ADDRESS_STATE: MESSAGETRIGGER;
  PATIENT_DEATH_DATE: MESSAGETRIGGER;
  PATIENT_SUBMITTER_ID: MESSAGETRIGGER;
  PATIENT_GUARDIAN_NAME_LAST: MESSAGETRIGGER;
  PATIENT_RACE: MESSAGETRIGGER;
  PATIENT_NAME_MIDDLE: MESSAGETRIGGER;
  PATIENT_PHONE_TEL_EQUIP_CODE: MESSAGETRIGGER;
  PATIENT_NAME_FIRST: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE_LOCAL_NUMBER: MESSAGETRIGGER;
  PATIENT_BIRTH_INDICATOR: MESSAGETRIGGER;
  PATIENT_NAME_LAST: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE: MESSAGETRIGGER;
  PATIENT_ADDRESS_COUNTRY: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_STREET: MESSAGETRIGGER;
  PATIENT_ADDRESS_STREET2: MESSAGETRIGGER;
  PATIENT_PHONE_AREA_CODE: MESSAGETRIGGER;
  PATIENT_NAME_SUFFIX: MESSAGETRIGGER;
  PATIENT_VFC_EFFECTIVE_DATE: MESSAGETRIGGER;
  PATIENT_ADDRESS_CITY: MESSAGETRIGGER;
  PATIENT_ADDRESS_TYPE: MESSAGETRIGGER;
  PATIENT_MOTHERS_MAIDEN_NAME: MESSAGETRIGGER;
  PATIENT_GUARDIAN_NAME_MIDDLE: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE_TEL_EQUIP_CODE: MESSAGETRIGGER;
  PATIENT_GENDER: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_COUNTRY: MESSAGETRIGGER;
  PATIENT_VFC_STATUS: MESSAGETRIGGER;
  PATIENT_PUBLICITY_CODE: MESSAGETRIGGER;
  PATIENT_DEATH_INDICATOR: MESSAGETRIGGER;
  PATIENT_ETHNICITY: MESSAGETRIGGER;
  PATIENT_BIRTH_PLACE: MESSAGETRIGGER;
  PATIENT_ADDRESS_COUNTY: MESSAGETRIGGER;
  PATIENT_PHONE: MESSAGETRIGGER;
  PATIENT_NAME_TYPE_CODE: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_ZIP: MESSAGETRIGGER;
  PATIENT_GUARDIAN_ADDRESS_CITY: MESSAGETRIGGER;
  PATIENT_REGISTRY_STATUS: MESSAGETRIGGER;
  PATIENT_GUARDIAN_NAME_FIRST: MESSAGETRIGGER;
  PATIENT_GUARDIAN_EMAIL: MESSAGETRIGGER;
  PATIENT_ADDRESS_STREET: MESSAGETRIGGER;
  PATIENT_SUBMITTER_ID_TYPE_CODE: MESSAGETRIGGER;
  PATIENT_GUARDIAN_PHONE_TEL_USE_CODE: MESSAGETRIGGER;
  PATIENT_PRIMARY_LANGUAGE: MESSAGETRIGGER;
  PATIENT_PRIMARY_FACILITY_ID: MESSAGETRIGGER;
}

interface MessageHeader {
  metaFieldInfoMap: MetaFieldInfoMap;
  detectionList: string[];
  positionId: number;
  headerId: number;
  characterSet: string;
  characterSetAlt: string;
  country: string;
  messageControl: string;
  messageDate: number;
  messageDatestring: string;
  messageProfile: string;
  messageStructure: string;
  messageTrigger: string;
  messageType: string;
  processingStatus: string;
  receivingApplication: string;
  receivingFacility: string;
  sendingApplication: string;
  sendingFacility: string;
  messageVersion: string;
  sendingRespOrg: string;
  ackTypeAcceptCode: string;
  ackTypeApplicationCode: string;
  characterSetCode: string;
  characterSetAltCode: string;
  countryCode: string;
  processingStatusCode: string;
}

interface MetaFieldInfoMap {
  MESSAGE_TRIGGER: MESSAGETRIGGER;
  MESSAGE_SENDING_APPLICATION: MESSAGETRIGGER;
  MESSAGE_TYPE: MESSAGETRIGGER;
  MESSAGE_CONTROL_ID: MESSAGETRIGGER;
  MESSAGE_SENDING_FACILITY: MESSAGETRIGGER;
  MESSAGE_RECEIVING_APPLICATION: MESSAGETRIGGER;
  MESSAGE_DATE: MESSAGETRIGGER;
  MESSAGE_PROFILE_ID: MESSAGETRIGGER;
  MESSAGE_STRUCTURE: MESSAGETRIGGER;
  MESSAGE_RECEIVING_FACILITY: MESSAGETRIGGER;
  MESSAGE_VERSION: MESSAGETRIGGER;
  MESSAGE_PROCESSING_ID: MESSAGETRIGGER;
  MESSAGE_ACCEPT_ACK_TYPE: MESSAGETRIGGER;
  MESSAGE_APP_ACK_TYPE: MESSAGETRIGGER;
}

interface MESSAGETRIGGER {
  value: string;
  hl7Location: Hl7Location;
  vxuField: string;
}

interface Hl7Location {
  line: number;
  segmentId: string;
  segmentSequence: number;
  fieldPosition: number;
  fieldRepetition: number;
  componentNumber: number;
  subComponentNumber: number;
  fieldLoc: string;
  baseReference: string;
  abbreviated: string;
}
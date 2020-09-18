import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import {
  Messages,
  IMessageFilter,
  Report,
  CodesMap,
  AgeGroup,
  VaccinationExpectedMap,
  ProviderReport,
  IHL7MessageView
} from '../dashboard/report/model';

@Injectable({
  providedIn: 'root'
})
export class ReportingService {

  constructor(private http: HttpClient) { }

  // DO BACK
  getMessageList(
    provider: string,
    dateStart: string,
    dateEnd: string,
    page: number,
    itemsPerPage: number,
    filter?: string): Observable<Messages> {
    return this.http.get<Messages>(
      'api/messages/' + provider + '/date/' + dateStart + '/' + dateEnd + '/messages/' + itemsPerPage + '/page/' + page + '/',
      {
        params: {
          filters: filter || '',
        }
      });
  }

  getProviderReport(provider: string, dateStart: string, dateEnd: string): Observable<ProviderReport> {
    return this.http.get<ProviderReport>('api/report/complete/' + provider + '/start/' + dateStart + '/end/' + dateEnd);
  }

  getExampeMessageByDetection(
    detection: string,
    provider: string,
    dateStart: string,
    dateEnd: string): Observable<IHL7MessageView> {
    return this.http.get<IHL7MessageView>(
      'api/report/example/detection/' + detection + '/' + provider + '/start/' + dateStart + '/end/' + dateEnd
    );
    // return of({
    //   message: 'MSH|^~\&|Test EHR Application|X68||NIST Test Iz Reg|20120701082240-0500||VXU^V04^VXU_V04|NIST-IZ-001.00|P|2.5.1|||ER|AL|||||Z22^CDCPHINVS\n'
    //     + 'PID|1||D26376273^^^NIST MPI^MR||Snow^Madelynn^Ainsley^^^^L|Lam^Morgan^^^^^M|20070706|F||2076-8^Native Hawaiian or Other Pacific Islander^CDCREC|32 Prescott Street Ave^^Warwick^MA^02452^USA^L||^PRN^PH^^^657^5558563|||||||||2186-5^non Hispanic or Latino^CDCREC\n'
    //     + 'PD1|||||||||||02^Reminder/Recall - any method^HL70215|||||A|20120701|20120701\n'
    //     + 'NK1|1|Lam^Morgan^^^^^L|MTH^Mother^HL70063|32 Prescott Street Ave^^Warwick^MA^02452^USA^L|^PRN^PH^^^657^5558563\n'
    //     + 'ORC|RE||IZ-783274^NDA|||||||I-23432^Burden^Donna^A^^^^^NIST-AA-1^^^^PRN||57422^RADON^NICHOLAS^^^^^^NIST-AA-1^L^^^MD\n'
    //     + 'RXA|0|1|20120814||33332-0010-01^Influenza, seasonal, injectable, preservative free^NDC|0.5|CM^MilliLiter [SI Volume Units]^UCUM||00^New immunization record^NIP001|7832-1^Lemon^Mike^A^^^^^NIST-AA-1^^^^PRN|^^^X68||||Z0860BB|20121104|CSL^CSL Behring^MVX|||CP|A\n'
    //     + 'RXR|C28161^Intramuscular^NCIT|LD^Left Arm^HL70163\n'
    //     + 'OBX|1|CE|64994-7^Vaccine funding program eligibility category^LN|1|V05^VFC eligible - Federally Qualified Health Center Patient (under-insured)^HL70064||||||F|||20120701|||VXC40^Eligibility captured at the immunization level^CDCPHINVS\n'
    //     + 'OBX|2|CE|30956-7^vaccine type^LN|2|88^Influenza, unspecified formulation^CVX||||||F\n'
    //     + 'OBX|3|TS|29768-9^Date vaccine information statement published^LN|2|20120702||||||F\n'
    //     + 'OBX|4|TS|29769-7^Date vaccine information statement presented^LN|2|20120814||||||F',
    //   locations: ['PID[1]-11[1].1[1]']
    // });
  }

  getExampeMessageByCode(
    codeType: string,
    code: string,
    provider: string,
    dateStart: string,
    dateEnd: string): Observable<IHL7MessageView> {
    return this.http.get<IHL7MessageView>(
      'api/report/example/code/' + codeType + '/' + code + '/' + provider + '/start/' + dateStart + '/end/' + dateEnd
    );
    // return of({
    //   message: 'MSH|^~\&|Test EHR Application|X68||NIST Test Iz Reg|20120701082240-0500||VXU^V04^VXU_V04|NIST-IZ-001.00|P|2.5.1|||ER|AL|||||Z22^CDCPHINVS\n'
    //     + 'PID|1||D26376273^^^NIST MPI^MR||Snow^Madelynn^Ainsley^^^^L|Lam^Morgan^^^^^M|20070706|F||108 N Main St^Native Hawaiian or Other Pacific Islander^CDCREC|32 Prescott Street Ave^^Warwick^MA^02452^USA^L||^PRN^PH^^^657^5558563|||||||||2186-5^non Hispanic or Latino^CDCREC\n'
    //     + 'PD1|||||||||||02^Reminder/Recall - any method^HL70215|||||A|20120701|20120701\n'
    //     + 'NK1|1|Lam^Morgan^^^^^L|MTH^Mother^HL70063|32 Prescott Street Ave^^Warwick^MA^02452^USA^L|^PRN^PH^^^657^5558563\n'
    //     + 'ORC|RE||IZ-783274^NDA|||||||I-23432^Burden^Donna^A^^^^^NIST-AA-1^^^^PRN||57422^RADON^NICHOLAS^^^^^^NIST-AA-1^L^^^MD\n'
    //     + 'RXA|0|1|20120814||33332-0010-01^Influenza, seasonal, injectable, preservative free^NDC|0.5|CM^MilliLiter [SI Volume Units]^UCUM||00^New immunization record^NIP001|7832-1^Lemon^Mike^A^^^^^NIST-AA-1^^^^PRN|^^^X68||||Z0860BB|20121104|CSL^CSL Behring^MVX|||CP|P\n'
    //     + 'RXR|MMR^Intramuscular^NCIT|LD^Left Arm^HL70163\n'
    //     + 'OBX|1|CE|64994-7^Vaccine funding program eligibility category^LN|1|V05^VFC eligible - Federally Qualified Health Center Patient (under-insured)^HL70064||||||F|||20120701|||VXC40^Eligibility captured at the immunization level^CDCPHINVS\n'
    //     + 'OBX|2|CE|30956-7^vaccine type^LN|2|88^Influenza, unspecified formulation^CVX||||||F\n'
    //     + 'OBX|3|TS|29768-9^Date vaccine information statement published^LN|2|20120702||||||F\n'
    //     + 'OBX|4|TS|29769-7^Date vaccine information statement presented^LN|2|20120814||||||F'
    // });
  }

  getVaccinationReportGroupList(provider: string): Observable<string[]> {
    return this.http.get<string[]>('api/codes/vaccineReportGroupList/' + provider);
  }

  getVaccinationsExpected(provider: string, dateStart: string, dateEnd: string): Observable<VaccinationExpectedMap> {
    return this.http.get<VaccinationExpectedMap>('api/codes/vaccinationsExpected/' + provider + '/' + dateStart + '/' + dateEnd);
  }

  getAgeCategoryList(provider: string): Observable<AgeGroup[]> {
    return this.http.get<AgeGroup[]>('api/codes/ageCategoryList/' + provider);
  }

  getVaccination(provider: string, dateStart: string, dateEnd: string): Observable<VaccinationExpectedMap> {
    return this.http.get<VaccinationExpectedMap>('api/codes/vaccinations/' + provider + '/' + dateStart + '/' + dateEnd);
  }

  getReport(provider: string, dateStart: string, dateEnd: string): Observable<Report> {
    return this.http.get<Report>('api/report/' + provider + '/date/' + dateStart + '/' + dateEnd);
  }

  getCodesReceivedList(provider: string, dateStart: string, dateEnd: string, filter?: string): Observable<CodesMap> {
    return this.http.get<CodesMap>('api/codes/' + provider + '/' + dateStart + '/' + dateEnd + '/', {
      params: {
        filters: filter || '',
      }
    });
  }

  filterAsString(filter: MessageFilter): string {
    const filterValues = [];
    for (const key of Object.keys(filter)) {
      if (filter.hasOwnProperty(key) && filter[key]) {
        filterValues.push(key + '::' + filter[key]);
      }
    }

    return filterValues.join('|');
  }

  filterFromString(filter: MessageFilter): string {
    const filterValues = [];
    for (const key of Object.keys(filter)) {
      if (filter.hasOwnProperty(key) && filter[key]) {
        filterValues.push(key + '::' + filter[key]);
      }
    }

    return filterValues.join('|');
  }
}

export class MessageFilter {
  constructor(private filter: IMessageFilter) { }

  static filterFromString(str: string): MessageFilter {
    const filter: IMessageFilter = {};
    str.split('|').forEach((part) => {
      const pair = part.split('::');
      if (pair.length === 2) {
        filter[pair[0]] = pair[1];
      }
    });
    return new MessageFilter(filter);
  }

  filterAsString(): string {
    const filterValues = [];
    for (const key of Object.keys(this.filter)) {
      if (this.filter.hasOwnProperty(key) && this.filter[key]) {
        filterValues.push(key + '::' + this.filter[key]);
      }
    }
    return filterValues.join('|');
  }

  merge(str: string): MessageFilter {
    return MessageFilter.filterFromString(this.filterAsString() + '|' + str);
  }

  get messageSearchText() {
    return this.filter.messageSearchText;
  }

  get detectionId() {
    return this.filter.detectionId;
  }

  get vaccineGroupAge() {
    return this.filter.vaccineGroupAge;
  }

  get vaccineGroup() {
    return this.filter.vaccineGroup;
  }

  get codeType() {
    return this.filter.codeType;
  }

  get codeValue() {
    return this.filter.codeValue;
  }

  clear(key: keyof IMessageFilter) {
    delete this.filter[key];
  }
}

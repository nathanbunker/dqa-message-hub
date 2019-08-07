import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Messages, IMessageFilter, Report, CodesMap, AgeGroup, VaccinationExpectedMap } from '../dashboard/report/model';

@Injectable({
  providedIn: 'root'
})
export class ReportingService {

  constructor(private http: HttpClient) { }

  getMessageList(provider: string, date: string, page: number, itemsPerPage: number, filter?: string): Observable<Messages> {
    return this.http.get<Messages>('api/messages/' + provider + '/date/' + date + '/messages/' + itemsPerPage + '/page/' + page + '/', {
      params: {
        filters: filter || '',
      }
    });
  }

  getVaccinationReportGroupList(provider: string): Observable<string[]> {
    return this.http.get<string[]>('api/codes/vaccineReportGroupList/' + provider);
  }

  getVaccinationsExpected(provider: string, date: string): Observable<VaccinationExpectedMap> {
    return this.http.get<VaccinationExpectedMap>('api/codes/vaccinationsExpected/' + provider + '/' + date + '/' + date);
  }

  getAgeCategoryList(provider: string): Observable<AgeGroup[]> {
    return this.http.get<AgeGroup[]>('api/codes/ageCategoryList/' + provider);
  }

  getVaccination(provider: string, date: string): Observable<VaccinationExpectedMap> {
    return this.http.get<VaccinationExpectedMap>('api/codes/vaccinations/' + provider + '/' + date + '/' + date);
  }

  getReport(provider: string, date: string): Observable<Report> {
    return this.http.get<Report>('api/report/' + provider + '/date/' + date + '/');
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

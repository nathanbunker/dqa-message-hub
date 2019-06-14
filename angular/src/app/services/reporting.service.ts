import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Messages, IMessageFilter, Report } from '../dashboard/report/model';

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

  getReport(provider: string, date: string): Observable<Report> {
    return this.http.get<Report>('api/report/' + provider + '/date/' + date + '/');
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

  clear(key: keyof IMessageFilter) {
    delete this.filter[key];
  }
}

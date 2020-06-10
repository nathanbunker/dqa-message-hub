import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CodeSetService {

  constructor(private http: HttpClient) { }

  getCodeSets(): Observable<ICodeSetItem[]> {
    return this.http.get<ICodeSetItem[]>('api/codes');
  }

  getCodeSet(codeType: string): Observable<ICodeSet> {
    return this.http.get<ICodeSet>('api/codes/' + codeType);
  }
}

export interface ICodeSetItem {
  typeCode: string;
  name: string;
  description: string;
}

export interface ICodeSet {
  label: string;
  type: string;
  code: ICode[];
}

export interface ICode {
  value: string;
  label: string;
  description: string;
  codeStatus: ICodeStatus;
  reference?: any;
  useDate?: any;
  useAge?: any;
  conceptType?: any;
  testAge?: any;
}

export interface ICodeStatus {
  status: string;
  deprecated?: any;
}

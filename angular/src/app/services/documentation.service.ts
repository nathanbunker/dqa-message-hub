import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentationService {

  constructor(private http: HttpClient) { }

  getDocumentation(): Observable<DetectionDocumentation[]> {
    return this.http.get<DetectionDocumentation[]>('api/documentation/table');
  }
}

export interface DetectionDocumentation {
  code: string;
  field: string;
  description: string;
  active: boolean;
  target: string;
  severity: string;
  details?: Detail[];
}

export interface Detail {
  rule: string;
  details?: null | string | string;
}

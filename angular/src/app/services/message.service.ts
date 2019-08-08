import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Messages, MessageDetail } from '../dashboard/report/model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageService {


  constructor(private http: HttpClient) { }

  getMessage(messageId: number): Observable<MessageDetail> {
    return this.http.get<MessageDetail>('api/messages/' + messageId);
  }

}

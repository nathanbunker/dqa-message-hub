import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Messages, MessageDetail, MessageList } from '../model';
import { MessageService } from '../../../services/message.service';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { HighlightType } from '../../../hl7-view/hl7-part';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.css']
})
export class MessagesComponent implements OnInit {

  @Input()
  messageList: Messages;
  @Input()
  page: number;
  @Output()
  pageChange: EventEmitter<number>;

  constructor(private route: ActivatedRoute, private messageService: MessageService, private router: Router) {
    this.pageChange = new EventEmitter<number>();
  }

  changePage(page: number) {
    this.pageChange.emit(page);
  }

  getMessageDetails(message: MessageList): Observable<MessageDetail> {
    if (message.details) {
      return of(message.details);
    } else {
      return this.messageService.getMessage(message.id).pipe(
        tap((details) => {
          message.details = details;
        }),
      );
    }
  }

  toggleMessageDetails(message: MessageList) {
    if (!message.showDetails) {
      if (!message.details) {
        this.messageService.getMessage(message.id).subscribe((details) => {
          message.details = details;
          message.showDetails = true;
        });
      } else {
        message.showDetails = true;
      }
    } else {
      message.showDetails = false;
    }
  }

  toggleMessageView(details: MessageDetail) {
    if (!details.showMessageReceived) {
      if (!details.received) {
        details.received = {
          message: details.messageReceived,
          highlights: details.detections.filter((detection) => !!detection.location).map((detection) => {
            return {
              query: {
                instancePath: detection.location,
              },
              type: detection.severity === 'E' ? HighlightType.ERROR : HighlightType.WARNING,
            };
          })
        };
        details.showMessageReceived = true;
      } else {
        details.showMessageReceived = true;
      }
    } else {
      details.showMessageReceived = false;
    }
  }

  toggleAckView(details: MessageDetail) {
    if (!details.showResponse) {
      if (!details.response) {
        details.response = {
          message: details.messageResponse,
          highlights: [],
        };
        details.showResponse = true;
      } else {
        details.showResponse = true;
      }
    } else {
      details.showResponse = false;
    }
  }

  ngOnInit() {
  }

}

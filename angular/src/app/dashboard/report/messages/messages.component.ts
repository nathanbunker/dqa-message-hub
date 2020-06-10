import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Messages } from '../model';

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

  constructor(private route: ActivatedRoute, private router: Router) {
    this.pageChange = new EventEmitter<number>();
  }

  changePage(page: number) {
    this.pageChange.emit(page);
  }

  ngOnInit() {
  }

}

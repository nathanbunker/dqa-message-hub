import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { MessageFilter } from '../../../services/reporting.service';
import { CodesMap } from '../model';

@Component({
  selector: 'app-codes',
  templateUrl: './codes.component.html',
  styleUrls: ['./codes.component.css']
})
export class CodesComponent implements OnInit {

  codesList = {} as CodesMap;

  @Output()
  filterBy: EventEmitter<MessageFilter>;
  @Input()
  filters: MessageFilter;

  @Input()
  set codes(codes: CodesMap) {
    if (codes) {
      this.codesList = codes;
    }
  }

  constructor() {
    this.filterBy = new EventEmitter<MessageFilter>();
  }

  filter(code: string, typeCode: string) {
    this.filterBy.emit(MessageFilter.filterFromString('codeType::' + typeCode).merge('codeValue::' + code));
  }

  ngOnInit() {
  }

}

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
  filterBy: EventEmitter<string>;
  @Input()
  filters: MessageFilter;

  @Input()
  set codes(codes: CodesMap) {
    if (codes) {
      this.codesList = codes;
      // console.log(this.codesList.map);
    }
  }

  constructor() {
    this.filterBy = new EventEmitter<string>();
  }

  filter(code: string) {
    this.filterBy.emit(code);
  }

  ngOnInit() {
  }

}

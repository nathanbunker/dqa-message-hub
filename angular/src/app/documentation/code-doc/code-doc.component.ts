import { Component, OnInit } from '@angular/core';
import { CodeSetService, ICodeSetItem, ICode } from '../../services/code-set.service';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-code-doc',
  templateUrl: './code-doc.component.html',
  styleUrls: ['./code-doc.component.css']
})
export class CodeDocComponent implements OnInit {

  codeSet: ICodeSetItem;
  codeSets$: Observable<ICodeSetItem[]>;
  codes$: Observable<ICode[]>;
  filter: BehaviorSubject<string>;
  all: number;
  filtered: number;

  constructor(private codeSetService: CodeSetService) {
    this.filter = new BehaviorSubject<string>('');
  }

  changeCodeSet($event) {
    this.codes$ = combineLatest(this.filter.asObservable(), this.codeSetService.getCodeSet($event.typeCode)).pipe(
      map(([filter, data]) => {
        const list = data ? data.code || [] : [];
        this.all = list.length;
        const filteredList = list.sort((a, b) => {
          return a.value.localeCompare(b.value);
        }).filter((elm) => {
          return elm.value.toLowerCase().includes(filter.toLowerCase());
        });
        this.filtered = filteredList.length;
        return filteredList;
      }),
    );
  }

  get filterText(): string {
    return this.filter.getValue();
  }

  set filterText(str: string) {
    this.filter.next(str);
  }

  ngOnInit() {
    this.codeSets$ = this.codeSetService.getCodeSets();
  }

}

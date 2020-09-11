import { Component, OnInit } from '@angular/core';
import {Observable, Subject, merge} from 'rxjs';
import {debounceTime, distinctUntilChanged, map} from 'rxjs/operators';
import { Input, Output, EventEmitter } from '@angular/core';
import { filter } from 'rxjs/internal/operators/filter';
import { NgbTypeahead } from '@ng-bootstrap/ng-bootstrap';
import { ViewChild } from '@angular/core';

@Component({
  selector: 'app-provider-typeahead',
  templateUrl: './provider-typeahead.component.html',
  styleUrls: ['./provider-typeahead.component.css']
})
export class ProviderTypeaheadComponent implements OnInit {

  @Input()
  facilityList: string[];

  @Output()
  typeaheadChoice = new EventEmitter();

  /*
  See this link for documentation on why these crazy next few lines are here.
  https://ng-bootstrap.github.io/#/components/typeahead/examples#focus
  */
  @ViewChild('typeaheadinstance') instance: NgbTypeahead;
  focus$ = new Subject<string>();
  click$ = new Subject<string>();

  public model: any;

  search = (text$: Observable<string>) => {
    console.log(this.instance);
    const debouncedText$ = text$.pipe(debounceTime(200), distinctUntilChanged());
    const clicksWithClosedPopup$ = this.click$.pipe(filter(() => !this.instance.isPopupOpen()));
    const inputFocus$ = this.focus$;

    return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
      map(term => (term === '' ? this.facilityList
        : this.facilityList.filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1)).slice(0, 10))
    );
  }

  // search = (text$: Observable<string>) =>
  //   text$.pipe(
  //     debounceTime(200),
  //     distinctUntilChanged(),
  //     map(term => term.length < 1 ? []
  //       : this.facilityList.filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
  //   )

  constructor() { }

  ngOnInit() {
  }

  selectedItem(event) {
    this.typeaheadChoice.emit(event.item);
  }



}

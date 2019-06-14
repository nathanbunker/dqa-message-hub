import { Component, OnInit, TemplateRef } from '@angular/core';
import { Observable, combineLatest, ReplaySubject, concat, merge, Subject } from 'rxjs';
import { CalendarInfo } from '../calendar/calendar.component';
import { map, take, concatMap, tap, mergeMap, pluck, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { FacilityService } from '../../facility.service';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MessageFilter, ReportingService } from '../../services/reporting.service';
import { IMessageFilter, Messages, Report } from '../report/model';

export enum ProviderDashboardTab {
  MESSAGES = 'messages',
  CODES = 'codes',
  DETECTIONS = 'detections'
}

@Component({
  selector: 'app-provider',
  templateUrl: './provider.component.html',
  styleUrls: ['./provider.component.css']
})
export class ProviderComponent implements OnInit {

  urlParams$: Observable<{
    date: string;
    provider: string;
    filter: MessageFilter;
    tab: ProviderDashboardTab;
    page: number;
  }>;
  filter: ReplaySubject<string>;
  calendar$: Observable<CalendarInfo>;
  messageList$: Observable<Messages>;
  report$: Observable<Report>;
  currentYear: number;
  tabsType = ProviderDashboardTab;
  filterDisplay: {
    detections: {
      [id: string]: string
    }
  };
  slideYear: Subject<number>;
  constructor(
    private route: ActivatedRoute,
    private facilityService: FacilityService,
    private reportService: ReportingService,
    private router: Router,
    private modalService: NgbModal) {
    this.currentYear = moment(new Date()).year();
    this.filter = new ReplaySubject<string>();
    this.filterDisplay = {
      detections: {},
    };
    this.slideYear = new Subject<number>();
  }

  openFilterDialog(content: TemplateRef<any>) {
    this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {
      this.filterBy(result, 'messageSearchText');
    });
  }

  filterBy(value: string, key: keyof IMessageFilter) {
    this.urlParams$.pipe(
      take(1),
      tap((params) => {
        if (value) {
          this.filter.next(params.filter.merge(key + '::' + value).filterAsString());
        }
      }),
    ).subscribe();
  }

  pageChange(page: number) {
    console.log('PAGE');
    this.router.navigate(['.'], {
      relativeTo: this.route,
      queryParamsHandling: 'merge',
      queryParams: {
        page,
      }
    });
  }

  clearFilter(filterKey: keyof IMessageFilter) {
    this.urlParams$.pipe(
      take(1),
      tap((params) => {
        params.filter.clear(filterKey);
        this.filter.next(params.filter.filterAsString());
      }),
    ).subscribe();
  }

  previousYear() {
  }

  nextYear() {
  }

  dateSelected(date: Date) {
    this.urlParams$.pipe(
      take(1),
      tap((params) => {
        this.router.navigate(['../..', moment(date).format('YYYYMMDD'), params.tab], {
          queryParamsHandling: 'preserve',
          relativeTo: this.route,
        });
      })
    ).subscribe();
  }

  switchTab(tabChangeEvent) {
    this.router.navigate(['../', tabChangeEvent.nextId], {
      queryParamsHandling: 'preserve',
      relativeTo: this.route,
    });
    tabChangeEvent.preventDefault();
  }

  ngOnInit() {
    this.urlParams$ = combineLatest(
      this.route.params,
      this.route.queryParams,
    ).pipe(
      map(([params, query]) => {
        return {
          date: params.date as string,
          provider: params.provider as string,
          filter: MessageFilter.filterFromString(query.filters ? query.filters : ''),
          page: query.page ? query.page as number : 1,
          tab: params.tab,
        };
      }),
    );

    this.urlParams$.pipe(
      tap((params) => {
        this.report$ = this.reportService.getReport(params.provider, params.date).pipe(
          tap((report) => {
            for (const detection of report.detectionCounts) {
              this.filterDisplay
                .detections[detection.applicationErrorCode.alternateIdentifier] = detection.applicationErrorCode.alternateText;
            }
          }),
        );

        this.messageList$ = this.reportService.getMessageList(
          params.provider,
          params.date,
          params.page - 1,
          10,
          params.filter.filterAsString(),
        );
      }),
    ).subscribe();



    this.filter.pipe(
      tap((filters: string) => {
        this.router.navigate(['.'], {
          relativeTo: this.route,
          queryParamsHandling: 'merge',
          queryParams: {
            filters,
          }
        });
      })
    ).subscribe();

    this.calendar$ = combineLatest(
      merge(this.slideYear, this.urlParams$.pipe(
        pluck('date'),
        distinctUntilChanged(),
        map((date: string) => {
          return moment(date, 'YYYYMMDD').year();
        })
      )),
      this.urlParams$)
      .pipe(
        switchMap(([year, params]) => {
          console.log('X');
          return this.facilityService.getFacilityHistory(params.provider, year).pipe(take(1));
        })
      );
  }

}

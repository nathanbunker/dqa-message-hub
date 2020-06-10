import { Component, OnInit, TemplateRef } from '@angular/core';
import { Observable, combineLatest, ReplaySubject, merge, Subject } from 'rxjs';
import { CalendarInfo } from '../calendar/calendar.component';
import { map, take, tap, pluck, distinctUntilChanged, switchMap, shareReplay } from 'rxjs/operators';
import { FacilityService } from '../../facility.service';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MessageFilter, ReportingService } from '../../services/reporting.service';
import { IMessageFilter, Messages, Report, VaccinationExpectedMap, AgeGroup, CodesMap, ProviderReport } from '../report/model';
import { VaccinationsData } from '../report/vaccines/vaccines.component';

export enum ProviderDashboardTab {
  MESSAGES = 'messages',
  CODES = 'codes',
  DETECTIONS = 'detections',
  VACCINES = 'vaccines',
  REPORT = 'report',
}

export enum DateElectionDecision {
  PROCEED,
  RESET,
  NONE,
}

export enum DateType {
  START = 0,
  END = 1,
}

export interface DateRange {
  start?: moment.Moment;
  end?: moment.Moment;
}

@Component({
  selector: 'app-provider',
  templateUrl: './provider.component.html',
  styleUrls: ['./provider.component.css']
})
export class ProviderComponent implements OnInit {

  urlParams$: Observable<{
    dateStart: string;
    dateEnd: string;
    provider: string;
    filter: MessageFilter;
    tab: ProviderDashboardTab;
    page: number;
  }>;
  filter: ReplaySubject<string>;

  dateSequence: DateType;
  calendar$: Observable<CalendarInfo>;
  messageList$: Observable<Messages>;
  codesReceivedList$: Observable<CodesMap>;
  report$: Observable<Report>;
  vaccinationExpected$: Observable<VaccinationExpectedMap>;
  vaccinations$: Observable<VaccinationExpectedMap>;
  vaccinationTabData$: Observable<VaccinationsData>;
  providerReport$: Observable<ProviderReport>;
  vaccinationReportGroupList$: Observable<string[]>;
  ageGroups$: Observable<AgeGroup[]>;
  calendarYear$: Observable<number>;

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
    this.dateSequence = 0;
  }

  forwardDateSequence() {
    this.dateSequence = (this.dateSequence + 1) % 2;
  }

  openFilterDialog(content: TemplateRef<any>) {
    this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {
      this.filterBy(result, 'messageSearchText', true);
    });
  }

  filterBy(value: string, key: keyof IMessageFilter, keep?: boolean) {
    this.urlParams$.pipe(
      take(1),
      tap((params) => {
        if (value) {
          if (keep) {
            this.filter.next(params.filter.merge(key + '::' + value).filterAsString());
          } else {
            this.filter.next(key + '::' + value);
          }
        }
      }),
    ).subscribe();
  }

  filterByAll(filter: MessageFilter) {
    this.urlParams$.pipe(
      take(1),
      tap((params) => {
        if (filter) {
          this.filter.next(filter.filterAsString());
        }
      }),
    ).subscribe();
  }

  pageChange(page: number) {
    this.router.navigate(['.'], {
      relativeTo: this.route,
      queryParamsHandling: 'merge',
      queryParams: {
        page,
      }
    });
  }

  clearFilter(...filterKey: Array<keyof IMessageFilter>) {
    this.urlParams$.pipe(
      take(1),
      tap((params) => {
        filterKey.forEach((key) => {
          params.filter.clear(key);
        });
        this.filter.next(params.filter.filterAsString());
      }),
    ).subscribe();
  }

  previousYear() {
    this.calendarYear$.pipe(
      take(1),
      tap((year) => {
        if (year > 0) {
          this.slideYear.next(year - 1);
        }
      })
    ).subscribe();
  }

  nextYear() {
    this.calendarYear$.pipe(
      take(1),
      tap((year) => {
        if (year < this.currentYear) {
          this.slideYear.next(year + 1);
        }
      })
    ).subscribe();
  }

  dateChange(str: string, type: DateType) {
    const date = moment(str, 'YYYYMMDD');
    this.electDate(date, type).pipe(
      take(1),
      tap((decision) => {
        if (decision === DateElectionDecision.PROCEED) {
          this.goToDate(type === DateType.START ? {
            start: date,
          } : {
              end: date,
            });
          this.forwardDateSequence();
        } else if (decision === DateElectionDecision.RESET) {
          this.goToDate({
            start: date,
            end: date,
          });
        }
      }),
    ).subscribe();
  }

  goToDate(range: DateRange) {
    this.urlParams$.pipe(
      take(1),
      tap((params) => {
        const start = range.start ? range.start.format('YYYYMMDD') : params.dateStart;
        const end = range.end ? range.end.format('YYYYMMDD') : params.dateEnd;

        this.router.navigate([
          '../../..',
          start,
          end,
          params.tab
        ], {
          queryParamsHandling: 'preserve',
          relativeTo: this.route,
        });
      })
    ).subscribe();
  }

  dateSelected(date: Date) {
    const selectedDate = moment(date);
    this.electDate(selectedDate, this.dateSequence).pipe(
      take(1),
      tap((decision) => {
        if (this.dateSequence === DateType.START) {
          if (decision === DateElectionDecision.PROCEED || decision === DateElectionDecision.RESET) {
            this.goToDate({
              start: selectedDate,
              end: selectedDate,
            });
            this.forwardDateSequence();
          }
        } else if (this.dateSequence === DateType.END) {
          if (decision === DateElectionDecision.PROCEED) {
            this.goToDate({
              end: selectedDate,
            });
            this.forwardDateSequence();
          } else if (decision === DateElectionDecision.RESET) {
            this.goToDate({
              start: selectedDate,
              end: selectedDate,
            });
          }
        }
      })
    ).subscribe();
  }

  electDate(date: moment.Moment, type: DateType): Observable<DateElectionDecision> {
    return this.urlParams$.pipe(
      take(1),
      map((params) => {
        const urlEndDate = moment(params.dateEnd, 'YYYYMMDD');
        const urlStartDate = moment(params.dateStart, 'YYYYMMDD');
        if (type === DateType.START) {
          if (urlStartDate.isSame(date)) {
            return DateElectionDecision.NONE;
          } else if (urlEndDate.isAfter(date)) {
            return DateElectionDecision.PROCEED;
          } else {
            return DateElectionDecision.RESET;
          }
        } else if (type === DateType.END) {
          if (urlEndDate.isSame(date)) {
            return DateElectionDecision.NONE;
          } else if (urlStartDate.isBefore(date)) {
            return DateElectionDecision.PROCEED;
          } else {
            return DateElectionDecision.RESET;
          }
        }
        return DateElectionDecision.NONE;
      })
    );
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
          dateStart: params.dateStart as string,
          dateEnd: params.dateEnd as string,
          provider: params.provider as string,
          filter: MessageFilter.filterFromString(query.filters ? query.filters : ''),
          page: query.page ? query.page as number : 1,
          tab: params.tab,
        };
      }),
    );

    this.urlParams$.pipe(
      tap((params) => {
        this.report$ = this.reportService.getReport(params.provider, params.dateStart, params.dateEnd).pipe(
          tap((report) => {
            for (const detection of report.detectionCounts) {
              this.filterDisplay
                .detections[detection.applicationErrorCode.alternateIdentifier] = detection.applicationErrorCode.alternateText;
            }
          }),
        );

        this.ageGroups$ = this.reportService.getAgeCategoryList(params.provider);
        this.vaccinationExpected$ = this.reportService.getVaccinationsExpected(params.provider, params.dateStart, params.dateEnd);
        this.vaccinations$ = this.reportService.getVaccination(params.provider, params.dateStart, params.dateEnd);
        this.vaccinationReportGroupList$ = this.reportService.getVaccinationReportGroupList(params.provider);
        this.providerReport$ = this.reportService.getProviderReport(params.provider, params.dateStart, params.dateEnd);
        this.vaccinationTabData$ = combineLatest(
          this.ageGroups$,
          this.vaccinationExpected$,
          this.vaccinations$,
          this.vaccinationReportGroupList$).pipe(
            map(([ageGroups, expected, provided, vaccineGroupList]) => {
              return {
                ageGroups,
                expected,
                provided,
                vaccineGroupList,
              };
            }),
          );

        this.messageList$ = this.reportService.getMessageList(
          params.provider,
          params.dateStart,
          params.dateEnd,
          params.page - 1,
          10,
          params.filter.filterAsString(),
        );

        this.codesReceivedList$ = this.reportService.getCodesReceivedList(
          params.provider,
          params.dateStart,
          params.dateEnd,
          params.filter.filterAsString(),
        );
      }),
    ).subscribe();



    this.filter.pipe(
      tap((filters: string) => {
        this.router.navigate(['../messages'], {
          relativeTo: this.route,
          queryParamsHandling: 'merge',
          queryParams: {
            filters,
          }
        });
      })
    ).subscribe();


    const urlYear = this.urlParams$.pipe(
      pluck('dateStart'),
      distinctUntilChanged(),
      map((date: string) => {
        return moment(date, 'YYYYMMDD').year();
      })
    );

    this.calendarYear$ = merge(this.slideYear.asObservable(), urlYear).pipe(
      shareReplay(1),
    );

    this.calendar$ = combineLatest(this.calendarYear$, this.urlParams$).pipe(
      switchMap(([year, params]) => {
        return this.facilityService.getFacilityHistory(params.provider, year).pipe(take(1));
      })
    );
  }

}

import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { AgeGroup, VaccinationExpectedMap } from '../model';
import { MessageFilter } from '../../../services/reporting.service';

export interface VaccinationsData {
  ageGroups: AgeGroup[];
  expected: VaccinationExpectedMap;
  provided: VaccinationExpectedMap;
  vaccineGroupList: string[];
}

@Component({
  selector: 'app-vaccines',
  templateUrl: './vaccines.component.html',
  styleUrls: ['./vaccines.component.css']
})
export class VaccinesComponent implements OnInit {

  _providedTable: {
    ageGroup: string,
    vaccines: {
      vaccine: string,
      count: number,
      status: string,
    }[]
  }[];
  _expectedTable: {
    vaccine: string,
    groups: {
      [ageGroup: string]: {
        count: string,
        ageGroup: string,
        status: string,
        class: string,
      }[],
    },
  }[];

  ageGroupOrder: {
    [label: string]: number,
  };
  vaccineGroupOrder: {
    [label: string]: number,
  };

  orderedAgeGroups: string[] = [];

  @Input()
  filters: MessageFilter;
  @Output()
  filterBy: EventEmitter<MessageFilter>;

  @Input()
  set data(value: VaccinationsData) {
    const sortAge = (a: string, b: string): number => {
      return this.ageGroupOrder[a] - this.ageGroupOrder[b];
    };

    const sortVaccine = (a: string, b: string): number => {
      return this.vaccineGroupOrder[a] - this.vaccineGroupOrder[b];
    };

    if (value) {
      this.ageGroupOrder = {};
      this.vaccineGroupOrder = {};

      value.ageGroups.forEach(
        (group) => {
          this.ageGroupOrder[group.label] = group.order;
        }
      );

      value.vaccineGroupList.forEach(
        (group, index) => {
          this.vaccineGroupOrder[group] = index;
        }
      );

      this.orderedAgeGroups = value.ageGroups.map((group) => group.label).sort(sortAge);


      this._providedTable = [];
      for (const group of Object.keys(value.provided.map).sort(sortAge)) {
        this._providedTable.push(
          {
            ageGroup: group,
            vaccines: Object.keys(value.provided.map[group]).map(
              (vaccine) => {
                return {
                  vaccine,
                  count: value.provided.map[group][vaccine].count,
                  status: value.provided.map[group][vaccine].status,
                };
              }
            )
          }
        );
      }

      this._expectedTable = [];
      for (const group of Object.keys(value.expected.map).sort(sortVaccine)) {
        const groups = {};
        Object.keys(value.expected.map[group]).forEach(
          (age) => {
            groups[age] = {
              count: value.expected.map[group][age].count,
              ageGroup: age,
              status: value.expected.map[group][age].status,
              class: value.expected.map[group][age].reportStyleClass,
            };
          }
        );
        this._expectedTable.push(
          {
            vaccine: group,
            groups,
          }
        );
      }
    }
  }

  filter(vaccineGroup: string, ageGroup: string) {
    this.filterBy.emit(MessageFilter.filterFromString('vaccineGroupAge::' + ageGroup).merge('vaccineGroup::' + vaccineGroup));
  }

  keyOf(obj: any): string[] {
    return Object.keys(obj);
  }

  constructor() {
    this.filterBy = new EventEmitter<MessageFilter>();
  }

  ngOnInit() {
  }

}

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Hl7ViewComponent } from './hl7-view.component';

describe('Hl7ViewComponent', () => {
  let component: Hl7ViewComponent;
  let fixture: ComponentFixture<Hl7ViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ Hl7ViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Hl7ViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

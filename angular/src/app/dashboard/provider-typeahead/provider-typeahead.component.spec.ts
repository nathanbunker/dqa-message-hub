import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProviderTypeaheadComponent } from './provider-typeahead.component';

describe('ProviderTypeaheadComponent', () => {
  let component: ProviderTypeaheadComponent;
  let fixture: ComponentFixture<ProviderTypeaheadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProviderTypeaheadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProviderTypeaheadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

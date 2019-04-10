import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DetectionDocComponent } from './detection-doc.component';

describe('DetectionDocComponent', () => {
  let component: DetectionDocComponent;
  let fixture: ComponentFixture<DetectionDocComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DetectionDocComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetectionDocComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

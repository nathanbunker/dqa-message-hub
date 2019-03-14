import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CodeDocComponent } from './code-doc.component';

describe('CodeDocComponent', () => {
  let component: CodeDocComponent;
  let fixture: ComponentFixture<CodeDocComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CodeDocComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CodeDocComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

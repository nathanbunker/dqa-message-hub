import { TestBed } from '@angular/core/testing';

import { CodeSetService } from './code-set.service';

describe('CodeSetService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CodeSetService = TestBed.get(CodeSetService);
    expect(service).toBeTruthy();
  });
});

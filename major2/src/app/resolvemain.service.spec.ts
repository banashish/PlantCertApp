import { TestBed } from '@angular/core/testing';

import { ResolvemainService } from './resolvemain.service';

describe('ResolvemainService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ResolvemainService = TestBed.get(ResolvemainService);
    expect(service).toBeTruthy();
  });
});

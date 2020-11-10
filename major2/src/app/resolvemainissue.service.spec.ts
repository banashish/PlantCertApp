import { TestBed } from '@angular/core/testing';

import { ResolvemainissueService } from './resolvemainissue.service';

describe('ResolvemainissueService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ResolvemainissueService = TestBed.get(ResolvemainissueService);
    expect(service).toBeTruthy();
  });
});

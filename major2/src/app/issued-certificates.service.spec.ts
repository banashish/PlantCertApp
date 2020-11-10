import { TestBed } from '@angular/core/testing';

import { IssuedCertificatesService } from './issued-certificates.service';

describe('IssuedCertificatesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: IssuedCertificatesService = TestBed.get(IssuedCertificatesService);
    expect(service).toBeTruthy();
  });
});

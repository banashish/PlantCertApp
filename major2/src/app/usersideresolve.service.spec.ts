import { TestBed } from '@angular/core/testing';

import { UsersideresolveService } from './usersideresolve.service';

describe('UsersideresolveService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UsersideresolveService = TestBed.get(UsersideresolveService);
    expect(service).toBeTruthy();
  });
});

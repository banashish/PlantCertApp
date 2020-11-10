import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserwindowComponent } from './userwindow.component';

describe('UserwindowComponent', () => {
  let component: UserwindowComponent;
  let fixture: ComponentFixture<UserwindowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserwindowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserwindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

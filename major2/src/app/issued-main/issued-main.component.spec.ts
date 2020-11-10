import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IssuedMainComponent } from './issued-main.component';

describe('IssuedMainComponent', () => {
  let component: IssuedMainComponent;
  let fixture: ComponentFixture<IssuedMainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IssuedMainComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IssuedMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

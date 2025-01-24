import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageCustomerComponent } from './message-customer.component';

describe('MessageCustomerComponent', () => {
  let component: MessageCustomerComponent;
  let fixture: ComponentFixture<MessageCustomerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MessageCustomerComponent]
    });
    fixture = TestBed.createComponent(MessageCustomerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

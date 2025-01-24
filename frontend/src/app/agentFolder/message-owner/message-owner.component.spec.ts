import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageOwnerComponent } from './message-owner.component';

describe('MessageOwnerComponent', () => {
  let component: MessageOwnerComponent;
  let fixture: ComponentFixture<MessageOwnerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MessageOwnerComponent]
    });
    fixture = TestBed.createComponent(MessageOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

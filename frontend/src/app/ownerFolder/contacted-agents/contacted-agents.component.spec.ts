import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContactedAgentsComponent } from './contacted-agents.component';

describe('ContactedAgentsComponent', () => {
  let component: ContactedAgentsComponent;
  let fixture: ComponentFixture<ContactedAgentsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContactedAgentsComponent]
    });
    fixture = TestBed.createComponent(ContactedAgentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

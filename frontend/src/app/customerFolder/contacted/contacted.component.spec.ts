import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContactedComponent } from './contacted.component';

describe('ContactedComponent', () => {
  let component: ContactedComponent;
  let fixture: ComponentFixture<ContactedComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContactedComponent]
    });
    fixture = TestBed.createComponent(ContactedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

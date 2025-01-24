import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowPropertiesComponent } from './show-properties.component';

describe('ShowPropertiesComponent', () => {
  let component: ShowPropertiesComponent;
  let fixture: ComponentFixture<ShowPropertiesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShowPropertiesComponent]
    });
    fixture = TestBed.createComponent(ShowPropertiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

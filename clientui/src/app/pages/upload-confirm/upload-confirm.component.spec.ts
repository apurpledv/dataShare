import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadConfirmComponent } from './upload-confirm.component';

describe('UploadConfirmComponent', () => {
  let component: UploadConfirmComponent;
  let fixture: ComponentFixture<UploadConfirmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadConfirmComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadConfirmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

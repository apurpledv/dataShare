import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { throwError } from "rxjs";
import { FileService } from "@app/core/services/file.service";
import { MockFileService } from "../mock/MockFileService";
import { UploadComponent } from "@app/pages/upload/upload.component";

describe('Upload IT Tests', () => {
  let component: UploadComponent;
  let fixture: ComponentFixture<UploadComponent>;

  let fileService: MockFileService;
  let formBuilder: FormBuilder;

  let file: File;

  beforeEach(async () => {
    TestBed.configureTestingModule({
        providers: [
            { provide: FileService, useClass: MockFileService },
            { provide: ActivatedRoute, useValue: {} },
            { provide: Router }
        ],
    });

    fixture = TestBed.createComponent(UploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    fileService = TestBed.inject(FileService) as MockFileService;
    formBuilder = TestBed.inject(FormBuilder);

    file = new File(['content'], 'test.txt', { type: 'text/plain' });
  });

  it('should try to upload a valid file', () => {
    const formThingy: FormGroup = formBuilder.group({
      password: '', 
      expirationDate: '2'
    });
    component.uploadForm = formThingy;
    component.selectedFile = file;

    const spyUpload = jest.spyOn(fileService, 'upload');

    component.onSubmit();
    fixture.detectChanges();
    expect(spyUpload).toHaveBeenCalled();
  });

  it('should try to upload an invalid file', () => {
    const formThingy: FormGroup = formBuilder.group({
      password: '', 
      expirationDate: ['', Validators.required]
    });
    component.uploadForm = formThingy;
    const spyUpload = jest.spyOn(fileService, 'upload');

    component.onSubmit();
    fixture.detectChanges();
    expect(spyUpload).not.toHaveBeenCalled();
  });

  it('should try to upload a valid file but an error occurs', () => {
    const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();
    const consoleAlertMock = jest.spyOn(window, 'alert').mockImplementation();
    const formThingy: FormGroup = formBuilder.group({
      password: '', 
      expirationDate: '2'
    });
    component.uploadForm = formThingy;
    component.selectedFile = file;

    const spyUpload = jest.spyOn(fileService, 'upload');
    spyUpload.mockImplementation(() => throwError(() => new Error('error')));

    component.onSubmit();
    fixture.detectChanges();
    expect(spyUpload).toHaveBeenCalled();
    expect(consoleErrorMock).toHaveBeenCalled();
    expect(consoleAlertMock).toHaveBeenCalled();
  });

  /*it('should try to delete a file and succeed', () => {
    const spyDelete = jest.spyOn(fileService, 'delete');
    
    component.onDelete(1);

    fixture.detectChanges();
    expect(spyDelete).toHaveBeenCalled();
  });

  it('should try to delete a file and fail', () => {
    const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();
    const spyDelete = jest.spyOn(fileService, 'delete');
    spyDelete.mockImplementation(() => throwError(() => new Error('error')));

    component.onDelete(1);

    fixture.detectChanges();
    expect(consoleErrorMock).toHaveBeenCalled();

    consoleErrorMock.mockRestore();
  });*/
});
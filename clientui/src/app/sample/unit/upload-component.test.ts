import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder, FormGroup } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { MockFileService } from "../mock/MockFileService";
import { FileService } from "@app/core/services/file.service";
import { UploadComponent } from "@app/pages/upload/upload.component";

describe('UploadComponent Unit Tests', () => {
  let component: UploadComponent;
  let fixture: ComponentFixture<UploadComponent>;

  let fileService: MockFileService;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    TestBed.configureTestingModule({
        providers: [
            { provide: FileService, useClass: MockFileService },
            { provide: ActivatedRoute, useValue: {} }
        ],
    });

    fixture = TestBed.createComponent(UploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    fileService = TestBed.inject(FileService) as MockFileService;
    formBuilder = TestBed.inject(FormBuilder);

    jest.spyOn(window, 'alert').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should reset the form', () => {
    const formThingy: FormGroup = formBuilder.group({
      file: new Blob(), 
      userId: 1, 
      expirationDate: 2
    });
    component.uploadForm = formThingy;
    
    component.onReset();
    
    expect(component.uploadForm.controls['file'].value).toBeNull();
    expect(component.uploadForm.controls['userId'].value).toBeNull();
    expect(component.uploadForm.controls['expirationDate'].value).toBeNull();
  });

  function createEventWithFiles(files: File[] | null): Event {
    const input = document.createElement('input');

    Object.defineProperty(input, 'files', {
      value: files,
      writable: false,
    });

    return { target: input } as unknown as Event;
  }

  it('should set selectedFile when one valid file is selected', () => {
    const file = new File(['content'], 'test.txt', { type: 'text/plain' });
    const event = createEventWithFiles([file]);

    component.onFileSelected(event);

    expect(component.selectedFile).toBe(file);
    expect(window.alert).not.toHaveBeenCalled();
  });

  it('should return early if no files are selected', () => {
    const event = createEventWithFiles([]);

    component.onFileSelected(event);

    expect(component.selectedFile).toBeUndefined();
    expect(window.alert).not.toHaveBeenCalled();
  });

  it('should alert if the file is too big', () => {
    const file1 = new File(['a'], 'a.txt');
    const file2 = new File(['b'], 'b.txt');
    const event = createEventWithFiles([file1, file2]);

    component.maxFileSize = 1;

    component.onFileSelected(event);

    expect(window.alert).toHaveBeenCalledWith(
      "La taille du fichier ne peut pas excéder 1 Go."
    );
    expect(component.selectedFile).toBeUndefined();
  });
});

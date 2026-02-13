import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { AuthToken } from "@app/core/models/AuthToken";
import { UserService } from "@app/core/services/user.service";
import { LoginComponent } from "@app/pages/login/login.component";
import { MockUserService } from "../mock/MockUserService";
import { of, throwError } from "rxjs";
import { DashboardComponent } from "@app/pages/dashboard/dashboard.component";
import { FileService } from "@app/core/services/file.service";
import { MockFileService } from "../mock/MockFileService";
import { DSFile } from "@app/core/models/DSFile";
import { UploadComponent } from "@app/pages/upload/upload.component";
import { DownloadComponent } from "@app/pages/download/download.component";

describe('Download IT Tests', () => {
  let component: DownloadComponent;
  let fixture: ComponentFixture<DownloadComponent>;

  let fileService: MockFileService;
  let formBuilder: FormBuilder;

  let file: File;

  beforeEach(async () => {
    TestBed.configureTestingModule({
        providers: [
            { provide: FileService, useClass: MockFileService },
            { provide: ActivatedRoute, useValue: {params: of({id: 1})} },
            { provide: Router }
        ],
    });

    fixture = TestBed.createComponent(DownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    fileService = TestBed.inject(FileService) as MockFileService;
    formBuilder = TestBed.inject(FormBuilder);
  });

  /*it('should download a file', () => {
    const blob = new Blob(['test content'], { type: 'text/plain' });
    const spyDownload = jest.spyOn(fileService, 'download').mockReturnValue(of(blob));
    const link = document.createElement('a');
    const spyClick = jest.spyOn(link, 'click');

    jest.spyOn(document, 'createElement').mockReturnValue(link);
    component.onDownload('file/path.txt', 'path.txt');

    expect(spyDownload).toHaveBeenCalled();
    expect(spyClick).toHaveBeenCalled();
    expect(link.download).toBe('path.txt');
    expect(link.href).toBe('blob:url');
  });*/

  it('should download a file', () => {
    const blob = new Blob(['test content'], { type: 'text/plain' });

    const spyDownload = jest.spyOn(fileService, 'download').mockReturnValue(of(blob));

    // Mock createObjectURL
    Object.defineProperty(global.URL, 'createObjectURL', {
      writable: true,
      value: jest.fn(() => 'blob:url'),
    });

    const clickMock = jest.fn();

    jest.spyOn(document, 'createElement').mockReturnValue({
      href: '',
      download: '',
      click: clickMock,
    } as any);

    component.onDownload('file/path.txt', 'path.txt');

    expect(spyDownload).toHaveBeenCalled();
    expect(clickMock).toHaveBeenCalled();
  });
});
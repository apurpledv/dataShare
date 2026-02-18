import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { of, throwError } from "rxjs";
import { DashboardComponent } from "@app/pages/dashboard/dashboard.component";
import { FileService } from "@app/core/services/file.service";
import { MockFileService } from "../mock/MockFileService";
import { DSFile } from "@app/core/models/DSFile";

describe('Dashboard IT Tests', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  let fileService: MockFileService;
  let router: Router;
  let formBuilder: FormBuilder;

  const original = window.location;

  const reloadFn = () => {
    window.location.reload();
  };

  beforeEach(async () => {
    TestBed.configureTestingModule({
        providers: [
            { provide: FileService, useClass: MockFileService },
            { provide: ActivatedRoute, useValue: {} },
            { provide: Router}
        ],
    });

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    fileService = TestBed.inject(FileService) as MockFileService;
    router = TestBed.inject(Router);
    formBuilder = TestBed.inject(FormBuilder);

    Object.defineProperty(window, 'location', {
      configurable: true,
      value: { reload: jest.fn() },
    });
  });

  afterAll(() => {
    Object.defineProperty(window, 'location', { configurable: true, value: original });
  });

  it('should check that the filesList is initialized', () => {
      const response: DSFile[] = [];
      jest.spyOn(fileService, 'getFiles').mockReturnValue(of(response));
  
      component.ngOnInit();
  
      fixture.detectChanges();
      expect(component.filesList).toEqual(response);
  });

  it('should fetch a file data and redirect', () => {
      const mockFile: DSFile = {
        id: 1,
        path: 'path/to/name.png',
        ownerId: 1,
        name: 'name.png',
        uploadDate: new Date(),
        expirationDate: new Date(),
        type: 'png',
        size: 100,
        expirationLabel: '',
        sizeLabel: '',
      }
      const mockList: DSFile[] = [mockFile];
      jest.spyOn(fileService, 'getFiles').mockReturnValue(of(mockList));
      const spyNavigate = jest.spyOn(router, 'navigateByUrl');
  
      component.ngOnInit();
      fixture.detectChanges();

      component.onViewFile(1);
      fixture.detectChanges();
      expect(spyNavigate).toHaveBeenCalled();
  });

  it('should try to delete a file and succeed', () => {
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
  });
});
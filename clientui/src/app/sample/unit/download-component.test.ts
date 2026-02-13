import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { MockFileService } from "../mock/MockFileService";
import { FileService } from "@app/core/services/file.service";
import { DownloadComponent } from "@app/pages/download/download.component";
import { of } from "rxjs";

describe('DownloadComponent Unit Tests', () => {
  let component: DownloadComponent;
  let fixture: ComponentFixture<DownloadComponent>;

  let fileService: MockFileService;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    TestBed.configureTestingModule({
        providers: [
            { provide: FileService, useClass: MockFileService },
            { provide: ActivatedRoute, useValue: {params: of({id: 1})} }
        ],
    });

    fixture = TestBed.createComponent(DownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    fileService = TestBed.inject(FileService) as MockFileService;
    formBuilder = TestBed.inject(FormBuilder);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should give three different file size labels: "10.00 o", "1.000 Ko" and "1.000 Mo"', () => {
    expect(component.getSizeLabel(10)).toBe('10.00 o');
    expect(component.getSizeLabel(1024)).toBe('1.000 Ko');
    expect(component.getSizeLabel(1024000)).toBe('1.000 Mo');
  })
});

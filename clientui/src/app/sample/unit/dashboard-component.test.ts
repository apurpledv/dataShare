import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { MockFileService } from "../mock/MockFileService";
import { DashboardComponent } from "@app/pages/dashboard/dashboard.component";
import { FileService } from "@app/core/services/file.service";

describe('DashboardComponent Unit Tests', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  let fileService: MockFileService;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    TestBed.configureTestingModule({
        providers: [
            { provide: FileService, useClass: MockFileService },
            { provide: ActivatedRoute, useValue: {} }
        ],
    });

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    fileService = TestBed.inject(FileService) as MockFileService;
    formBuilder = TestBed.inject(FormBuilder);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

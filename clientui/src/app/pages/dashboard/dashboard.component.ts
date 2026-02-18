import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { DSFile } from '../../core/models/DSFile';
import { FileService } from '../../core/services/file.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from "@angular/router";
import { TimeService } from '../../core/services/time.service';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  constructor(private router: Router) { }
  
  private destroyRef = inject(DestroyRef);
  private fileService = inject(FileService);
  private timeService = inject(TimeService);
  filesList!: DSFile[];

  userId!: Number;

  public ngOnInit(): void {
    this.userId = parseInt(sessionStorage.getItem('user_id') || "-1");

    this.fileService.getFiles(this.userId)
      .subscribe((filesList: DSFile[]) => {
        this.filesList = filesList

        this.filesList.forEach(file => {
          file.expirationLabel = "Expire dans " + this.timeService.getDaysBeforeExpiring(file.expirationDate) + " jours.";
        });
      });
  }

  public onViewFile(fileId: number): void {
    this.router.navigateByUrl('download/' + fileId);
  }

  public onDelete(fileId: Number): void {
    this.fileService.delete(fileId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
          next: () => {},
          error: (err: any) => {
            console.error(err);
          },
          complete: () => {
            location.reload();
          }
        });
  }
}

import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { DSFile } from '../../core/models/DSFile';
import { FileService } from '../../core/services/file.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private fileService = inject(FileService);
  filesList!: DSFile[];

  userId: Number = 1;

  public ngOnInit(): void {

    this.fileService.getFiles(this.userId)
      .subscribe((filesList: DSFile[]) => {
        this.filesList = filesList

        this.filesList.forEach(file => {
          file.expirationLabel = "Expire dans " + this.getDaysBeforeExpiring(file.expirationDate) + " jours.";
        });
        console.log(this.filesList);
      });
  }

  private getDaysBeforeExpiring(dateSent: Date): Number {
      let currentDate = new Date();
      dateSent = new Date(dateSent);

      return Math.floor((
        Date.UTC(dateSent.getFullYear(), dateSent.getMonth(), dateSent.getDate())
        - Date.UTC(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate())
        ) / (1000 * 60 * 60 * 24)
      );
  }

  public onDownload(fileName: String, filePath: String): void {
    console.log("lesgo");
    this.fileService.download(this.userId, filePath).subscribe((data) => {

      //this.blob = new Blob([data], {type: 'application/pdf'});

      var downloadURL = window.URL.createObjectURL(data);
      var link = document.createElement('a');
      link.href = downloadURL;
      link.download = fileName.toString();
      link.click();
    })
  }

  public onDelete(fileId: Number): void {
    console.log("deleting");
    this.fileService.delete(fileId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        alert("file deleted");
        location.reload();
      },
    );
  }
}

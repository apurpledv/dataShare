import { Component, inject, OnInit } from '@angular/core';
import { DSFile } from '../../core/models/DSFile';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FileService } from '../../core/services/file.service';
import { TimeService } from '../../core/services/time.service';

@Component({
  selector: 'app-download',
  imports: [RouterLink],
  templateUrl: './download.component.html',
  styleUrl: './download.component.scss'
})
export class DownloadComponent implements OnInit {
  private fileService = inject(FileService);
  private timeService = inject(TimeService);
  private route = inject(ActivatedRoute);
  private userId!: number;
  private fileId!: number;
  uploadDateLabel!: string;

  file!: DSFile;

  public ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.userId = parseInt(sessionStorage.getItem('user_id')|| '-1');
      this.fileId = params['fileId'];

      this.fileService.getFile(this.fileId)
        .subscribe((dsFile: DSFile) => {
          this.file = dsFile;

          let daysLeft = this.timeService.getDaysBeforeExpiring(this.file.expirationDate);
          let expirationLabel;
          switch (daysLeft) {
            case 0: expirationLabel = 'Ce fichier expirera dans demain.'; break;
            case 1: expirationLabel = 'Ce fichier expirera dans ' + daysLeft + ' jour.'; break;
            default: expirationLabel = 'Ce fichier expirera dans ' + daysLeft + ' jours.';
          }
          this.file.expirationLabel = expirationLabel;
          this.file.sizeLabel = this.getSizeLabel(this.file.size);

          const options: Intl.DateTimeFormatOptions = {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
          };
          this.uploadDateLabel = this.file.uploadDate.toLocaleString(undefined, options);
        });
    });
  }

  public getSizeLabel(fileSize: number): string {
    let fileLabel = '';

    if (fileSize < 1000) {
      fileLabel = fileSize.toPrecision(4).toString() + ' o'
    } else if (fileSize >= 1024 && fileSize < 1024000) {
      fileLabel = (fileSize / 1024).toPrecision(4).toString() + ' Ko'
    } else if (fileSize >= 1024000 && fileSize < 1024000000) {
      fileLabel = (fileSize / 1024000).toPrecision(4).toString() + ' Mo'
    }

    return fileLabel;
  }

  public onDownload(filePath: String, fileName: String): void {
    this.fileService.download(this.userId, filePath).subscribe((data) => {
      var downloadURL = window.URL.createObjectURL(data);
      var link = document.createElement('a');
      link.href = downloadURL;
      link.download = fileName.toString();
      link.click();
    })
  }
}

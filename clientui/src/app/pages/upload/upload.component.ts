import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { FileService } from '../../core/services/file.service';
import { CommonModule } from '@angular/common';
import { UploadFile } from '../../core/models/UploadFile';

@Component({
  selector: 'app-upload',
  imports: [RouterLink, CommonModule, ReactiveFormsModule],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.scss'
})
export class UploadComponent implements OnInit {
  constructor(private router: Router) { }

  private fileService = inject(FileService);
  private formBuilder = inject(FormBuilder);
  private destroyRef = inject(DestroyRef);

  maxFileSize = 1073741824; // 1 Go

  uploadForm!: FormGroup;
  selectedFile!: File;
  submitted: boolean = false;

  ngOnInit() {
    this.uploadForm = this.formBuilder.group({
        password: [''],
        expirationDate: ['7', Validators.required]
    });
  }

  get form() {
    return this.uploadForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.uploadForm.invalid)
      return;

    const uploadFile: UploadFile = {
      file: this.selectedFile,
      userId: parseInt(sessionStorage.getItem("user_id") || "-1"),
      expirationDate: parseInt(this.uploadForm.get('expirationDate')?.value || '7'),
      password: this.uploadForm.get('password')?.value
    };

    this.fileService.upload(uploadFile)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
          next: () => {
            console.log('Uploading file... ');
          },
          error: (err: any) => {
            alert("Une erreur est survenue lors de l'upload du fichier. Veuillez réessayer ultérieurement.");
            console.error(err);
          },
          complete: () => {
            this.router.navigateByUrl('dashboard');
          }
        }
      );
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length <= 0)
      return;

    if (input.files.length > this.maxFileSize) {
      alert("La taille du fichier ne peut pas excéder 1 Go.")
      return;
    }
    this.selectedFile = input.files[0];
  }

  onReset(): void {
    this.submitted = false;
    this.uploadForm.reset();
  }
}

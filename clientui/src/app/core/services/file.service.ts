import { HttpClient, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DSFile } from '../models/DSFile';
import { Observable } from 'rxjs';
import { UploadFile } from '../models/UploadFile';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  constructor(private httpClient: HttpClient) { }

  public getFile(fileId: Number): Observable<DSFile> {
    return this.httpClient.get<DSFile>('/api/file/' + fileId);
  }

  public getFiles(userId: Number): Observable<DSFile[]> {
    return this.httpClient.get<DSFile[]>('/api/file/list/' + userId);
  }

  public download(userId: Number, filePath: String): Observable<Blob> {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };

    return this.httpClient.get<Blob>('/api/file/download/' + filePath, httpOptions);
  }

  public upload(uploadFile: UploadFile): Observable<Object> {
    const formData: FormData = new FormData();
    formData.append('file', uploadFile.file);
    formData.append('userId', uploadFile.userId.toString());
    formData.append('expirationDays', uploadFile.expirationDate.toString());
    formData.append('password', uploadFile.password);

    const uploadReq = new HttpRequest('POST', '/api/file/upload', formData, {
      reportProgress: true,
      responseType: 'json'
    });

    return this.httpClient.request(uploadReq);
  }

  public delete(fileId: Number): Observable<Object> {
    return this.httpClient.delete('/api/file/' + fileId);
  }
}

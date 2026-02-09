import { HttpClient } from '@angular/common/http';
import { Injectable, Resource } from '@angular/core';
import { DSFile } from '../models/DSFile';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  constructor(private httpClient: HttpClient) { }

  public getFiles(userId: Number): Observable<DSFile[]> {
    return this.httpClient.get<DSFile[]>('/api/file/list/' + userId);
  }

  public download(userId: Number, filePath: String): Observable<Blob> {
    const httpOptions = {
      responseType: 'blob' as 'json'
    };

    return this.httpClient.get<Blob>('/api/file/download/' + userId + "/" + filePath, httpOptions);
  }

  public delete(fileId: Number): Observable<Object> {
    return this.httpClient.delete('/api/file/' + fileId);
  }
}

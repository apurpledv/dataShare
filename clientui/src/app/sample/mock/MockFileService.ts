import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { DSFile } from "@app/core/models/DSFile";
import { UploadFile } from "@app/core/models/UploadFile";

@Injectable({
  providedIn: 'root'
})
export class MockFileService {
    public getFile(fileId: Number): Observable<DSFile> {
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
        return new Observable((observer) => observer.next(mockFile));
    }

    public getFiles(userId: Number): Observable<DSFile[]> {
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
        const mockFiles = [mockFile];
        return new Observable((observer) => observer.next(mockFiles));
    }

    public download(userId: Number, filePath: String): Observable<Blob> {
        const mockBlob = new Blob();
        return new Observable((observer) => observer.next(mockBlob));
    }

    public upload(uploadFile: UploadFile): Observable<Object> {
        return new Observable((observer) => observer.next(1));
    }

    public delete(fileId: Number): Observable<Object> {
        return new Observable((observer) => observer.next(1));
    }
}
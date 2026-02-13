import { TestBed } from "@angular/core/testing";
import { HttpClient, provideHttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { FileService } from "@app/core/services/file.service";
import { DSFile } from "@app/core/models/DSFile";
import { UploadFile } from "@app/core/models/UploadFile";

describe('FileService Unit Tests', () => {
    let service: FileService;
    let httpClient: HttpClient;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                provideHttpClient()
            ]
        });
        service = TestBed.inject(FileService);
        httpClient = TestBed.inject(HttpClient);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should call getFile() and return an Observable<DSFile>', () => {
        const spy = jest.spyOn(service, 'getFile');
        const spyHttp = jest.spyOn(httpClient, 'get');
        const result = service.getFile(1);

        expect(spy).toHaveBeenCalled();
        expect(spyHttp).toHaveBeenCalled();
        expect(result).toBeInstanceOf(Observable<DSFile>);
    });

    it('should call getFiles() and return an Observable<DSFile[]>', () => {
        const spy = jest.spyOn(service, 'getFiles');
        const spyHttp = jest.spyOn(httpClient, 'get');
        const result = service.getFiles(1);

        expect(spy).toHaveBeenCalled();
        expect(spyHttp).toHaveBeenCalled();
        expect(result).toBeInstanceOf(Observable<DSFile[]>);
    });

    it('should call download() and return an Observable<Blob>', () => {
        const spy = jest.spyOn(service, 'download');
        const spyHttp = jest.spyOn(httpClient, 'get');
        const result = service.download(1, 'filepath.png');

        expect(spy).toHaveBeenCalled();
        expect(spyHttp).toHaveBeenCalled();
        expect(result).toBeInstanceOf(Observable<Blob>);
    });

    it('should call upload() and return an Observable<Object>', () => {
        const uploadFile: UploadFile = {
            file: new Blob(),
            userId: 1,
            expirationDate: 3,
            password: ''
        }
        const spy = jest.spyOn(service, 'upload');
        const spyHttp = jest.spyOn(httpClient, 'request');
        const result = service.upload(uploadFile);

        expect(spy).toHaveBeenCalled();
        expect(spyHttp).toHaveBeenCalled();
        expect(result).toBeInstanceOf(Observable<Object>);
    });

    it('should call delete() and return an Observable<Object>', () => {
        const spy = jest.spyOn(service, 'delete');
        const spyHttp = jest.spyOn(httpClient, 'delete');
        const result = service.delete(1);

        expect(spy).toHaveBeenCalled();
        expect(spyHttp).toHaveBeenCalled();
        expect(result).toBeInstanceOf(Observable<Object>);
    });
});
import { TestBed } from "@angular/core/testing";
import { HttpClient, provideHttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { FileService } from "@app/core/services/file.service";
import { DSFile } from "@app/core/models/DSFile";
import { UploadFile } from "@app/core/models/UploadFile";
import { UserService } from "@app/core/services/user.service";
import { User } from "@app/core/models/User";
import { AuthToken } from "@app/core/models/AuthToken";

describe('UserService Unit Tests', () => {
    let service: UserService;
    let httpClient: HttpClient;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                provideHttpClient()
            ]
        });
        service = TestBed.inject(UserService);
        httpClient = TestBed.inject(HttpClient);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should call register() and return an Observable<Object>', () => {
        const mockUser: User = {
            email: 'email',
            password: 'password'
        }
        const spy = jest.spyOn(service, 'register');
        const spyHttp = jest.spyOn(httpClient, 'post');
        const result = service.register(mockUser);

        expect(spy).toHaveBeenCalled();
        expect(spyHttp).toHaveBeenCalled();
        expect(result).toBeInstanceOf(Observable<Object>);
    });

    it('should call login() and return an Observable<AuthToken>', () => {
        const mockUser: User = {
            email: 'email',
            password: 'password'
        }
        const spy = jest.spyOn(service, 'login');
        const spyHttp = jest.spyOn(httpClient, 'post');
        const result = service.login(mockUser);

        expect(spy).toHaveBeenCalled();
        expect(spyHttp).toHaveBeenCalled();
        expect(result).toBeInstanceOf(Observable<AuthToken>);
    });
});
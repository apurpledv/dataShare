import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { AuthToken } from "@app/core/models/AuthToken";
import { User } from "@app/core/models/User";

@Injectable({
  providedIn: 'root'
})
export class MockUserService {
    register(user: User): Observable<Object> {
        return new Observable((observer) => observer.next(1));
    }

    login(user: User): Observable<AuthToken> {
        const mockToken: AuthToken = {token: '123456.123456.123456789', userId: '1'}
        return new Observable((observer) => observer.next(mockToken))
    }
}
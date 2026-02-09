import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../models/User';
import { Observable } from 'rxjs';
import { AuthToken } from '../models/AuthToken';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private httpClient: HttpClient) { }

  public register(user: User): Observable<Object> {
    return this.httpClient.post('/api/register', user);
  }

  public login(user: User): Observable<AuthToken> {
    return this.httpClient.post<AuthToken>('/api/login', user);
  }
}

import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log("Intercepting request: " + req.url);

    // don't bother checking for tokens if it's a public endpoint
    const publicEndpoints = ['/api/login', '/api/register'];
    if (publicEndpoints.some(url => req.url.includes(url))) {
      return next.handle(req);
    }

    const token = sessionStorage.getItem('auth_token');

    // without token: handle normally
    if (!token)
      return next.handle(req);

    // with token: insert into the Authorisation Header
    const authReq = req.clone({
      setHeaders: {
        Authorization: 'Bearer ' + token
      }
    });

    return next.handle(authReq);
  }
}

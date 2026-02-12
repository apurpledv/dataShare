import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private jwtHelper: JwtHelperService, private router: Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log("Intercepting request: " + req.url);
    
    const token = sessionStorage.getItem('auth_token');
    const publicEndpoints = ['/api/login', '/api/register'];

    // don't bother checking for tokens if it's a public endpoint
    if (publicEndpoints.some(url => req.url.includes(url))) {
      return next.handle(req);
    }

    // if no token -> send to login
    if (!token) {
      this.router.navigateByUrl('login');
      return next.handle(req);
    }

    // if token expired -> send to login
    let expDate = this.jwtHelper.getTokenExpirationDate(token) || new Date(Date.now() - 1);

    if (expDate < new Date()) {
      sessionStorage.removeItem('auth_token');
      sessionStorage.removeItem('user_id');
      this.router.navigateByUrl('login');
      return next.handle(req);
    }

    // with valid token: insert into the Authorisation Header
    const authReq = req.clone({
      setHeaders: {
        Authorization: 'Bearer ' + token
      }
    });

    return next.handle(authReq);
  }
}

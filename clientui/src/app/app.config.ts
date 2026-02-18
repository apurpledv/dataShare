import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { AuthInterceptor } from './core/services/auth-interceptor.service';
import { JwtModule } from '@auth0/angular-jwt';

export function tokenGetter() {
  return sessionStorage.getItem("auth_token");
}

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(
        JwtModule.forRoot({
            config: {
                tokenGetter: tokenGetter,
            },
        }),
    ),
    provideHttpClient(withInterceptorsFromDi()),
    { 
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor, 
      multi: true 
    },
    provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes)
  ]
};

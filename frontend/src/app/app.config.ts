import { ApplicationConfig, ErrorHandler, inject, provideZoneChangeDetection } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
//agregue el withhashlocation
import { provideRouter, withHashLocation, withInMemoryScrolling, withNavigationErrorHandler } from '@angular/router';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { routes } from './app.routes';
import { GlobalErrorHandler } from './core/global-error.handler';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { AlertService } from './core/services/alert.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(
      routes,
      withHashLocation(),
      withInMemoryScrolling({ scrollPositionRestoration: 'top' }),
      withNavigationErrorHandler((e) => {
        console.error('Error de navegación', e.error);
        inject(AlertService).error(`No se pudo abrir la página: ${e.error}`.slice(0, 300));
      }),
    ),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
  ],
};
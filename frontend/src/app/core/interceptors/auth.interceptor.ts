import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

// Agrega el token JWT a cada petición.
// Solo cierra la sesión cuando el backend dice que el TOKEN no sirve (401).
// Cualquier otro error (400, 404, 409 de reglas de negocio, 500...) se muestra como alerta
// y el usuario sigue en la misma pantalla.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.getToken();

  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      const esLogin = req.url.includes('/auth/login');
      if (err.status === 401 && token && !esLogin) {
        auth.logout();
      }
      return throwError(() => err);
    }),
  );
};
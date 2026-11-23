import { HttpContextToken, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AlertService } from '../services/alert.service';

// Las peticiones con SILENT = true no muestran aviso: el componente muestra el error donde corresponde
export const SILENT = new HttpContextToken<boolean>(() => false);

export function mensajeDeError(err: HttpErrorResponse): string {
  if (err.status === 0) {
    return 'No se pudo conectar con el servidor. Revisa que el backend esté encendido en el puerto 8082.';
  }
  const cuerpo = err.error;
  if (cuerpo?.errores) {
    const detalle = Object.values(cuerpo.errores).join('. ');
    return `${cuerpo.mensaje}: ${detalle}`;
  }
  if (cuerpo?.mensaje) {
    return cuerpo.mensaje;
  }
  return `Error ${err.status}: no se pudo completar la operación.`;
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const alertas = inject(AlertService);
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (!req.context.get(SILENT)) {
        alertas.error(mensajeDeError(err));
      }
      return throwError(() => err);
    }),
  );
};

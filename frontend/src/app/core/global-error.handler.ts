import { HttpErrorResponse } from '@angular/common/http';
import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { AlertService } from './services/alert.service';

// Muestra en pantalla los errores inesperados de Angular (además de la consola)
@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private injector: Injector) {}

  handleError(error: unknown): void {
    console.error(error);
    // Los errores HTTP ya los muestra el interceptor
    if (error instanceof HttpErrorResponse) return;
    const texto = error instanceof Error ? `${error.name}: ${error.message}` : String(error);
    this.injector.get(AlertService).error(`Error en la aplicación: ${texto}`.slice(0, 300));
  }
}
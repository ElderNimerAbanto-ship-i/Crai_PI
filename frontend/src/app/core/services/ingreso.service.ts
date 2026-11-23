import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API } from '../api.config';
import { Ingreso, TipoAcceso } from '../interfaces/ingreso.interface';
import { SILENT } from '../interceptors/error.interceptor';

@Injectable({ providedIn: 'root' })
export class IngresoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API}/ingresos`;

  listar(): Observable<Ingreso[]> {
    return this.http.get<Ingreso[]>(this.url);
  }

  // SILENT: el componente muestra el error (ej. "El usuario ya se encuentra registrado")
  registrar(codigoUni: string, tipoAcceso: TipoAcceso): Observable<Ingreso> {
    return this.http.post<Ingreso>(
      `${this.url}/registrar`,
      { codigoUni, tipoAcceso },
      { context: new HttpContext().set(SILENT, true) },
    );
  }
}
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API } from '../api.config';
import { LibroPrestado, Resumen } from '../interfaces/reporte.interface';
import { HorarioUso, PrestamoMes, UsoEscuela, UsoSala } from '../interfaces/dashboard.interface';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly http = inject(HttpClient);

  resumen(): Observable<Resumen> {
    return this.http.get<Resumen>(`${API}/reportes/resumen`);
  }

  librosMasPrestados(limite = 5): Observable<LibroPrestado[]> {
    const params = new HttpParams().set('limite', limite);
    return this.http.get<LibroPrestado[]>(`${API}/reportes/libros-mas-prestados`, { params });
  }
    // ---- Dashboard (desde y hasta en formato yyyy-MM-dd) ----
  usoPorEscuela(desde: string, hasta: string): Observable<UsoEscuela[]> {
    return this.http.get<UsoEscuela[]>(`${API}/reportes/uso-por-escuela`, { params: this.rango(desde, hasta) });
  }

  usoSalas(desde: string, hasta: string): Observable<UsoSala[]> {
    return this.http.get<UsoSala[]>(`${API}/reportes/uso-salas`, { params: this.rango(desde, hasta) });
  }

  usoHorarios(desde: string, hasta: string): Observable<HorarioUso[]> {
    return this.http.get<HorarioUso[]>(`${API}/reportes/uso-salas/horarios`, { params: this.rango(desde, hasta) });
  }

  prestamosPorMes(meses = 12): Observable<PrestamoMes[]> {
    const params = new HttpParams().set('meses', meses);
    return this.http.get<PrestamoMes[]>(`${API}/reportes/prestamos-por-mes`, { params });
  }

  private rango(desde: string, hasta: string): HttpParams {
    return new HttpParams().set('desde', desde).set('hasta', hasta);
  }
}

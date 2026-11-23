import { HttpClient, HttpContext, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API } from '../api.config';
import { SILENT } from '../interceptors/error.interceptor';
import {
  Disponibilidad,
  EstudianteValidado,
  HorarioSala,
  ReservaPublica,
  ReservaPublicaResultado,
  ReservaSala,
} from '../interfaces/reserva.interface';
import { SalaEstado } from '../interfaces/sala.interface';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private readonly http = inject(HttpClient);

  // --- Flujo del estudiante ---
  validarEstudiante(codigoUni: string): Observable<EstudianteValidado> {
    return this.http.get<EstudianteValidado>(
      `${API}/reservas-publicas/estudiantes/${encodeURIComponent(codigoUni)}`,
      { context: new HttpContext().set(SILENT, true) },
    );
  }

  disponibilidad(duracionMinutos: number, personas: number): Observable<Disponibilidad> {
    const params = new HttpParams().set('duracionMinutos', duracionMinutos).set('personas', personas);
    return this.http.get<Disponibilidad>(`${API}/reservas-publicas/disponibilidad`, { params });
  }

  reservar(datos: ReservaPublica): Observable<ReservaPublicaResultado> {
    return this.http.post<ReservaPublicaResultado>(`${API}/reservas-publicas`, datos);
  }

  estadoSalas(): Observable<SalaEstado[]> {
    return this.http.get<SalaEstado[]>(`${API}/reservas-publicas/salas-estado`, {
      context: new HttpContext().set(SILENT, true),
    });
  }

  // ---Administración---

  listar(): Observable<ReservaSala[]> {
    return this.http.get<ReservaSala[]>(`${API}/reservas-sala`);
  }

  cancelar(id: number): Observable<ReservaSala> {
    return this.http.put<ReservaSala>(`${API}/reservas-sala/${id}/cancelar`, null);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/reservas-sala/${id}`);
  }
    horarios(idSala: number, duracionMinutos: number): Observable<HorarioSala[]> {
    const params = new HttpParams().set('duracionMinutos', duracionMinutos);
    return this.http.get<HorarioSala[]>(`${API}/reservas-publicas/salas/${idSala}/horarios`, { params });
  }
}

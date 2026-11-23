import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API } from '../api.config';
import { DetallePrestamo, Prestamo, PrestamoRegistro } from '../interfaces/prestamo.interface';

@Injectable({ providedIn: 'root' })
export class PrestamoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API}/prestamos`;

  listar(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(this.url);
  }

  listarVencidos(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(`${this.url}/vencidos`);
  }

  detalle(id: number): Observable<DetallePrestamo[]> {
    return this.http.get<DetallePrestamo[]>(`${this.url}/${id}/detalles`);
  }

  registrar(datos: PrestamoRegistro): Observable<Prestamo> {
    return this.http.post<Prestamo>(`${this.url}/registrar-sp`, datos);
  }

  devolver(idEjemplar: number): Observable<Prestamo> {
    return this.http.put<Prestamo>(`${this.url}/devolver/${idEjemplar}`, null);
  }

  renovar(id: number, diasExtension: number): Observable<Prestamo> {
    const params = new HttpParams().set('diasExtension', diasExtension);
    return this.http.put<Prestamo>(`${this.url}/renovar/${id}`, null, { params });
  }
}
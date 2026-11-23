import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API } from '../api.config';
import { Ejemplar } from '../interfaces/libro.interface';

@Injectable({ providedIn: 'root' })
export class EjemplarService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API}/ejemplares`;

  listar(): Observable<Ejemplar[]> {
    return this.http.get<Ejemplar[]>(this.url);
  }

  crear(ejemplar: Ejemplar): Observable<Ejemplar> {
    return this.http.post<Ejemplar>(this.url, ejemplar);
  }

  actualizar(id: number, ejemplar: Ejemplar): Observable<Ejemplar> {
    return this.http.put<Ejemplar>(`${this.url}/${id}`, ejemplar);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

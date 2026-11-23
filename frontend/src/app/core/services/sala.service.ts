import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API } from '../api.config';
import { Sala } from '../interfaces/sala.interface';

@Injectable({ providedIn: 'root' })
export class SalaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API}/salas`;

  listar(): Observable<Sala[]> {
    return this.http.get<Sala[]>(this.url);
  }

  crear(sala: Sala): Observable<Sala> {
    return this.http.post<Sala>(this.url, sala);
  }

  actualizar(id: number, sala: Sala): Observable<Sala> {
    return this.http.put<Sala>(`${this.url}/${id}`, sala);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

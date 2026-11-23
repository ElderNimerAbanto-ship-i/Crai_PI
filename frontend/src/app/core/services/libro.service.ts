import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API } from '../api.config';
import { Libro } from '../interfaces/libro.interface';

@Injectable({ providedIn: 'root' })
export class LibroService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API}/libros`;

  listar(buscar?: string): Observable<Libro[]> {
    let params = new HttpParams();
    if (buscar && buscar.trim()) {
      params = params.set('buscar', buscar.trim());
    }
    return this.http.get<Libro[]>(this.url, { params });
  }

  obtener(id: number): Observable<Libro> {
    return this.http.get<Libro>(`${this.url}/${id}`);
  }

  // El backend recibe multipart/form-data: campos del libro + archivo "file" opcional
  crear(datos: FormData): Observable<Libro> {
    return this.http.post<Libro>(this.url, datos);
  }

  actualizar(id: number, datos: FormData): Observable<Libro> {
    return this.http.put<Libro>(`${this.url}/${id}`, datos);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

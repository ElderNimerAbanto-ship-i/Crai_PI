import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API } from '../api.config';
import { EscuelaProf, Usuario, UsuarioRegistro } from '../interfaces/usuario.interface';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${API}/usuarios`);
  }

  registrar(datos: UsuarioRegistro): Observable<Usuario> {
    return this.http.post<Usuario>(`${API}/auth/registro`, datos);
  }

  cambiarAcceso(id: number, habilitado: boolean): Observable<Usuario> {
    const params = new HttpParams().set('habilitado', habilitado);
    return this.http.put<Usuario>(`${API}/usuarios/${id}/acceso`, null, { params });
  }

  // Escuelas profesionales
  listarEscuelas(): Observable<EscuelaProf[]> {
    return this.http.get<EscuelaProf[]>(`${API}/escuelas`);
  }

  crearEscuela(escuela: EscuelaProf): Observable<EscuelaProf> {
    return this.http.post<EscuelaProf>(`${API}/escuelas`, escuela);
  }

  actualizarEscuela(id: number, escuela: EscuelaProf): Observable<EscuelaProf> {
    return this.http.put<EscuelaProf>(`${API}/escuelas/${id}`, escuela);
  }

  eliminarEscuela(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/escuelas/${id}`);
  }
}

import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API } from '../api.config';
import { LoginRequest, LoginResponse } from '../interfaces/auth.interface';
import { Rol } from '../interfaces/usuario.interface';
import { SILENT } from '../interceptors/error.interceptor';

const TOKEN = 'token_crai';
const USUARIO = 'usuario_crai';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  // Usuario en sesión (se recupera del localStorage al recargar la página)
  private readonly _usuario = signal<LoginResponse | null>(this.leerUsuario());
  readonly usuario = this._usuario.asReadonly();
  readonly logueado = computed(() => this._usuario() !== null);
  readonly esAdmin = computed(() => this._usuario()?.rol === 'ADMIN');

  login(credenciales: LoginRequest): Observable<LoginResponse> {
    // SILENT: el error se muestra dentro del formulario de login, no como alerta
    return this.http
      .post<LoginResponse>(`${API}/auth/login`, credenciales, {
        context: new HttpContext().set(SILENT, true),
      })
      .pipe(
        tap((resp) => {
          localStorage.setItem(TOKEN, resp.token);
          localStorage.setItem(USUARIO, JSON.stringify(resp));
          this._usuario.set(resp);
        }),
      );
  }

  logout(): void {
    localStorage.removeItem(TOKEN);
    localStorage.removeItem(USUARIO);
    this._usuario.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN);
  }

  tieneRol(...roles: Rol[]): boolean {
    const rol = this._usuario()?.rol;
    return !!rol && roles.includes(rol);
  }

  private leerUsuario(): LoginResponse | null {
    const guardado = localStorage.getItem(USUARIO);
    if (!guardado || !localStorage.getItem(TOKEN)) return null;
    try {
      return JSON.parse(guardado) as LoginResponse;
    } catch {
      return null;
    }
  }
}
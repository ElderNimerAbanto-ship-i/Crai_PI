import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  readonly auth = inject(AuthService);
  menuAbierto = signal(false);

  private readonly enlacesAdmin = [
    { ruta: '/libros', texto: 'Libros' },
    { ruta: '/ejemplares', texto: 'Ejemplares' },
    { ruta: '/prestamos', texto: 'Préstamos' },
    { ruta: '/reservas', texto: 'Reservas' },
    { ruta: '/usuarios', texto: 'Usuarios' },
    { ruta: '/ingresos', texto: 'Ingresos' },
    { ruta: '/configuracion', texto: 'Configuración' },
    { ruta: '/dashboard', texto: 'Dashboard' },
  ];

  // El menú de gestión solo se muestra al ADMIN
  enlaces = computed(() => (this.auth.esAdmin() ? this.enlacesAdmin : []));

  alternar(): void {
    this.menuAbierto.update((v) => !v);
  }

  cerrar(): void {
    this.menuAbierto.set(false);
  }

  salir(): void {
    this.cerrar();
    this.auth.logout();
  }
}
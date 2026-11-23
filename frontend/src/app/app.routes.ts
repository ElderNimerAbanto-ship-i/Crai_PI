import { Routes } from '@angular/router';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';
import { HomeComponent } from './features/home/home.component';
import { LibrosListComponent } from './features/libros/libros-list/libros-list.component';
import { LibroFormComponent } from './features/libros/libro-form/libro-form.component';
import { EjemplaresComponent } from './features/ejemplares/ejemplares.component';
import { PrestamosComponent } from './features/prestamos/prestamos.component';
import { ReservarComponent } from './features/reservas/reservar/reservar.component';
import { ReservasAdminComponent } from './features/reservas/reservas-admin/reservas-admin.component';
import { UsuariosComponent } from './features/usuarios/usuarios.component';
import { IngresosComponent } from './features/ingresos/ingresos.component';
import { ConfiguracionComponent } from './features/configuracion/configuracion.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { LoginComponent } from './features/login/login.component';
import { adminGuard, invitadoGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Login: pantalla completa, fuera del layout
  { path: 'login', component: LoginComponent, canActivate: [invitadoGuard] },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'home' },
      // públicas
      { path: 'home', component: HomeComponent },
      { path: 'reservar', component: ReservarComponent },
      // solo ADMIN
      { path: 'libros', component: LibrosListComponent, canActivate: [adminGuard] },
      { path: 'libros/nuevo', component: LibroFormComponent, canActivate: [adminGuard] },
      { path: 'libros/editar/:id', component: LibroFormComponent, canActivate: [adminGuard] },
      { path: 'ejemplares', component: EjemplaresComponent, canActivate: [adminGuard] },
      { path: 'prestamos', component: PrestamosComponent, canActivate: [adminGuard] },
      { path: 'reservas', component: ReservasAdminComponent, canActivate: [adminGuard] },
      { path: 'usuarios', component: UsuariosComponent, canActivate: [adminGuard] },
      { path: 'ingresos', component: IngresosComponent, canActivate: [adminGuard] },
      { path: 'configuracion', component: ConfiguracionComponent, canActivate: [adminGuard] },
      { path: 'dashboard', component: DashboardComponent, canActivate: [adminGuard] },
    ],
  },
  { path: '**', redirectTo: 'home' },
];
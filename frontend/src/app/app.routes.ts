import { Routes } from '@angular/router';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';
import { HomeComponent } from './features/home/home.component';
import { ReservarComponent } from './features/reservas/reservar/reservar.component';
import { LoginComponent } from './features/login/login.component';
import { invitadoGuard } from './core/guards/auth.guard';

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
    ],
  },
  { path: '**', redirectTo: 'home' },
];
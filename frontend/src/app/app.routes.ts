import { Routes } from '@angular/router';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';
import { HomeComponent } from './features/home/home.component';
import { ReservarComponent } from './features/reservas/reservar/reservar.component';

export const routes: Routes = [
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
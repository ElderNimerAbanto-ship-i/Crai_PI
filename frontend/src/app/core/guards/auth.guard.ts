import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Solo deja pasar si hay sesión iniciada
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.logueado() ? true : inject(Router).createUrlTree(['/login']);
};

// Solo deja pasar al ADMIN
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.logueado()) return router.createUrlTree(['/login']);
  return auth.esAdmin() ? true : router.createUrlTree(['/home']);
};

// Si ya inició sesión, no tiene sentido volver a la pantalla de login
export const invitadoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.logueado() ? inject(Router).createUrlTree(['/home']) : true;
};
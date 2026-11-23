import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { AlertService } from '../../core/services/alert.service';
import { mensajeDeError } from '../../core/interceptors/error.interceptor';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly alertas = inject(AlertService);
  private readonly router = inject(Router);

  cargando = signal(false);
  error = signal('');
  verPassword = signal(false);

  form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  ingresar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.error.set('');
    this.cargando.set(true);

    this.auth
      .login(this.form.getRawValue())
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (resp) => {
          this.alertas.ok(`Bienvenido, ${resp.nombrePersona}`);
          this.router.navigate([resp.rol === 'ADMIN' ? '/dashboard' : '/home']);
        },
        error: (err: HttpErrorResponse) => {
          this.form.controls.password.reset();
          this.error.set(mensajeDeError(err));
        },
      });
  }
}
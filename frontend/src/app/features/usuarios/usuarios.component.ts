import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EscuelaProf, Rol, Usuario } from '../../core/interfaces/usuario.interface';
import { AlertService } from '../../core/services/alert.service';
import { UsuarioService } from '../../core/services/usuario.service';

@Component({
  standalone: true,
  selector: 'app-usuarios',
  imports: [ReactiveFormsModule],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.css',
})
export class UsuariosComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly srv = inject(UsuarioService);
  private readonly alertas = inject(AlertService);

  lista = signal<Usuario[]>([]);
  escuelas = signal<EscuelaProf[]>([]);
  cargando = signal(true);
  mostrarForm = signal(false);
  roles: Rol[] = ['ESTUDIANTE', 'DOCENTE', 'ADMIN'];

  form = this.fb.nonNullable.group({
    nombres: ['', Validators.required],
    apellidos: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    codigoUni: ['', Validators.required],
    username: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    rol: ['ESTUDIANTE' as Rol],
    idEscuelaProf: [0],
  });

  ngOnInit(): void {
    this.cargar();
    this.srv.listarEscuelas().subscribe((e) => this.escuelas.set(e.filter((x) => x.estado === 'A')));
  }

  cargar(): void {
    this.srv.listar().subscribe({
      next: (u) => {
        this.lista.set(u);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  registrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.srv
      .registrar({ ...v, idEscuelaProf: Number(v.idEscuelaProf) > 0 ? Number(v.idEscuelaProf) : null })
      .subscribe(() => {
        this.alertas.ok('Usuario registrado');
        this.form.reset({ rol: 'ESTUDIANTE', idEscuelaProf: 0 });
        this.mostrarForm.set(false);
        this.cargar();
      });
  }

  alternarAcceso(u: Usuario): void {
    this.srv.cambiarAcceso(u.idPersona, !u.accesoHabilitado).subscribe(() => {
      this.alertas.ok(u.accesoHabilitado ? 'Acceso deshabilitado' : 'Acceso habilitado');
      this.cargar();
    });
  }
}

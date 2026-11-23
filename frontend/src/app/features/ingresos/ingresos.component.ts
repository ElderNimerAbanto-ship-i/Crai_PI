import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Ingreso, TipoAcceso } from '../../core/interfaces/ingreso.interface';
import { AlertService } from '../../core/services/alert.service';
import { IngresoService } from '../../core/services/ingreso.service';
import { mensajeDeError } from '../../core/interceptors/error.interceptor';
import { Grupo, TablaAgrupada, agrupar } from '../../core/utils/agrupar';
import { fechaHora } from '../../core/utils/format';

@Component({
  standalone: true,
  selector: 'app-ingresos',
  imports: [FormsModule],
  templateUrl: './ingresos.component.html',
  styleUrl: './ingresos.component.css',
})
export class IngresosComponent implements OnInit {
  private readonly srv = inject(IngresoService);
  private readonly alertas = inject(AlertService);

  lista = signal<Ingreso[]>([]);
  cargando = signal(true);
  enviando = signal(false);
  errorRegistro = signal('');
  codigo = '';
  fechaHora = fechaHora;
  readonly tabla = new TablaAgrupada();

  // Un grupo por persona (código universitario)
  grupos = computed(() =>
    agrupar(
      this.lista(),
      (i) => i.codigoUni,
      (i) => i.nombrePersona,
      (i) => i.codigoUni,
      (i) => i.fechaHora,
    ),
  );
  visibles = computed(() => this.tabla.filtrar(this.grupos()));

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.srv.listar().subscribe({
      next: (i) => {
        this.lista.set(i);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  registrar(tipo: TipoAcceso): void {
    const c = this.codigo.trim();
    if (!c || this.enviando()) return;
    this.errorRegistro.set('');
    this.enviando.set(true);

    this.srv.registrar(c, tipo).subscribe({
      next: (i) => {
        this.enviando.set(false);
        this.alertas.ok(`${tipo === 'ENTRADA' ? 'Entrada' : 'Salida'} registrada: ${i.nombrePersona}`);
        this.codigo = '';
        this.cargar();
      },
      // El error NO cierra la sesión: se muestra en pantalla y el usuario sigue trabajando
      error: (err: HttpErrorResponse) => {
        this.enviando.set(false);
        const mensaje = mensajeDeError(err);
        this.errorRegistro.set(mensaje);
        this.alertas.error(mensaje);
      },
    });
  }

  // ---- Resumen de cada persona ----
  ultimo(g: Grupo<Ingreso>): Ingreso {
    return g.items[0];
  }

  // Dentro = su último movimiento de hoy es una ENTRADA
  estaDentro(g: Grupo<Ingreso>): boolean {
    const u = g.items[0];
    return u.tipoAcceso === 'ENTRADA' && new Date(u.fechaHora).toDateString() === new Date().toDateString();
  }

  entradas(g: Grupo<Ingreso>): number {
    return g.items.filter((i) => i.tipoAcceso === 'ENTRADA').length;
  }
}
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Grupo, TablaAgrupada, agrupar } from '../../core/utils/agrupar';
import { Ejemplar } from '../../core/interfaces/libro.interface';
import { DetallePrestamo, Prestamo } from '../../core/interfaces/prestamo.interface';
import { AlertService } from '../../core/services/alert.service';
import { EjemplarService } from '../../core/services/ejemplar.service';
import { PrestamoService } from '../../core/services/prestamo.service';
import { estaVencido, fecha } from '../../core/utils/format';

@Component({
  standalone: true,
  selector: 'app-prestamos',
  imports: [ReactiveFormsModule, FormsModule],
  templateUrl: './prestamos.component.html',
  styleUrl: './prestamos.component.css',
  
})
export class PrestamosComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly srv = inject(PrestamoService);
  private readonly ejemplaresSrv = inject(EjemplarService);
  private readonly alertas = inject(AlertService);

  lista = signal<Prestamo[]>([]);
  disponibles = signal<Ejemplar[]>([]);
  detalles = signal<Record<number, DetallePrestamo[]>>({});
  cargando = signal(true);
  soloVencidos = signal(false);
  fecha = fecha;
  estaVencido = estaVencido;
  // ---- Tabla agrupada por persona ----
  readonly tabla = new TablaAgrupada();
  grupos = computed(() =>
    agrupar(
      this.lista(),
      (p) => String(p.idPersona),
      (p) => p.nombrePersona,
      () => '',
      (p) => p.fechaPrestamo,
    ),
  );
  visibles = computed(() => this.tabla.filtrar(this.grupos()));

  activos(g: Grupo<Prestamo>): number {
    return g.items.filter((p) => p.estado === 'A' && !estaVencido(p.fechaVencimiento, p.estado)).length;
  }

  vencidos(g: Grupo<Prestamo>): number {
    return g.items.filter((p) => estaVencido(p.fechaVencimiento, p.estado)).length;
  }

  devueltos(g: Grupo<Prestamo>): number {
    return g.items.filter((p) => p.estado === 'D').length;
  }

  // Vencimiento más cercano entre sus préstamos activos
  proximoVencimiento(g: Grupo<Prestamo>): string | null {
    const fechas = g.items.filter((p) => p.estado === 'A').map((p) => p.fechaVencimiento).sort();
    return fechas[0] ?? null;
  }

  // Búsqueda del ejemplar por código (N.º de ejemplar o ISBN) o título
  busqueda = signal('');
  ejemplarSel = signal<Ejemplar | null>(null);
  resultados = computed(() => {
    const q = this.normalizar(this.busqueda());
    if (!q) return [];
    return this.disponibles()
      .filter(
        (e) =>
          String(e.idEjemplar) === q ||
          this.normalizar(String(e.idEjemplar)).includes(q) ||
          this.normalizar(e.isbn ?? '').includes(q) ||
          this.normalizar(e.tituloLibro ?? '').includes(q),
      )
      // primero la coincidencia exacta de código
      .sort((a, b) => Number(this.esExacto(b, q)) - Number(this.esExacto(a, q)))
      .slice(0, 8);
  });

  form = this.fb.nonNullable.group({
    codigoUni: ['', Validators.required],
    idEjemplar: [0, [Validators.required, Validators.min(1)]],
    dias: [3, [Validators.required, Validators.min(1), Validators.max(3)]],
  });

  private normalizar(t: string): string {
    return t.trim().toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
  }

  private esExacto(e: Ejemplar, q: string): boolean {
    return String(e.idEjemplar) === q || this.normalizar(e.isbn ?? '') === q;
  }

  alBuscar(valor: string): void {
    this.busqueda.set(valor);
  }

  elegirEjemplar(e: Ejemplar): void {
    this.ejemplarSel.set(e);
    this.form.controls.idEjemplar.setValue(e.idEjemplar!);
    this.busqueda.set('');
  }

  // Enter en el buscador: elige el primer resultado (útil con lector de código de barras)
  elegirPrimero(): void {
    const primero = this.resultados()[0];
    if (primero) this.elegirEjemplar(primero);
  }

  quitarEjemplar(): void {
    this.ejemplarSel.set(null);
    this.form.controls.idEjemplar.setValue(0);
  }

  ngOnInit(): void {
    this.cargar();
    this.cargarDisponibles();
  }

  cargar(): void {
    this.cargando.set(true);
    const origen = this.soloVencidos() ? this.srv.listarVencidos() : this.srv.listar();
    origen.subscribe({
      next: (p) => {
        this.lista.set(p);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  cargarDisponibles(): void {
    this.ejemplaresSrv.listar().subscribe((e) => this.disponibles.set(e.filter((x) => x.estado === 'DISPONIBLE')));
  }

  filtrar(vencidos: boolean): void {
    this.soloVencidos.set(vencidos);
    this.cargar();
  }

  registrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.srv
      .registrar({ codigoUni: v.codigoUni.trim(), idEjemplar: Number(v.idEjemplar), dias: Number(v.dias) })
      .subscribe(() => {
        this.alertas.ok('Préstamo registrado');
        this.form.reset({ codigoUni: '', idEjemplar: 0, dias: 3 });
        this.ejemplarSel.set(null);
        this.busqueda.set('');
        this.cargar();
        this.cargarDisponibles();
      });
  }

  verDetalle(p: Prestamo): void {
    if (this.detalles()[p.idPrestamo]) {
      this.detalles.update((d) => {
        const { [p.idPrestamo]: _, ...resto } = d;
        return resto;
      });
      return;
    }
    this.srv.detalle(p.idPrestamo).subscribe((d) => this.detalles.update((m) => ({ ...m, [p.idPrestamo]: d })));
  }

  devolver(p: Prestamo): void {
    this.srv.detalle(p.idPrestamo).subscribe((d) => {
      if (!d.length) return;
      this.srv.devolver(d[0].idEjemplar).subscribe(() => {
        this.alertas.ok(`Devolución registrada: ${d[0].tituloLibro}`);
        this.cargar();
        this.cargarDisponibles();
      });
    });
  }

  renovar(p: Prestamo): void {
    this.srv.renovar(p.idPrestamo, 3).subscribe(() => {
      this.alertas.ok('Préstamo renovado 3 días');
      this.cargar();
    });
  }
}
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Ejemplar, EstadoEjemplar, Libro } from '../../core/interfaces/libro.interface';
import { AlertService } from '../../core/services/alert.service';
import { EjemplarService } from '../../core/services/ejemplar.service';
import { LibroService } from '../../core/services/libro.service';
import { normalizar } from '../../core/utils/agrupar';

// Un libro con todas sus copias físicas
interface GrupoLibro {
  idLibro: number;
  titulo: string;
  ejemplares: Ejemplar[];
}

@Component({
  standalone: true,
  selector: 'app-ejemplares',
  imports: [ReactiveFormsModule],
  templateUrl: './ejemplares.component.html',
  styleUrl: './ejemplares.component.css',
})
export class EjemplaresComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly srv = inject(EjemplarService);
  private readonly librosSrv = inject(LibroService);
  private readonly alertas = inject(AlertService);

  lista = signal<Ejemplar[]>([]);
  libros = signal<Libro[]>([]);
  cargando = signal(true);
  editando: number | null = null;
  estados: EstadoEjemplar[] = ['DISPONIBLE', 'PRESTADO', 'MANTENIMIENTO'];
  // libros cuyos ejemplares están desplegados
  abiertos = signal<ReadonlySet<number>>(new Set());

  // Agrupa los ejemplares por libro y ordena por título
  grupos = computed<GrupoLibro[]>(() => {
    const mapa = new Map<number, GrupoLibro>();
    for (const e of this.lista()) {
      const g = mapa.get(e.idLibro) ?? { idLibro: e.idLibro, titulo: e.tituloLibro ?? 'Sin título', ejemplares: [] };
      g.ejemplares.push(e);
      mapa.set(e.idLibro, g);
    }
    const grupos = [...mapa.values()];
    grupos.forEach((g) => g.ejemplares.sort((a, b) => (a.idEjemplar ?? 0) - (b.idEjemplar ?? 0)));
    return grupos.sort((a, b) => a.titulo.localeCompare(b.titulo, 'es'));
  });
  // ---- Buscador de libro (autocompletado) ----
  busquedaLibro = signal('');
  idLibroSel = signal(0);
  indiceActivo = signal(0);
  libroSel = computed(() => this.libros().find((l) => l.idLibro === this.idLibroSel()) ?? null);
  resultadosLibro = computed(() => {
    const q = normalizar(this.busquedaLibro());
    if (!q) return [];
    return this.libros()
      .filter((l) => normalizar(l.titulo).includes(q) || normalizar(l.autor).includes(q))
      // primero los que empiezan con lo escrito
      .sort((a, b) => Number(normalizar(b.titulo).startsWith(q)) - Number(normalizar(a.titulo).startsWith(q))
        || a.titulo.localeCompare(b.titulo, 'es'))
      .slice(0, 8);
  });

  alBuscarLibro(valor: string): void {
    this.busquedaLibro.set(valor);
    this.indiceActivo.set(0);
  }

  elegirLibro(l: Libro): void {
    this.idLibroSel.set(l.idLibro);
    this.form.controls.idLibro.setValue(l.idLibro);
    this.form.controls.idLibro.markAsTouched();
    this.busquedaLibro.set('');
  }

  quitarLibro(): void {
    this.idLibroSel.set(0);
    this.form.controls.idLibro.setValue(0);
  }

  // Flechas para moverse, Enter para elegir, Escape para limpiar
  teclaBuscador(ev: KeyboardEvent): void {
    const total = this.resultadosLibro().length;
    if (ev.key === 'ArrowDown' && total) {
      ev.preventDefault();
      this.indiceActivo.update((i) => (i + 1) % total);
    } else if (ev.key === 'ArrowUp' && total) {
      ev.preventDefault();
      this.indiceActivo.update((i) => (i - 1 + total) % total);
    } else if (ev.key === 'Enter') {
      ev.preventDefault();
      const elegido = this.resultadosLibro()[this.indiceActivo()];
      if (elegido) this.elegirLibro(elegido);
    } else if (ev.key === 'Escape') {
      this.busquedaLibro.set('');
    }
  }

  form = this.fb.nonNullable.group({
    idLibro: [0, [Validators.required, Validators.min(1)]],
    isbn: [''],
    estado: ['DISPONIBLE' as EstadoEjemplar],
  });

  ngOnInit(): void {
    this.librosSrv.listar().subscribe((l) => this.libros.set(l));
    this.cargar();
  }

  cargar(): void {
    this.srv.listar().subscribe({
      next: (e) => {
        this.lista.set(e);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  clase(estado: EstadoEjemplar): string {
    return estado === 'DISPONIBLE' ? 'sello-ok' : estado === 'PRESTADO' ? 'sello-aviso' : 'sello-error';
  }

  // Resumen de estados de un libro con varios ejemplares: "2 disponibles", "1 prestado"
  resumen(g: GrupoLibro): { estado: EstadoEjemplar; texto: string }[] {
    const nombres: Record<EstadoEjemplar, [string, string]> = {
      DISPONIBLE: ['disponible', 'disponibles'],
      PRESTADO: ['prestado', 'prestados'],
      MANTENIMIENTO: ['en mantenimiento', 'en mantenimiento'],
    };
    return this.estados
      .map((estado) => ({ estado, n: g.ejemplares.filter((e) => e.estado === estado).length }))
      .filter((x) => x.n > 0)
      .map((x) => ({ estado: x.estado, texto: `${x.n} ${nombres[x.estado][x.n === 1 ? 0 : 1]}` }));
  }

  estaAbierto(idLibro: number): boolean {
    return this.abiertos().has(idLibro);
  }

  alternar(idLibro: number): void {
    this.abiertos.update((s) => {
      const nuevo = new Set(s);
      if (!nuevo.delete(idLibro)) nuevo.add(idLibro);
      return nuevo;
    });
  }

  editar(e: Ejemplar): void {
    this.editando = e.idEjemplar ?? null;
    this.form.setValue({ idLibro: e.idLibro, isbn: e.isbn ?? '', estado: e.estado });
    this.idLibroSel.set(e.idLibro);
    this.busquedaLibro.set('');
    window.scrollTo({ top: 0 });
  }

  cancelar(): void {
    this.editando = null;
    this.form.reset({ idLibro: 0, isbn: '', estado: 'DISPONIBLE' });
    this.idLibroSel.set(0);
    this.busquedaLibro.set('');
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const cuerpo: Ejemplar = { idLibro: Number(v.idLibro), isbn: v.isbn || null, estado: v.estado };
    const peticion = this.editando ? this.srv.actualizar(this.editando, cuerpo) : this.srv.crear(cuerpo);
    peticion.subscribe(() => {
      this.alertas.ok(this.editando ? 'Ejemplar actualizado' : 'Ejemplar agregado');
      this.cancelar();
      this.cargar();
    });
  }

  eliminar(e: Ejemplar): void {
    if (!confirm('¿Eliminar este ejemplar?')) return;
    this.srv.eliminar(e.idEjemplar!).subscribe(() => {
      this.alertas.ok('Ejemplar eliminado');
      this.cargar();
    });
  }
}
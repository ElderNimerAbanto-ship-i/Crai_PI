import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Libro } from '../../core/interfaces/libro.interface';
import { LibroService } from '../../core/services/libro.service';
import { BookCoverComponent } from '../../shared/book-cover/book-cover.component';

// Minúsculas y sin tildes: "calculo" encuentra "Cálculo"
const normalizar = (t: string | null | undefined): string =>
  (t ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim();

@Component({
  standalone: true,
  selector: 'app-home',
  imports: [FormsModule, BookCoverComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  private readonly libroService = inject(LibroService);

  libros = signal<Libro[] | null>(null);
  librosError = signal(false);
  busqueda = signal('');

  // Libros que coinciden con el buscador (título, autor, categoría o sección)
  filtrados = computed(() => {
    const q = normalizar(this.busqueda());
    const todos = this.libros() ?? [];
    if (!q) return todos;
    return todos.filter((l) =>
      normalizar([l.titulo, l.autor, l.nombreCategoria, l.seccionEspecial].filter(Boolean).join(' ')).includes(q),
    );
  });

  // Libros agrupados por categoría (categorías y títulos en orden alfabético)
  porCategoria = computed(() => {
    const grupos = new Map<string, Libro[]>();
    for (const l of this.filtrados()) {
      const clave = l.nombreCategoria?.trim() || 'Sin categoría';
      const lista = grupos.get(clave) ?? [];
      lista.push(l);
      grupos.set(clave, lista);
    }
    return [...grupos.entries()]
      .sort(([a], [b]) => a.localeCompare(b, 'es'))
      .map(([categoria, libros]) => ({
        categoria,
        libros: [...libros].sort((x, y) => x.titulo.localeCompare(y.titulo, 'es')),
      }));
  });

  ngOnInit(): void {
    this.libroService.listar().subscribe({
      next: (l) => this.libros.set(l),
      error: () => this.librosError.set(true),
    });
  }
}
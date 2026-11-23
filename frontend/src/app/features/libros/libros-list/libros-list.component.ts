import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Libro } from '../../../core/interfaces/libro.interface';
import { AlertService } from '../../../core/services/alert.service';
import { LibroService } from '../../../core/services/libro.service';
import { BookCoverComponent } from '../../../shared/book-cover/book-cover.component';

@Component({
  standalone: true,
  selector: 'app-libros-list',
  imports: [FormsModule, RouterLink, BookCoverComponent],
  templateUrl: './libros-list.component.html',
  styleUrl: './libros-list.component.css',
})
export class LibrosListComponent implements OnInit {
  private readonly libros = inject(LibroService);
  private readonly alertas = inject(AlertService);

  lista = signal<Libro[]>([]);
  cargando = signal(true);
  buscar = '';
  private espera?: ReturnType<typeof setTimeout>;

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.libros.listar(this.buscar).subscribe({
      next: (l) => {
        this.lista.set(l);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }
/*
  alEscribir(): void {
    clearTimeout(this.espera);
    this.espera = setTimeout(() => this.cargar(), 300);
  }
*/
  eliminar(libro: Libro): void {
    if (!confirm(`¿Eliminar "${libro.titulo}"? Esta acción no se puede deshacer.`)) return;
    this.libros.eliminar(libro.idLibro).subscribe({
      next: () => {
        this.alertas.ok('Libro eliminado');
        this.cargar();
      },
    });
  }
}

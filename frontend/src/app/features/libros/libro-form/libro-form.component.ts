import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Categoria } from '../../../core/interfaces/libro.interface';
import { AlertService } from '../../../core/services/alert.service';
import { CategoriaService } from '../../../core/services/categoria.service';
import { LibroService } from '../../../core/services/libro.service';
import { BookCoverComponent } from '../../../shared/book-cover/book-cover.component';

@Component({
  standalone: true,
  selector: 'app-libro-form',
  imports: [ReactiveFormsModule, RouterLink, BookCoverComponent],
  templateUrl: './libro-form.component.html',
  styleUrl: './libro-form.component.css',
})
export class LibroFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly libros = inject(LibroService);
  private readonly categoriasSrv = inject(CategoriaService);
  private readonly alertas = inject(AlertService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  categorias = signal<Categoria[]>([]);
  guardando = signal(false);
  id: number | null = null;
  archivo: File | null = null;
  vistaPrevia = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    titulo: ['', Validators.required],
    autor: ['', Validators.required],
    seccionEspecial: [''],
    idCategoria: [0, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.categoriasSrv.listar().subscribe((c) => this.categorias.set(c));
    const param = this.route.snapshot.paramMap.get('id');
    if (param) {
      this.id = Number(param);
      this.libros.obtener(this.id).subscribe((l) => {
        this.form.patchValue({
          titulo: l.titulo,
          autor: l.autor,
          seccionEspecial: l.seccionEspecial ?? '',
          idCategoria: l.idCategoria,
        });
        this.vistaPrevia.set(l.imagen);
      });
    }
  }

  alElegirArchivo(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    const archivo = input.files?.[0] ?? null;
    if (archivo && archivo.size > 1024 * 1024) {
      this.alertas.error('La imagen supera 1 MB. Elige una más liviana.');
      input.value = '';
      return;
    }
    this.archivo = archivo;
    if (archivo) this.vistaPrevia.set(URL.createObjectURL(archivo));
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const datos = new FormData();
    datos.append('titulo', v.titulo);
    datos.append('autor', v.autor);
    if (v.seccionEspecial) datos.append('seccionEspecial', v.seccionEspecial);
    datos.append('idCategoria', String(v.idCategoria));
    if (this.archivo) datos.append('file', this.archivo);

    this.guardando.set(true);
    const peticion = this.id ? this.libros.actualizar(this.id, datos) : this.libros.crear(datos);
    peticion.subscribe({
      next: () => {
        this.alertas.ok(this.id ? 'Libro actualizado' : 'Libro agregado');
        this.router.navigate(['/libros']);
      },
      error: (_: HttpErrorResponse) => this.guardando.set(false),
    });
  }
}

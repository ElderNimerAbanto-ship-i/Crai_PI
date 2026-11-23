import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Categoria } from '../../core/interfaces/libro.interface';
import { Sala } from '../../core/interfaces/sala.interface';
import { EscuelaProf } from '../../core/interfaces/usuario.interface';
import { AlertService } from '../../core/services/alert.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { SalaService } from '../../core/services/sala.service';
import { UsuarioService } from '../../core/services/usuario.service';

type Pestana = 'categorias' | 'escuelas' | 'salas';

@Component({
  standalone: true,
  selector: 'app-configuracion',
  imports: [FormsModule],
  templateUrl: './configuracion.component.html',
  styleUrl: './configuracion.component.css',
})
export class ConfiguracionComponent implements OnInit {
  private readonly categoriasSrv = inject(CategoriaService);
  private readonly usuariosSrv = inject(UsuarioService);
  private readonly salasSrv = inject(SalaService);
  private readonly alertas = inject(AlertService);

  pestana = signal<Pestana>('categorias');
  categorias = signal<Categoria[]>([]);
  escuelas = signal<EscuelaProf[]>([]);
  salas = signal<Sala[]>([]);

  nuevaCategoria = '';
  nuevaEscuela = '';
  nuevaSala: Sala = { nombre: '', capacidadMin: 3, capacidadMax: 8, estado: 'DISPONIBLE' };

  ngOnInit(): void {
    this.cargarTodo();
  }

  cargarTodo(): void {
    this.categoriasSrv.listar().subscribe((c) => this.categorias.set(c));
    this.usuariosSrv.listarEscuelas().subscribe((e) => this.escuelas.set(e));
    this.salasSrv.listar().subscribe((s) => this.salas.set(s));
  }

  // ---- Categorías ----
  agregarCategoria(): void {
    const nombre = this.nuevaCategoria.trim();
    if (!nombre) return;
    this.categoriasSrv.crear({ nombre }).subscribe(() => {
      this.alertas.ok('Categoría agregada');
      this.nuevaCategoria = '';
      this.cargarTodo();
    });
  }

  renombrarCategoria(c: Categoria): void {
    const nombre = prompt('Nuevo nombre de la categoría', c.nombre)?.trim();
    if (!nombre || nombre === c.nombre) return;
    this.categoriasSrv.actualizar(c.idCategoria!, { nombre }).subscribe(() => {
      this.alertas.ok('Categoría actualizada');
      this.cargarTodo();
    });
  }

  eliminarCategoria(c: Categoria): void {
    if (!confirm(`¿Eliminar la categoría "${c.nombre}"?`)) return;
    this.categoriasSrv.eliminar(c.idCategoria!).subscribe(() => {
      this.alertas.ok('Categoría eliminada');
      this.cargarTodo();
    });
  }

  // ---- Escuelas ----
  agregarEscuela(): void {
    const nombreEsc = this.nuevaEscuela.trim();
    if (!nombreEsc) return;
    this.usuariosSrv.crearEscuela({ nombreEsc, estado: 'A' }).subscribe(() => {
      this.alertas.ok('Escuela agregada');
      this.nuevaEscuela = '';
      this.cargarTodo();
    });
  }

  alternarEscuela(e: EscuelaProf): void {
    this.usuariosSrv
      .actualizarEscuela(e.idEscuelaProf!, { nombreEsc: e.nombreEsc, estado: e.estado === 'A' ? 'I' : 'A' })
      .subscribe(() => this.cargarTodo());
  }

  eliminarEscuela(e: EscuelaProf): void {
    if (!confirm(`¿Eliminar la escuela "${e.nombreEsc}"?`)) return;
    this.usuariosSrv.eliminarEscuela(e.idEscuelaProf!).subscribe(() => {
      this.alertas.ok('Escuela eliminada');
      this.cargarTodo();
    });
  }

  // ---- Salas ----
  agregarSala(): void {
    const s = this.nuevaSala;
    if (!s.nombre.trim()) return;
    this.salasSrv
      .crear({ ...s, nombre: s.nombre.trim(), capacidadMin: Number(s.capacidadMin), capacidadMax: Number(s.capacidadMax) })
      .subscribe(() => {
        this.alertas.ok('Sala agregada');
        this.nuevaSala = { nombre: '', capacidadMin: 3, capacidadMax: 8, estado: 'DISPONIBLE' };
        this.cargarTodo();
      });
  }

  alternarSala(s: Sala): void {
    this.salasSrv
      .actualizar(s.idSala!, { ...s, estado: s.estado === 'DISPONIBLE' ? 'MANTENIMIENTO' : 'DISPONIBLE' })
      .subscribe(() => this.cargarTodo());
  }

  eliminarSala(s: Sala): void {
    if (!confirm(`¿Eliminar "${s.nombre}"?`)) return;
    this.salasSrv.eliminar(s.idSala!).subscribe(() => {
      this.alertas.ok('Sala eliminada');
      this.cargarTodo();
    });
  }
}

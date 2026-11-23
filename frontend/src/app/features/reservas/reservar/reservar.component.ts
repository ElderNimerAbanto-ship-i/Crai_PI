import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { mensajeDeError } from '../../../core/interceptors/error.interceptor';
import {
  ACTIVIDADES,
  ActividadAcademica,
  EstudianteValidado,
  HorarioSala,
  ReservaPublicaResultado,
} from '../../../core/interfaces/reserva.interface';
import { SalaEstado } from '../../../core/interfaces/sala.interface';
import { ReservaService } from '../../../core/services/reserva.service';
import { hora } from '../../../core/utils/format';

interface Persona {
  codigo: string;
  validado: EstudianteValidado | null;
  error: string;
  validando: boolean;
}

const vacia = (): Persona => ({ codigo: '', validado: null, error: '', validando: false });

@Component({
  standalone: true,
  selector: 'app-reservar',
  imports: [FormsModule, RouterLink],
  templateUrl: './reservar.component.html',
  styleUrl: './reservar.component.css',
})
export class ReservarComponent {
  private readonly srv = inject(ReservaService);

  readonly MIN = 3;
  readonly MAX = 8;
  actividades = ACTIVIDADES;
  duraciones = [60, 90, 120];
  hora = hora;

  paso = signal<1 | 2 | 3>(1);
  // personas[0] es el responsable; el resto, los integrantes
  personas = signal<Persona[]>([vacia(), vacia(), vacia()]);
  duracion = signal(60);
  actividad: ActividadAcademica | '' = '';
  aceptaReglas = false;
  salasAbiertas = signal(false);
  salas = signal<SalaEstado[]>([]);
  salaSel = signal<SalaEstado | null>(null);
  horarios = signal<HorarioSala[]>([]);
  horarioSel = signal<HorarioSala | null>(null);
  cargandoHorarios = signal(false);
  enviando = signal(false);
  resultado = signal<ReservaPublicaResultado | null>(null);

  todosValidados = computed(() => this.personas().every((p) => p.validado) && !this.hayRepetidos());

  hayRepetidos(): boolean {
    const codigos = this.personas().map((p) => p.codigo.trim().toLowerCase()).filter(Boolean);
    return new Set(codigos).size !== codigos.length;
  }

  etiqueta(i: number): string {
    return i === 0 ? 'Responsable' : `Integrante ${i}`;
  }

  actualizar(i: number, cambios: Partial<Persona>): void {
    this.personas.update((l) => l.map((p, k) => (k === i ? { ...p, ...cambios } : p)));
  }

  alEscribir(i: number, valor: string): void {
    this.actualizar(i, { codigo: valor, validado: null, error: '' });
  }

  validar(i: number): void {
    const codigo = this.personas()[i].codigo.trim();
    if (!codigo) return;
    const repetidos = this.personas().filter((p, k) => k !== i && p.codigo.trim().toLowerCase() === codigo.toLowerCase());
    if (repetidos.length) {
      this.actualizar(i, { error: 'Este código ya está en la lista.', validado: null });
      return;
    }
    this.actualizar(i, { validando: true, error: '' });
    this.srv.validarEstudiante(codigo).subscribe({
      next: (e) => this.actualizar(i, { validado: e, validando: false }),
      error: (err: HttpErrorResponse) => this.actualizar(i, { error: mensajeDeError(err), validando: false, validado: null }),
    });
  }

  agregar(): void {
    if (this.personas().length < this.MAX) this.personas.update((l) => [...l, vacia()]);
  }

  quitar(i: number): void {
    if (this.personas().length > this.MIN) this.personas.update((l) => l.filter((_, k) => k !== i));
  }

  irAPaso2(): void {
    if (!this.todosValidados()) return;
    this.paso.set(2);
    this.cargarSalas();
  }

  // ---- Salas y horarios ----
  cargarSalas(): void {
    this.srv.estadoSalas().subscribe({ next: (s) => this.salas.set(s) });
  }

  alternarSalas(): void {
    this.salasAbiertas.update((v) => !v);
    if (this.salasAbiertas()) this.cargarSalas();
  }

  admiteGrupo(s: SalaEstado): boolean {
    const n = this.personas().length;
    return s.estado !== 'MANTENIMIENTO' && n >= s.capacidadMin && n <= s.capacidadMax;
  }

  textoSala(s: SalaEstado): string {
    if (s.estado === 'MANTENIMIENTO') return 'En mantenimiento';
    if (!this.admiteGrupo(s)) return `Admite de ${s.capacidadMin} a ${s.capacidadMax} personas`;
    return `De ${s.capacidadMin} a ${s.capacidadMax} personas`;
  }

  elegirSala(s: SalaEstado): void {
    if (!this.admiteGrupo(s)) return;
    this.salaSel.set(s);
    this.horarioSel.set(null);
    this.cargarHorarios();
  }

  cargarHorarios(): void {
    const sala = this.salaSel();
    if (!sala) return;
    this.cargandoHorarios.set(true);
    this.srv.horarios(sala.idSala, this.duracion()).subscribe({
      next: (h) => {
        this.horarios.set(h);
        this.cargandoHorarios.set(false);
      },
      error: () => this.cargandoHorarios.set(false),
    });
  }

  cambiarDuracion(min: number): void {
    this.duracion.set(min);
    this.horarioSel.set(null);
    this.cargarHorarios();
  }

  elegirHorario(h: HorarioSala): void {
    if (h.estado === 'LIBRE') this.horarioSel.set(h);
  }

  textoHorario(h: HorarioSala): string {
    switch (h.estado) {
      case 'LIBRE': return 'Libre';
      case 'OCUPADO': return 'Ocupado';
      case 'PASADO': return 'Ya comenzó';
      default: return 'No disponible';
    }
  }

  // Método (no computed): lee campos de ngModel que no son signals
  puedeConfirmar(): boolean {
    return !!this.salaSel() && !!this.horarioSel() && !!this.actividad && this.aceptaReglas && !this.enviando();
  }

  confirmar(): void {
    const sala = this.salaSel();
    const horario = this.horarioSel();
    if (!this.puedeConfirmar() || !this.actividad || !sala || !horario) return;
    const [responsable, ...resto] = this.personas();
    this.enviando.set(true);
    this.srv
      .reservar({
        idSala: sala.idSala,
        horaInicio: horario.horaInicio,
        codigoResponsable: responsable.codigo.trim(),
        codigosIntegrantes: resto.map((p) => p.codigo.trim()),
        duracionMinutos: this.duracion(),
        actividad: this.actividad,
        aceptaReglas: this.aceptaReglas,
      })
      .subscribe({
        next: (r) => {
          this.resultado.set(r);
          this.paso.set(3);
          this.enviando.set(false);
        },
        error: () => {
          // Probablemente otro grupo tomó el horario: se vuelve a consultar
          this.enviando.set(false);
          this.horarioSel.set(null);
          this.cargarHorarios();
        },
      });
  }

  nueva(): void {
    this.personas.set([vacia(), vacia(), vacia()]);
    this.actividad = '';
    this.aceptaReglas = false;
    this.salaSel.set(null);
    this.horarioSel.set(null);
    this.horarios.set([]);
    this.salasAbiertas.set(false);
    this.resultado.set(null);
    this.duracion.set(60);
    this.paso.set(1);
  }
}
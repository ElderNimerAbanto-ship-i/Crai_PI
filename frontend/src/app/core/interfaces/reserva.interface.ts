export type ActividadAcademica =
  | 'INVESTIGACION'
  | 'MATEMATICAS'
  | 'EXPOSICION'
  | 'ESTUDIO_GRUPAL'
  | 'TRABAJOS'
  | 'EXAMENES';

export const ACTIVIDADES: { valor: ActividadAcademica; texto: string }[] = [
  { valor: 'INVESTIGACION', texto: 'Investigación académica' },
  { valor: 'MATEMATICAS', texto: 'Práctica de matemáticas y resolución de ejercicios' },
  { valor: 'EXPOSICION', texto: 'Preparación y práctica de una exposición' },
  { valor: 'ESTUDIO_GRUPAL', texto: 'Estudio grupal y repaso de cursos' },
  { valor: 'TRABAJOS', texto: 'Desarrollo de trabajos académicos' },
  { valor: 'EXAMENES', texto: 'Preparación para exámenes' },
];

export interface ReservaSala {
  idReservaSala: number;
  fechaReserva: string;
  horaInicio: string;
  horaFin: string;
  estado: 'A' | 'C';
  idSala: number;
  nombreSala: string;
  totalIntegrantes: number;
  codigoResponsable: string | null;
  nombreResponsable: string | null;
  actividad: ActividadAcademica | null;
  duracionMinutos: number | null;
}

export interface EstudianteValidado {
  codigoUni: string;
  nombrePersona: string;
  escuela: string | null;
}

export interface Disponibilidad {
  disponible: boolean;
  idSala: number | null;
  nombreSala: string | null;
  horaInicio: string;
  horaFin: string;
  mensaje: string;
}

export interface ReservaPublica {
  codigoResponsable: string;
  codigosIntegrantes: string[];
  duracionMinutos: number;
  actividad: ActividadAcademica;
  aceptaReglas: boolean;
}

export interface ReservaPublicaResultado {
  idReservaSala: number;
  nombreSala: string;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  duracionMinutos: number;
  actividad: ActividadAcademica;
  responsable: EstudianteValidado;
  integrantes: EstudianteValidado[];
  mensaje: string;
}
export interface HorarioSala {
  horaInicio: string;
  horaFin: string;
  estado: 'LIBRE' | 'OCUPADO' | 'PASADO' | 'MANTENIMIENTO';
}

export interface ReservaPublica {
  idSala: number;
  horaInicio: string;
  codigoResponsable: string;
}
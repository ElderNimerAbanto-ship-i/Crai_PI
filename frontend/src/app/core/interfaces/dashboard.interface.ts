export interface UsoEscuela {
  escuela: string;
  ingresos: number;
  prestamos: number;
  reservas: number;
  total: number;
}

export interface UsoSala {
  idSala: number;
  sala: string;
  reservas: number;
  horas: number;
}

// diaSemana: 1 = lunes ... 7 = domingo
export interface HorarioUso {
  diaSemana: number;
  hora: number;
  reservas: number;
}

// periodo: "2026-11"
export interface PrestamoMes {
  periodo: string;
  libros: number;
}
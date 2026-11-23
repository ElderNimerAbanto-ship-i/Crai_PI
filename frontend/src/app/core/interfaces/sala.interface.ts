export interface Sala {
  idSala?: number;
  nombre: string;
  capacidadMin: number;
  capacidadMax: number;
  estado: 'DISPONIBLE' | 'MANTENIMIENTO';
}

export interface SalaEstado {
  idSala: number;
  nombre: string;
  capacidadMin: number;
  capacidadMax: number;
  estado: 'LIBRE' | 'OCUPADA' | 'MANTENIMIENTO';
  ocupadaHasta: string | null;
}

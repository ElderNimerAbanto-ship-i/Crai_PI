export interface Categoria {
  idCategoria?: number;
  nombre: string;
}

export interface Libro {
  idLibro: number;
  titulo: string;
  autor: string;
  seccionEspecial: string | null;
  idCategoria: number;
  nombreCategoria: string;
  imagen: string | null;
  ejemplaresDisponibles: number;
}

export type EstadoEjemplar = 'DISPONIBLE' | 'PRESTADO' | 'MANTENIMIENTO';

export interface Ejemplar {
  idEjemplar?: number;
  estado: EstadoEjemplar;
  isbn: string | null;
  idLibro: number;
  tituloLibro?: string;
}

export interface Prestamo {
  idPrestamo: number;
  fechaPrestamo: string;
  fechaVencimiento: string;
  fechaDevolucion: string | null;
  estado: 'A' | 'D';
  idPersona: number;
  nombrePersona: string;
}

export interface PrestamoRegistro {
  codigoUni: string;
  idEjemplar: number;
  dias: number;
}

export interface DetallePrestamo {
  idDetalle: number;
  idPrestamo: number;
  idEjemplar: number;
  isbnEjemplar: string | null;
  tituloLibro: string;
}
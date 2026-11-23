export interface Resumen {
  totalLibros: number;
  totalEjemplares: number;
  ejemplaresDisponibles: number;
  totalUsuarios: number;
  prestamosActivos: number;
  prestamosVencidos: number;
  reservasHoy: number;
  ingresosHoy: number;
}

export interface LibroPrestado {
  idLibro: number;
  titulo: string;
  totalPrestamos: number;
}

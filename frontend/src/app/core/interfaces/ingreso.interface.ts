export type TipoAcceso = 'ENTRADA' | 'SALIDA';

export interface Ingreso {
  idIngreso: number;
  fechaHora: string;
  tipoAcceso: TipoAcceso;
  codigoUni: string;
  nombrePersona: string;
  escuela: string | null;
}
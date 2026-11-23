export type Rol = 'ADMIN' | 'ESTUDIANTE' | 'DOCENTE';

export interface EscuelaProf {
  idEscuelaProf?: number;
  nombreEsc: string;
  estado: 'A' | 'I';
}

export interface Usuario {
  idPersona: number;
  username: string;
  rol: Rol;
  codigoUni: string;
  nombrePersona: string;
  email: string;
  accesoHabilitado: boolean;
  nombreEscuela: string | null;
}

export interface UsuarioRegistro {
  nombres: string;
  apellidos: string;
  email: string;
  codigoUni: string;
  username: string;
  password: string;
  rol: Rol;
  idEscuelaProf: number | null;
}

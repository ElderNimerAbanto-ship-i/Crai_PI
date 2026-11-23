import { Rol } from './usuario.interface';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  rol: Rol;
  idPersona: number;
  nombrePersona: string;
}
import { Injectable, signal } from '@angular/core';

export type AlertTipo = 'ok' | 'error' | 'info';

export interface Alerta {
  id: number;
  tipo: AlertTipo;
  texto: string;
}

@Injectable({ providedIn: 'root' })
export class AlertService {
  private readonly _alertas = signal<Alerta[]>([]);
  readonly alertas = this._alertas.asReadonly();
  private contador = 0;

  ok(texto: string): void {
    this.mostrar('ok', texto);
  }

  error(texto: string): void {
    this.mostrar('error', texto);
  }

  info(texto: string): void {
    this.mostrar('info', texto);
  }

  cerrar(id: number): void {
    this._alertas.update((lista) => lista.filter((a) => a.id !== id));
  }

  private mostrar(tipo: AlertTipo, texto: string): void {
    const id = ++this.contador;
    this._alertas.update((lista) => [...lista, { id, tipo, texto }]);
    setTimeout(() => this.cerrar(id), tipo === 'error' ? 7000 : 4000);
  }
}

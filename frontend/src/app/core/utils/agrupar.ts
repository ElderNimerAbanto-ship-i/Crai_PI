import { signal } from '@angular/core';

// Un grupo = una persona con todos sus registros
export interface Grupo<T> {
  clave: string;
  titulo: string;
  subtitulo: string;
  items: T[];
}

export function normalizar(t: string | null | undefined): string {
  return (t ?? '').trim().toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
}

/**
 * Agrupa una lista por persona.
 * - Los registros de cada grupo quedan del más reciente al más antiguo.
 * - Los grupos se ordenan por su actividad más reciente.
 */
export function agrupar<T>(
  lista: T[],
  clave: (x: T) => string,
  titulo: (x: T) => string,
  subtitulo: (x: T) => string,
  fecha: (x: T) => string,
): Grupo<T>[] {
  const mapa = new Map<string, Grupo<T>>();
  for (const x of lista) {
    const k = clave(x);
    const g = mapa.get(k) ?? { clave: k, titulo: titulo(x), subtitulo: subtitulo(x), items: [] };
    g.items.push(x);
    mapa.set(k, g);
  }
  const grupos = [...mapa.values()];
  grupos.forEach((g) => g.items.sort((a, b) => fecha(b).localeCompare(fecha(a))));
  return grupos.sort((a, b) => fecha(b.items[0]).localeCompare(fecha(a.items[0])));
}

// Estado de filas desplegadas + texto de búsqueda, reutilizable en cualquier tabla agrupada
export class TablaAgrupada {
  readonly abiertos = signal<ReadonlySet<string>>(new Set());
  readonly filtro = signal('');

  estaAbierto(clave: string): boolean {
    return this.abiertos().has(clave);
  }

  alternar(clave: string): void {
    this.abiertos.update((s) => {
      const nuevo = new Set(s);
      if (!nuevo.delete(clave)) nuevo.add(clave);
      return nuevo;
    });
  }

  abrirTodos(grupos: Grupo<unknown>[]): void {
    this.abiertos.set(new Set(grupos.map((g) => g.clave)));
  }

  cerrarTodos(): void {
    this.abiertos.set(new Set());
  }

  // Filtra por nombre o código (sin tildes ni mayúsculas)
  filtrar<T>(grupos: Grupo<T>[]): Grupo<T>[] {
    const q = normalizar(this.filtro());
    if (!q) return grupos;
    return grupos.filter((g) => normalizar(g.titulo).includes(q) || normalizar(g.subtitulo).includes(q));
  }
}
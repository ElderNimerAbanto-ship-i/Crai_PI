export function hora(valor: string | null | undefined): string {
  return valor ? valor.slice(0, 5) : '';
}

export function fechaHora(valor: string | null | undefined): string {
  if (!valor) return '';
  const d = new Date(valor);
  return d.toLocaleString('es-PE', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

export function fecha(valor: string | null | undefined): string {
  if (!valor) return '';
  const [y, m, d] = valor.slice(0, 10).split('-');
  return `${d}/${m}/${y}`;
}

export function estaVencido(vencimiento: string, estado: string): boolean {
  return estado === 'A' && new Date(vencimiento).getTime() < Date.now();
}

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HorarioUso, PrestamoMes, UsoEscuela, UsoSala } from '../../core/interfaces/dashboard.interface';
import { Resumen } from '../../core/interfaces/reporte.interface';
import { ReporteService } from '../../core/services/reporte.service';

type Periodo = 'hoy' | 'mes' | '30' | '90' | 'anio' | 'personalizado';
type Metrica = 'ingresos' | 'prestamos' | 'reservas';

const DIAS = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
const DIAS_LARGO = ['lunes', 'martes', 'miércoles', 'jueves', 'viernes', 'sábado', 'domingo'];
const HORAS = Array.from({ length: 13 }, (_, i) => i + 8); // 08:00 a 20:00 (el CRAI cierra a las 21:00)

// yyyy-MM-dd con la fecha local (toISOString usaría UTC y puede cambiar el día)
function iso(f: Date): string {
  const p = (n: number) => String(n).padStart(2, '0');
  return `${f.getFullYear()}-${p(f.getMonth() + 1)}-${p(f.getDate())}`;
}

@Component({
  standalone: true,
  selector: 'app-dashboard',
  imports: [FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  private readonly srv = inject(ReporteService);

  readonly dias = DIAS;
  readonly horas = HORAS;
  readonly periodos: { valor: Periodo; texto: string }[] = [
    { valor: 'hoy', texto: 'Hoy' },
    { valor: '30', texto: '30 días' },
    { valor: '90', texto: '90 días' },
    { valor: 'mes', texto: 'Este mes' },
    { valor: 'anio', texto: 'Este año' },
    { valor: 'personalizado', texto: 'Personalizado' },
  ];
  readonly metricas: { valor: Metrica; texto: string }[] = [
    { valor: 'ingresos', texto: 'Ingresos' },
    { valor: 'prestamos', texto: 'Préstamos' },
    { valor: 'reservas', texto: 'Reservas de sala' },
  ];

  // Filtro de fechas compartido por los gráficos de carrera y salas
  periodo = signal<Periodo>('30');
  desde = signal('');
  hasta = signal('');
  metrica = signal<Metrica>('ingresos');

  resumen = signal<Resumen | null>(null);
  escuelas = signal<UsoEscuela[] | null>(null);
  salas = signal<UsoSala[] | null>(null);
  horarios = signal<HorarioUso[] | null>(null);
  meses = signal<PrestamoMes[] | null>(null);
  error = signal(false);

  // ---- Carrera: barras horizontales según la métrica elegida ----
  filasEscuela = computed(() => {
    const m = this.metrica();
    const filas = [...(this.escuelas() ?? [])].filter((e) => e[m] > 0).sort((a, b) => b[m] - a[m]);
    const max = filas[0]?.[m] ?? 0;
    return filas.map((e) => ({
      nombre: e.escuela,
      valor: e[m],
      pct: max ? (e[m] / max) * 100 : 0,
      sinEscuela: e.escuela === 'Sin escuela',
    }));
  });

  // ---- Salas: reservas y horas por sala ----
  filasSala = computed(() => {
    const lista = this.salas() ?? [];
    const max = Math.max(0, ...lista.map((s) => s.reservas));
    return lista.map((s) => ({ ...s, pct: max ? (s.reservas / max) * 100 : 0 }));
  });

  // ---- Mapa de calor: día de la semana × hora ----
  mapa = computed(() => {
    const datos = this.horarios() ?? [];
    const valores = new Map(datos.map((d) => [d.diaSemana * 100 + d.hora, d.reservas]));
    const max = Math.max(0, ...datos.map((d) => d.reservas));
    return HORAS.map((hora) => ({
      hora,
      celdas: DIAS.map((_, i) => {
        const valor = valores.get((i + 1) * 100 + hora) ?? 0;
        return { valor, intensidad: max ? valor / max : 0, dia: DIAS_LARGO[i] };
      }),
    }));
  });

  horaPico = computed(() => {
    const datos = this.horarios() ?? [];
    if (!datos.length) return null;
    const pico = datos.reduce((a, b) => (b.reservas > a.reservas ? b : a));
    const h = (n: number) => `${String(n).padStart(2, '0')}:00`;
    return { dia: DIAS_LARGO[pico.diaSemana - 1], desde: h(pico.hora), hasta: h(pico.hora + 1), reservas: pico.reservas };
  });

  // ---- Préstamos por mes: columnas ----
  columnas = computed(() => {
    const lista = this.meses() ?? [];
    const max = Math.max(0, ...lista.map((m) => m.libros));
    return lista.map((m) => {
      const [anio, mes] = m.periodo.split('-').map(Number);
      const nombre = new Date(anio, mes - 1, 1).toLocaleDateString('es-PE', { month: 'short' }).replace('.', '');
      return { etiqueta: `${nombre} ${String(anio).slice(2)}`, corta: nombre, anio: `’${String(anio).slice(2)}`, libros: m.libros, pct: max ? (m.libros / max) * 100 : 0 };
    });
  });

  // Mes resaltado en el gráfico de columnas: el que se toca o, por defecto, el de más préstamos
  mesSel = signal<number | null>(null);
  mesActivo = computed(() => {
    const columnas = this.columnas();
    const elegido = this.mesSel();
    if (elegido !== null && elegido < columnas.length) return elegido;
    let mejor = -1;
    let max = 0;
    columnas.forEach((c, i) => {
      if (c.libros > max) { max = c.libros; mejor = i; }
    });
    return mejor;
  });

  totalReservasSalas = computed(() => (this.salas() ?? []).reduce((suma, s) => suma + s.reservas, 0));

  pctEjemplares = computed(() => {
    const r = this.resumen();
    return r && r.totalEjemplares ? (r.ejemplaresDisponibles / r.totalEjemplares) * 100 : 0;
  });

  totalPrestados = computed(() => (this.meses() ?? []).reduce((suma, m) => suma + m.libros, 0));

  ngOnInit(): void {
    this.aplicarPeriodo('30');
    this.cargarFijos();
  }

  // Vuelve a pedir todos los datos al backend
  recargar(): void {
    if (this.periodo() === 'personalizado') this.cargarRango();
    else this.aplicarPeriodo(this.periodo());
    this.cargarFijos();
  }

  // Datos que no dependen del filtro de fechas
  private cargarFijos(): void {
    this.srv.resumen().subscribe({ next: (r) => this.resumen.set(r) });
    this.srv.prestamosPorMes(12).subscribe({ next: (m) => this.meses.set(m) });
  }

  aplicarPeriodo(p: Periodo): void {
    this.periodo.set(p);
    if (p === 'personalizado') return;
    const hoy = new Date();
    const inicio = new Date(hoy);
    if (p === 'hoy') { /* desde = hasta = hoy */ }
    else if (p === 'mes') inicio.setDate(1);
    else if (p === '30') inicio.setDate(hoy.getDate() - 29);
    else if (p === '90') inicio.setDate(hoy.getDate() - 89);
    else inicio.setMonth(0, 1);
    this.desde.set(iso(inicio));
    this.hasta.set(iso(hoy));
    this.cargarRango();
  }

  // Se llama al cambiar las fechas a mano
  alCambiarFechas(): void {
    if (this.desde() && this.hasta() && this.desde() <= this.hasta()) this.cargarRango();
  }

  rangoInvalido(): boolean {
    return !!this.desde() && !!this.hasta() && this.desde() > this.hasta();
  }

  private cargarRango(): void {
    const d = this.desde();
    const h = this.hasta();
    this.error.set(false);
    this.escuelas.set(null);
    this.salas.set(null);
    this.horarios.set(null);
    const alFallar = () => {
      this.error.set(true);
      this.escuelas.update((v) => v ?? []);
      this.salas.update((v) => v ?? []);
      this.horarios.update((v) => v ?? []);
    };
    this.srv.usoPorEscuela(d, h).subscribe({ next: (r) => this.escuelas.set(r), error: alFallar });
    this.srv.usoSalas(d, h).subscribe({ next: (r) => this.salas.set(r), error: alFallar });
    this.srv.usoHorarios(d, h).subscribe({ next: (r) => this.horarios.set(r), error: alFallar });
  }
}
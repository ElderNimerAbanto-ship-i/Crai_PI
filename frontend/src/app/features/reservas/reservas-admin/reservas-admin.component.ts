import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReservaSala } from '../../../core/interfaces/reserva.interface';
import { AlertService } from '../../../core/services/alert.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { fecha, hora } from '../../../core/utils/format';
import { Grupo, TablaAgrupada, agrupar } from '../../../core/utils/agrupar';

@Component({
  standalone: true,
  selector: 'app-reservas-admin',
  imports: [FormsModule],
  templateUrl: './reservas-admin.component.html',
  styleUrl: './reservas-admin.component.css',
})
export class ReservasAdminComponent implements OnInit {
  private readonly srv = inject(ReservaService);
  private readonly alertas = inject(AlertService);

  lista = signal<ReservaSala[]>([]);
  cargando = signal(true);
  fecha = fecha;
  hora = hora;

  // ---- Tabla agrupada por responsable ----
  readonly tabla = new TablaAgrupada();
  grupos = computed(() =>
    agrupar(
      this.lista(),
      (r) => r.codigoResponsable ?? r.nombreResponsable ?? 'sin-responsable',
      (r) => r.nombreResponsable ?? 'Sin responsable',
      (r) => r.codigoResponsable ?? '',
      // fecha + hora para ordenar de la más reciente a la más antigua
      (r) => `${r.fechaReserva}T${r.horaInicio}`,
    ),
  );
  visibles = computed(() => this.tabla.filtrar(this.grupos()));

  activas(g: Grupo<ReservaSala>): number {
    return g.items.filter((r) => r.estado === 'A').length;
  }

  canceladas(g: Grupo<ReservaSala>): number {
    return g.items.filter((r) => r.estado === 'C').length;
  }

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.srv.listar().subscribe({
      next: (r) => {
        this.lista.set(r);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  cancelar(r: ReservaSala): void {
    if (!confirm(`¿Cancelar la reserva de ${r.nombreSala}?`)) return;
    this.srv.cancelar(r.idReservaSala).subscribe(() => {
      this.alertas.ok('Reserva cancelada');
      this.cargar();
    });
  }

  eliminar(r: ReservaSala): void {
    if (!confirm('¿Eliminar esta reserva del historial?')) return;
    this.srv.eliminar(r.idReservaSala).subscribe(() => {
      this.alertas.ok('Reserva eliminada');
      this.cargar();
    });
  }
}
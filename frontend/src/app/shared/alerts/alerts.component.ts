import { Component, inject } from '@angular/core';
import { AlertService } from '../../core/services/alert.service';

@Component({
  standalone: true,
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrl: './alerts.component.css',
})
export class AlertsComponent {
  readonly alertas = inject(AlertService);
}

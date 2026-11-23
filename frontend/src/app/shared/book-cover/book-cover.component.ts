import { Component, computed, input } from '@angular/core';

// Portada del libro: usa la imagen subida o genera una portada tipográfica a partir del título
@Component({
  standalone: true,
  selector: 'app-book-cover',
  templateUrl: './book-cover.component.html',
  styleUrl: './book-cover.component.css',
})
export class BookCoverComponent {
  titulo = input.required<string>();
  autor = input<string>('');
  imagen = input<string | null>(null);

  tono = computed(() => {
    let h = 0;
    for (const c of this.titulo()) h = (h * 31 + c.charCodeAt(0)) % 360;
    // Tonos apagados de tela de encuadernación
    const base = [150, 205, 20, 280, 40, 340, 175, 100];
    return base[h % base.length];
  });
}

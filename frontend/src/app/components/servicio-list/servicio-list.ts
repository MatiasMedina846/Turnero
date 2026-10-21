import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServicioService } from '../../services/servicio.service';
import { Servicio } from '../../models/models';

@Component({
  selector: 'app-servicio-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './servicio-list.html',
})
export class ServicioList implements OnInit {
  servicios: Servicio[] = [];
  nuevo: Servicio = { nombre: '', duracionMinutos: 30 };

  // Zoneless app: flag async state changes with markForCheck() so the view refreshes.
  constructor(
    private service: ServicioService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.service.listar().subscribe({
      next: (data) => {
        this.servicios = data;
        this.cdr.markForCheck();
      },
    });
  }

  crear() {
    if (!this.nuevo.nombre || !this.nuevo.duracionMinutos) return;
    this.service.crear(this.nuevo).subscribe(() => {
      this.nuevo = { nombre: '', duracionMinutos: 30 };
      this.cargar();
      this.cdr.markForCheck();
    });
  }
}

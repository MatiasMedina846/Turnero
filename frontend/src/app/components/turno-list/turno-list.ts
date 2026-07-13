import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TurnoService } from '../../services/turno.service';
import { TurnoResponse } from '../../models/models';

@Component({
  selector: 'app-turno-list',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './turno-list.html',
})
export class TurnoList implements OnInit {

  turnos: TurnoResponse[] = [];
  cargando = true;

  constructor(private service: TurnoService) {}

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.cargando = true;
    this.service.listar().subscribe({
      next: data => {
        this.turnos = data;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      },
    });
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar este turno?')) return;
    this.service.eliminar(id).subscribe({
      next: () => {
        this.turnos = this.turnos.filter(t => t.id !== id);
      },
    });
  }
}

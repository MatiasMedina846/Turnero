import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmpleadoService } from '../../services/empleado.service';
import { Empleado } from '../../models/models';

@Component({
  selector: 'app-empleado-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './empleado-list.html',
})
export class EmpleadoList implements OnInit {

  empleados: Empleado[] = [];
  nuevo: Empleado = { nombre: '', especialidad: '' };

  constructor(private service: EmpleadoService) {}

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.service.listar().subscribe({
      next: data => this.empleados = data,
    });
  }

  crear() {
    if (!this.nuevo.nombre || !this.nuevo.especialidad) return;
    this.service.crear(this.nuevo).subscribe(() => {
      this.nuevo = { nombre: '', especialidad: '' };
      this.cargar();
    });
  }
}

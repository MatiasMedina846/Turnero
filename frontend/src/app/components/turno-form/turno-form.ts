import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EmpleadoService } from '../../services/empleado.service';
import { ServicioService } from '../../services/servicio.service';
import { TurnoService } from '../../services/turno.service';
import { Empleado, Servicio, TurnoRequest } from '../../models/models';

@Component({
  selector: 'app-turno-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './turno-form.html',
})
export class TurnoForm implements OnInit {

  empleados: Empleado[] = [];
  servicios: Servicio[] = [];
  editando = false;
  turnoId?: number;

  request: TurnoRequest = {
    clienteNombre: '',
    clienteTelefono: '',
    empleadoId: 0,
    servicioId: 0,
    fechaHoraInicio: '',
  };

  mensaje = '';
  error = '';

  constructor(
    private empleadoService: EmpleadoService,
    private servicioService: ServicioService,
    private turnoService: TurnoService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit() {
    this.empleadoService.listar().subscribe(data => this.empleados = data);
    this.servicioService.listar().subscribe(data => this.servicios = data);

    const id = this.route.snapshot.params['id'];
    if (id) {
      this.editando = true;
      this.turnoId = +id;
      this.turnoService.obtener(this.turnoId).subscribe(t => {
        this.request.clienteNombre = t.clienteNombre;
        this.request.clienteTelefono = t.clienteTelefono;
        this.request.empleadoId = t.empleadoId;
        this.request.servicioId = t.servicioId;
        this.request.fechaHoraInicio = t.fechaHoraInicio.substring(0, 16);
      });
    }
  }

  guardar() {
    this.mensaje = '';
    this.error = '';

    if (!this.request.clienteNombre || !this.request.empleadoId || !this.request.servicioId || !this.request.fechaHoraInicio) {
      this.error = 'Completa todos los campos obligatorios.';
      return;
    }

    const obs = this.editando
      ? this.turnoService.actualizar(this.turnoId!, this.request)
      : this.turnoService.crear(this.request);

    obs.subscribe({
      next: () => {
        this.mensaje = this.editando ? 'Turno actualizado con éxito.' : 'Turno reservado con éxito.';
        this.request = { clienteNombre: '', clienteTelefono: '', empleadoId: 0, servicioId: 0, fechaHoraInicio: '' };
      },
      error: (err) => {
        this.error = err.message;
      },
    });
  }
}

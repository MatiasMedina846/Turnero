import { Routes } from '@angular/router';
import { EmpleadoList } from './components/empleado-list/empleado-list';
import { ServicioList } from './components/servicio-list/servicio-list';
import { TurnoForm } from './components/turno-form/turno-form';
import { TurnoList } from './components/turno-list/turno-list';

export const routes: Routes = [
  { path: '', redirectTo: '/turnos', pathMatch: 'full' },
  { path: 'turnos', component: TurnoList, title: 'Agenda' },
  { path: 'nuevo-turno', component: TurnoForm, title: 'Nuevo Turno' },
  { path: 'editar-turno/:id', component: TurnoForm, title: 'Editar Turno' },
  { path: 'empleados', component: EmpleadoList, title: 'Empleados' },
  { path: 'servicios', component: ServicioList, title: 'Servicios' },
];

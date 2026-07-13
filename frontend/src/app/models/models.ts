export interface Empleado {
  id?: number;
  nombre: string;
  especialidad: string;
}

export interface Servicio {
  id?: number;
  nombre: string;
  duracionMinutos: number;
}

export interface TurnoRequest {
  clienteNombre: string;
  clienteTelefono?: string;
  empleadoId: number;
  servicioId: number;
  fechaHoraInicio: string;
}

export interface TurnoResponse {
  id: number;
  clienteNombre: string;
  clienteTelefono: string;
  empleadoId: number;
  empleadoNombre: string;
  servicioId: number;
  servicioNombre: string;
  fechaHoraInicio: string;
  fechaHoraFin: string;
  estado: string;
}

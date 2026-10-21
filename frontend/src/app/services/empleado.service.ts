import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Empleado } from '../models/models';
import { API_BASE } from '../api.config';

@Injectable({ providedIn: 'root' })
export class EmpleadoService {
  private api = `${API_BASE}/api/empleados`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(this.api);
  }

  crear(empleado: Empleado): Observable<Empleado> {
    return this.http.post<Empleado>(this.api, empleado);
  }
}

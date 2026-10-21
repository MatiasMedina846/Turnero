import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Servicio } from '../models/models';
import { API_BASE } from '../api.config';

@Injectable({ providedIn: 'root' })
export class ServicioService {
  private api = `${API_BASE}/api/servicios`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(this.api);
  }

  crear(servicio: Servicio): Observable<Servicio> {
    return this.http.post<Servicio>(this.api, servicio);
  }
}

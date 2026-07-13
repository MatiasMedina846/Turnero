import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { TurnoRequest, TurnoResponse } from '../models/models';

@Injectable({ providedIn: 'root' })
export class TurnoService {

  private api = '/api/turnos';

  constructor(private http: HttpClient) {}

  listar(): Observable<TurnoResponse[]> {
    return this.http.get<TurnoResponse[]>(this.api);
  }

  obtener(id: number): Observable<TurnoResponse> {
    return this.http.get<TurnoResponse>(`${this.api}/${id}`);
  }

  crear(request: TurnoRequest): Observable<TurnoResponse> {
    return this.http.post<TurnoResponse>(this.api, request).pipe(
      catchError((err: HttpErrorResponse) => {
        const msg = err.error?.error || 'Error al crear el turno';
        return throwError(() => new Error(msg));
      })
    );
  }

  actualizar(id: number, request: TurnoRequest): Observable<TurnoResponse> {
    return this.http.put<TurnoResponse>(`${this.api}/${id}`, request).pipe(
      catchError((err: HttpErrorResponse) => {
        const msg = err.error?.error || 'Error al actualizar el turno';
        return throwError(() => new Error(msg));
      })
    );
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}

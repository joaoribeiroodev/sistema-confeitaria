import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = `${environment.apiUrl}/pedidos`;

  constructor(private http: HttpClient) { }

  enviarPedido(pedido: any): Observable<any> {
    return this.http.post(this.baseUrl, pedido);
  }

  validarData(data: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/validar-data?data=${data}`);
  }

  validarHorario(data: string, horario: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/validar-horario?data=${data}&horario=${horario}`);
  }

  verificarHorario(data: string, horario: string) {
  // Passamos o horário com ":00" no final para o backend entender como LocalTime (HH:mm:ss)
  return this.http.get<boolean>(`${this.baseUrl}/verificar-horario?data=${data}&horario=${horario}:00`);
}
}


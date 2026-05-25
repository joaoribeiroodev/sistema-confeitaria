import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = `${environment.apiUrl}/pedidos`;

  constructor(private http: HttpClient) {}

  enviarPedido(pedido: any): Observable<any> {
    return this.http.post(this.baseUrl, pedido);
  }

  validarData(data: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/validar-data?data=${data}`);
  }
}


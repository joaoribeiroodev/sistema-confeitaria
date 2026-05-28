import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;
  private authUrl = `${environment.apiUrl}/auth`;
  private pedidosUrl = `${environment.apiUrl}/pedidos`; 

  constructor(private http: HttpClient) {}

  login(credentials: { username: string; password: string }): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.authUrl}/login`, credentials);
  }

  getMetricas(): Observable<any> {
    return this.http.get(`${this.apiUrl}/metricas`);
  }

  getHistorico(page: number, size: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/historico?page=${page}&size=${size}&sort=id,desc`);
  }

  exportarCsv(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/exportar-csv`, { responseType: 'blob' });
  }

  getTodosPedidosParaAcompanhamento(): Observable<any[]> {
    return this.http.get<any[]>(`${this.pedidosUrl}/admin/listar`);
  }

  atualizarStatusPedido(id: number, status: string): Observable<any> {
    return this.http.put(`${this.pedidosUrl}/${id}/status?status=${status}`, {});
  }
}
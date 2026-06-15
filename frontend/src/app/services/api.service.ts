import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = `${environment.apiUrl}/pedidos`;

  constructor(private http: HttpClient) { }


   //Envia o payload completo do pedido para salvar no banco de dados.

  enviarPedido(pedido: any): Observable<any> {
    return this.http.post(this.baseUrl, pedido);
  }


   //Verifica se a data selecionada possui vagas disponíveis na agenda.

  validarData(data: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/validar-data?data=${data}`);
  }


   //Verifica a disponibilidade de um horário específico para uma data.
   //Converte o formato "HH:mm" para "HH:mm:ss" antes do envio para o mapeamento do LocalTime.

  verificarHorario(data: string, horario: string): Observable<boolean> {
    const horarioFormatado = horario.includes(':00') ? horario : `${horario}:00`;
    return this.http.get<boolean>(`${this.baseUrl}/verificar-horario?data=${data}&horario=${horarioFormatado}`);
  }

  getSlotsDisponiveis(data: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/slots-disponiveis?data=${data}`);
  }
}

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  standalone: false
})
export class AdminDashboardComponent implements OnInit {
  activeTab: 'principal' | 'acompanhamento' = 'principal';

  metricas: any = null;
  pedidos: any[] = []; 
  page = 0;
  size = 10;
  totalPages = 1;

  todosPedidosAcompanhamento: any[] = [];
  pedidosFiltrados: any[] = [];
  filtroStatusAtual: string = 'PENDENTE';
  qtdPendentesCard: number = 0;

  constructor(
    private adminService: AdminService, 
    private router: Router,
    private cdr: ChangeDetectorRef 
  ) { }

  ngOnInit() {
    this.carregarDados();
  }

  mudarAbaPrincipal(aba: 'principal' | 'acompanhamento') {
    this.activeTab = aba;
    if (aba === 'acompanhamento') {
      this.carregarPedidosParaAcompanhamento();
    } else {
      this.carregarDados();
    }
  }

  carregarDados() {
    this.adminService.getMetricas().subscribe({
      next: (dados: any) => {
        this.metricas = dados;
        this.cdr.detectChanges();
      },
      error: () => this.logout()
    });

    this.carregarHistorico();
  }

  carregarHistorico() {
    this.adminService.getHistorico(this.page, this.size).subscribe({
      next: (res: any) => {
        this.pedidos = res.content;
        this.totalPages = res.totalPages;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error('Erro ao carregar histórico:', err)
    });
  }

  carregarPedidosParaAcompanhamento() {
    this.adminService.getTodosPedidosParaAcompanhamento().subscribe({
      next: (res: any[]) => {
        this.todosPedidosAcompanhamento = res;
        this.qtdPendentesCard = res.filter(p => p.status === 'PENDENTE' || !p.status).length;
        this.filtrarPedidosPorStatus(this.filtroStatusAtual);
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error('Erro ao carregar lista', err)
    });
  }

  filtrarPedidosPorStatus(status: string) {
    this.filtroStatusAtual = status;
    if (status === 'TODOS') {
      this.pedidosFiltrados = this.todosPedidosAcompanhamento;
    } else {
      this.pedidosFiltrados = this.todosPedidosAcompanhamento.filter(p => {
        const pStatus = p.status ? p.status.toUpperCase() : 'PENDENTE';
        return pStatus === status;
      });
    }
  }

  darBaixaPedido(id: number) {
    if (confirm('Confirmar entrega e dar BAIXA neste pedido?')) {
      this.adminService.atualizarStatusPedido(id, 'ENTREGUE').subscribe({
        next: () => this.carregarPedidosParaAcompanhamento(),
        error: () => alert('Erro ao atualizar status.')
      });
    }
  }

  cancelarPedido(id: number) {
    if (confirm('Tem certeza absoluta que deseja CANCELAR este pedido?')) {
      this.adminService.atualizarStatusPedido(id, 'CANCELADO').subscribe({
        next: () => this.carregarPedidosParaAcompanhamento(),
        error: () => alert('Erro ao cancelar o pedido.')
      });
    }
  }

  mudarPagina(novaPagina: number) {
    this.page = novaPagina;
    this.carregarHistorico();
  }

  exportarCSV() {
    this.adminService.exportarCsv().subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `relatorio-vendas-${new Date().getMonth() + 1}.csv`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => alert('Erro ao exportar CSV.')
    });
  }

  logout() {
    localStorage.removeItem('admin_token');
    this.router.navigate(['/admin/login']);
  }
}
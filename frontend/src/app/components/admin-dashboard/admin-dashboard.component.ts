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
  activeTab: 'principal' | 'acompanhamento' | 'agenda' = 'principal';

  metricas: any = null;
  pedidos: any[] = [];
  page = 0;
  size = 10;
  totalPages = 1;

  todosPedidosAcompanhamento: any[] = [];
  pedidosFiltrados: any[] = [];
  filtroStatusAtual: string = 'PENDENTE';
  qtdPendentesCard: number = 0;

  mesFiltro: string = '';

  dataAgenda: string = '';
  agendaDia: any = null;
  carregandoAgenda: boolean = false;

  constructor(
    private adminService: AdminService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.inicializarFiltroMes();
    this.inicializarDataAgenda();
    this.carregarDados();
  }

  private inicializarDataAgenda() {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const dia = String(hoje.getDate()).padStart(2, '0');
    this.dataAgenda = `${ano}-${mes}-${dia}`;
  }

  private inicializarFiltroMes() {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    this.mesFiltro = `${ano}-${mes}`;
  }

  mudarAbaPrincipal(aba: 'principal' | 'acompanhamento' | 'agenda') {
    this.activeTab = aba;
    if (aba === 'acompanhamento') {
      this.carregarPedidosParaAcompanhamento();
    } else if (aba === 'agenda') {
      this.carregarAgendaDia();
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
        next: () => {
          this.carregarPedidosParaAcompanhamento();
          this.carregarDados(); // Atualiza a tabela da Visão Geral e as métricas
        },
        error: () => alert('Erro ao atualizar status.')
      });
    }
  }

  cancelarPedido(id: number) {
    if (confirm('Tem certeza absoluta que deseja CANCELAR este pedido?')) {
      this.adminService.atualizarStatusPedido(id, 'CANCELADO').subscribe({
        next: () => {
          this.carregarPedidosParaAcompanhamento();
          this.carregarDados(); // Atualiza a tabela da Visão Geral e as métricas
        },
        error: () => alert('Erro ao cancelar o pedido.')
      });
    }
  }

  mudarPagina(novaPagina: number) {
    this.page = novaPagina;
    this.carregarHistorico();
  }

  exportarPlanilhaExcel() {
    if (!this.mesFiltro) {
      alert('Por favor, selecione um mês válido!');
      return;
    }

    const [anoStr, mesStr] = this.mesFiltro.split('-');
    const ano = parseInt(anoStr, 10);
    const mes = parseInt(mesStr, 10);

    this.adminService.exportarExcel(ano, mes).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `relatorio-pedidos-${mesStr}-${anoStr}.xlsx`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err: any) => {
        console.error('Erro no download:', err);
        alert('Erro ao exportar planilha Excel dos pedidos.');
      }
    });
  }

  logout() {
    localStorage.removeItem('admin_token');
    this.router.navigate(['/admin/login']);
  }

  carregarAgendaDia() {
    if (!this.dataAgenda) return;
    this.carregandoAgenda = true;
    this.adminService.getAgendaDia(this.dataAgenda).subscribe({
      next: (res: any) => {
        this.agendaDia = res;
        this.carregandoAgenda = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Erro ao carregar agenda:', err);
        this.carregandoAgenda = false;
        alert('Erro ao carregar a agenda do dia.');
      }
    });
  }

  alternarDiaInteiro() {
    if (!this.dataAgenda || !this.agendaDia) return;

    const bloquear = !this.agendaDia.diaBloqueado;
    const acao = bloquear ? 'DESATIVAR' : 'ATIVAR';
    const msg = bloquear
      ? `Deseja DESATIVAR o dia ${this.formatarDataBr(this.dataAgenda)} inteiro? Nenhum horário ficará disponível para novos pedidos.`
      : `Deseja ATIVAR o dia ${this.formatarDataBr(this.dataAgenda)} novamente?`;

    if (!confirm(msg)) return;

    const request$ = bloquear
      ? this.adminService.bloquearAgenda(this.dataAgenda, undefined, 'Dia desativado pelo admin')
      : this.adminService.desbloquearAgenda(this.dataAgenda);

    request$.subscribe({
      next: () => this.carregarAgendaDia(),
      error: () => alert(`Erro ao ${acao.toLowerCase()} o dia.`)
    });
  }

  alternarHorario(slot: any) {
    if (!this.dataAgenda || !slot) return;
    if (slot.status === 'OCUPADO' || slot.status === 'LOTADO') return;

    const horario = this.formatarHorario(slot.horario);
    const bloquear = slot.status !== 'BLOQUEADO';
    const acao = bloquear ? 'desativar' : 'ativar';

    if (!confirm(`Deseja ${acao} o horário ${horario}?`)) return;

    const request$ = bloquear
      ? this.adminService.bloquearAgenda(this.dataAgenda, horario, 'Horário desativado pelo admin')
      : this.adminService.desbloquearAgenda(this.dataAgenda, horario);

    request$.subscribe({
      next: () => this.carregarAgendaDia(),
      error: () => alert(`Erro ao ${acao} o horário.`)
    });
  }

  formatarDataBr(dataIso: string): string {
    const [ano, mes, dia] = dataIso.split('-');
    return `${dia}/${mes}/${ano}`;
  }

  formatarHorario(horario: string): string {
    if (!horario) return '';
    return horario.substring(0, 5);
  }

  getClasseSlot(status: string): string {
    switch (status) {
      case 'DISPONIVEL': return 'bg-emerald-50 border-emerald-200 text-emerald-700 hover:bg-emerald-100';
      case 'BLOQUEADO': return 'bg-rose-50 border-rose-200 text-rose-700 hover:bg-rose-100';
      case 'OCUPADO': return 'bg-amber-50 border-amber-200 text-amber-700 cursor-not-allowed opacity-80';
      case 'LOTADO': return 'bg-stone-100 border-stone-200 text-stone-500 cursor-not-allowed opacity-70';
      default: return 'bg-white border-stone-200 text-stone-600';
    }
  }

  getLabelSlot(status: string): string {
    switch (status) {
      case 'DISPONIVEL': return 'Disponível';
      case 'BLOQUEADO': return 'Desativado';
      case 'OCUPADO': return 'Com pedido';
      case 'LOTADO': return 'Dia lotado';
      default: return status;
    }
  }
}

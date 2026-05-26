import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  standalone: false
})
export class AdminDashboardComponent implements OnInit {
  metricas: any = null;
  pedidos: any[] = [];
  page = 0;
  size = 10;
  totalPages = 1;

  constructor(private adminService: AdminService, private router: Router) { }

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    this.adminService.getMetricas().subscribe({
      next: (dados: any) => this.metricas = dados,
      error: () => this.logout()
    });

    this.carregarHistorico();
  }

  carregarHistorico() {
    this.adminService.getHistorico(this.page, this.size).subscribe({
      next: (res: any) => {
        this.pedidos = res.content;
        this.totalPages = res.totalPages;
      }
    });
  }

  mudarPagina(novaPagina: number) {
    this.page = novaPagina;
    this.carregarHistorico();
  }

  exportarCSV() {
    this.adminService.exportarCsv().subscribe({
      next: (blob: Blob) => { // CORRIGIDO: Adicionado ": Blob"
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `relatorio-vendas-${new Date().getMonth() + 1}.csv`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => alert('Não foi possível gerar o arquivo CSV.')
    });
  }

  logout() {
    localStorage.removeItem('admin_token');
    this.router.navigate(['/admin/login']);
  }
}

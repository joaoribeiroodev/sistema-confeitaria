import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css'],
  standalone: false
})
export class AdminLoginComponent {
  username = '';
  password = '';
  erroMensagem = '';

  constructor(private adminService: AdminService, private router: Router) {}

  fazerLogin() {
    this.erroMensagem = '';

    this.adminService.login({ username: this.username, password: this.password }).subscribe({
      next: (res: any) => {
        localStorage.setItem('admin_token', res.token);
        this.router.navigate(['/admin']);
      },
      error: (err) => {
        if (err.status === 401) {
          this.erroMensagem = 'Usuário ou senha inválidos.';
        } else {
          this.erroMensagem = 'Erro ao conectar com o servidor.';
        }
      }
    });
  }
}

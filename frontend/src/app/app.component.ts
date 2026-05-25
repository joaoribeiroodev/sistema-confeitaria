import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: false // 👈 Adicione aqui também para matar o outro erro!
})
export class AppComponent {
  title = 'Delícias da Nalva'; // ou 'frontend'
}

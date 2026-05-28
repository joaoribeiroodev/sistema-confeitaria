import { Injectable } from '@angular/core';
import { BehaviorSubject, map } from 'rxjs';

export interface CartItem {
  id: number; // 🌟 Mantido estritamente como número (zero conflitos com o Java)
  nome: string;
  precoUnitario: number;
  quantidade: number;
  sabor?: string | null; // 🌟 Sabor armazenado de forma independente
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private cartItems = new BehaviorSubject<CartItem[]>([]);
  items$ = this.cartItems.asObservable();

  totalItems$ = this.items$.pipe(map(items => items.reduce((acc, item) => acc + item.quantidade, 0)));
  totalValue$ = this.items$.pipe(map(items => items.reduce((acc, item) => acc + (item.precoUnitario * item.quantidade), 0)));

  updateQuantity(id: number, nome: string, precoUnitario: number, change: number, sabor?: string | null) {
    const current = [...this.cartItems.value];
    
    // 🌟 Busca o item exato combinando o ID numérico E o Sabor selecionado
    const index = current.findIndex(i => i.id === id && i.sabor === sabor);

    if (index > -1) {
      current[index].quantidade += change;
      if (current[index].quantidade <= 0) current.splice(index, 1);
    } else if (change > 0) {
      current.push({ id, nome, precoUnitario, quantidade: 1, sabor });
    }
    this.cartItems.next(current);
  }

  getItemQuantity(id: number, sabor?: string | null): number {
    const item = this.cartItems.value.find(i => i.id === id && i.sabor === sabor);
    return item ? item.quantidade : 0;
  }

  adicionarItem(item: CartItem) {
    this.updateQuantity(item.id, item.nome, item.precoUnitario, 1, item.sabor);
  }

  removerItem(id: number, sabor?: string | null) {
    const item = this.cartItems.value.find(i => i.id === id && i.sabor === sabor);
    if (item && item.quantidade > 0) {
      this.updateQuantity(id, item.nome, item.precoUnitario, -1, item.sabor);
    }
  }

  getSnapshot() { return this.cartItems.value; }
  clear() { this.cartItems.next([]); }
}
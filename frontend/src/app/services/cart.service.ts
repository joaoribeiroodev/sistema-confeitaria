import { Injectable } from '@angular/core';
import { BehaviorSubject, map } from 'rxjs';

export interface CartItem {
  id: number;
  nome: string;
  precoUnitario: number;
  quantidade: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private cartItems = new BehaviorSubject<CartItem[]>([]);
  items$ = this.cartItems.asObservable();

  totalItems$ = this.items$.pipe(map(items => items.reduce((acc, item) => acc + item.quantidade, 0)));
  totalValue$ = this.items$.pipe(map(items => items.reduce((acc, item) => acc + (item.precoUnitario * item.quantidade), 0)));

  updateQuantity(id: number, nome: string, precoUnitario: number, change: number) {
    const current = [...this.cartItems.value];
    const index = current.findIndex(i => i.id === id);

    if (index > -1) {
      current[index].quantidade += change;
      if (current[index].quantidade <= 0) current.splice(index, 1);
    } else if (change > 0) {
      current.push({ id, nome, precoUnitario, quantidade: 1 });
    }
    this.cartItems.next(current);
  }

  getItemQuantity(id: number): number {
    const item = this.cartItems.value.find(i => i.id === id);
    return item ? item.quantidade : 0;
  }

  adicionarItem(item: CartItem) {
    this.updateQuantity(item.id, item.nome, item.precoUnitario, 1);
  }

  removerItem(id: number) {
    const item = this.cartItems.value.find(i => i.id === id);
    if (item && item.quantidade > 0) {
      this.updateQuantity(id, item.nome, item.precoUnitario, -1);
    }
  }

  getSnapshot() { return this.cartItems.value; }
  clear() { this.cartItems.next([]); }
}

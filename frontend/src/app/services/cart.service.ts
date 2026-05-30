import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface CartItem {
  id: number;
  nome: string;
  precoUnitario: number;
  quantidade: number;
  sabor?: string | null;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private cartItems = new BehaviorSubject<CartItem[]>([]);

  public items$: Observable<CartItem[]> = this.cartItems.asObservable();

  public totalItems$: Observable<number> = this.items$.pipe(
    map((items: CartItem[]) => items.reduce((acc, item) => acc + item.quantidade, 0))
  );

  public totalValue$: Observable<number> = this.items$.pipe(
    map((items: CartItem[]) => items.reduce((acc, item) => acc + (item.precoUnitario * item.quantidade), 0))
  );

  public updateQuantity(id: number, nome: string, precoUnitario: number, change: number, sabor?: string | null): void {
    const current = [...this.cartItems.value];

    const normalizedSabor = sabor || null;

    const index = current.findIndex((i: CartItem) => i.id === id && (i.sabor || null) === normalizedSabor);

    if (index > -1) {
      current[index].quantidade += change;
      if (current[index].quantidade <= 0) {
        current.splice(index, 1);
      }
    } else if (change > 0) {
      current.push({ id, nome, precoUnitario, quantidade: change, sabor: normalizedSabor });
    }

    this.cartItems.next(current);
  }

  public getItemQuantity(id: number, sabor?: string | null): number {
    const normalizedSabor = sabor || null;
    const item = this.cartItems.value.find((i: CartItem) => i.id === id && (i.sabor || null) === normalizedSabor);
    return item ? item.quantidade : 0;
  }

  public adicionarItem(item: CartItem): void {
    this.updateQuantity(item.id, item.nome, item.precoUnitario, item.quantidade, item.sabor);
  }

  public removerItem(id: number, sabor?: string | null, quantidadeParaRemover: number = 1): void {
    const normalizedSabor = sabor || null;
    const item = this.cartItems.value.find((i: CartItem) => i.id === id && (i.sabor || null) === normalizedSabor);

    if (item && item.quantidade > 0) {
      this.updateQuantity(id, item.nome, item.precoUnitario, -quantidadeParaRemover, normalizedSabor);
    }
  }

  public getSnapshot(): CartItem[] {
    return this.cartItems.value;
  }

  public clear(): void {
    this.cartItems.next([]);
  }
}

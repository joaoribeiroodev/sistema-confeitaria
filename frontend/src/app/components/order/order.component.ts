import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CartService } from '../../services/cart.service';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  standalone: false
})
export class OrderComponent implements OnInit {
  orderForm!: FormGroup;
  step: number = 1;
  activeCategory: string = 'SALGADOS_FRITOS';
  agendaLotada: boolean = false;
  buscandoCep: boolean = false;

  produtos = [
    { id: 1, nome: 'Coxinha de Frango', descricao: 'Cento do salgado frito na hora', preco: 80.00, categoria: 'SALGADOS_FRITOS' },
    { id: 2, nome: 'Empada de Frango', descricao: 'Salgado assado super recheado', preco: 95.00, categoria: 'SALGADOS_ASSADOS' },
    { id: 3, nome: 'Camafeu de Nozes', descricao: 'Doce fino banhado em fondant', preco: 150.00, categoria: 'DOCES_FINOS' },
    { id: 4, nome: 'Brigadeiro Tradicional', descricao: 'Chocolate nobre granulado', preco: 70.00, categoria: 'DOCES_SIMPLES' }
  ];

  constructor(
    private fb: FormBuilder,
    public cartService: CartService,
    private apiService: ApiService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    const savedClient = JSON.parse(localStorage.getItem('cliente_dados') || '{}');

    this.orderForm = this.fb.group({
      nome: [savedClient.nome || '', Validators.required],
      telefone: [savedClient.telefone || '', Validators.required],
      cpf: [savedClient.cpf || '', [Validators.required, this.validarCPF]],
      data: ['', Validators.required],
      horario: ['', Validators.required],
      // Novos campos de endereço detalhados
      cep: [savedClient.cep || '', [Validators.required, Validators.minLength(8)]],
      logradouro: [savedClient.logradouro || '', Validators.required],
      numero: [savedClient.numero || '', Validators.required],
      complemento: [savedClient.complemento || ''],
      bairro: [savedClient.bairro || '', Validators.required],
      cidade: [savedClient.cidade || '', Validators.required],
      uf: [savedClient.uf || '', Validators.required]
    });

    this.orderForm.get('data')?.valueChanges.subscribe(data => {
      if (data) {
        this.apiService.validarData(data).subscribe(disponivel => {
          this.agendaLotada = !disponivel;
        });
      }
    });
  }

  // =========================================================================
  // INTEGRAÇÃO VIACEP
  // =========================================================================
  buscarCep() {
    let cep = this.orderForm.get('cep')?.value?.replace(/\D/g, ''); // Remove tudo que não for número

    if (cep && cep.length === 8) {
      this.buscandoCep = true;
      this.http.get(`https://viacep.com.br/ws/${cep}/json/`).subscribe({
        next: (dados: any) => {
          this.buscandoCep = false;
          if (!dados.erro) {
            this.orderForm.patchValue({
              logradouro: dados.logradouro,
              bairro: dados.bairro,
              cidade: dados.localidade,
              uf: dados.uf
            });
          } else {
            alert('CEP não encontrado!');
          }
        },
        error: () => {
          this.buscandoCep = false;
          alert('Erro ao buscar o CEP. Tente novamente.');
        }
      });
    }
  }

  validarCPF(control: any) {
    const cpf = control.value?.replace(/[^\d]+/g, '');
    if (!cpf || cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return { cpfInvalido: true };
    let soma = 0, resto;
    for (let i = 1; i <= 9; i++) soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
    resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf.substring(9, 10))) return { cpfInvalido: true };
    soma = 0;
    for (let i = 1; i <= 10; i++) soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
    resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf.substring(10, 11))) return { cpfInvalido: true };
    return null;
  }

  avancarParaMenu() {
    if (this.orderForm.valid && !this.agendaLotada) {
      localStorage.setItem('cliente_dados', JSON.stringify(this.orderForm.value));
      this.step = 2;
    }
  }

  getQuantidade(id: number): number {
    try {
      const itens = this.cartService.getSnapshot() || [];
      const item = itens.find((i: any) => i.id === id);
      return item ? item.quantidade : 0;
    } catch (e) {
      return 0;
    }
  }

  adicionarProduto(prod: any) {
    const itemCarrinho = { id: prod.id, nome: prod.nome, precoUnitario: prod.preco, quantidade: 1 };
    const s = this.cartService as any;
    if (typeof s.adicionarItem === 'function') s.adicionarItem(itemCarrinho);
    else if (typeof s.adicionar === 'function') s.adicionar(itemCarrinho);
    else if (typeof s.addItem === 'function') s.addItem(itemCarrinho);
    else if (typeof s.add === 'function') s.add(itemCarrinho);
  }

  removerProduto(id: number) {
    const s = this.cartService as any;
    if (typeof s.removerItem === 'function') s.removerItem(id);
    else if (typeof s.remover === 'function') s.remover(id);
    else if (typeof s.removeItem === 'function') s.removeItem(id);
    else if (typeof s.remove === 'function') s.remove(id);
  }

  get totalItens(): number {
    try {
      const itens = this.cartService.getSnapshot() || [];
      return itens.reduce((acc: number, item: any) => acc + item.quantidade, 0);
    } catch (e) {
      return 0;
    }
  }

  get totalValue(): number {
    try {
      const itens = this.cartService.getSnapshot() || [];
      return itens.reduce((acc: number, item: any) => acc + (item.precoUnitario * item.quantidade), 0);
    } catch (e) {
      return 0;
    }
  }

  finalizarPedido() {
    const dadosForm = this.orderForm.value;
    const itensCarrinho = this.cartService.getSnapshot() || [];

    const complementoFormatado = dadosForm.complemento ? ` - ${dadosForm.complemento}` : '';
    const enderecoCompleto = `${dadosForm.logradouro}, ${dadosForm.numero}${complementoFormatado}, ${dadosForm.bairro}, ${dadosForm.cidade} - ${dadosForm.uf}, CEP: ${dadosForm.cep}`;

    let totalCalculado = 0;
    const itensPayload = itensCarrinho.map((item: any) => {
      totalCalculado += (item.precoUnitario * item.quantidade);
      return {
        produto: { id: item.id },
        quantidade: item.quantidade,
        precoPraticado: item.precoUnitario
      };
    });

    const payload = {
      cliente: {
        nome: dadosForm.nome,
        telefone: dadosForm.telefone,
        cpf: dadosForm.cpf,
        endereco: enderecoCompleto
      },
      dataEncomenda: dadosForm.data,
      horarioEncomenda: dadosForm.horario + ":00",
      valorTotal: totalCalculado,
      itens: itensPayload
    };

    const whatsappMessageParts: string[] = [];
    whatsappMessageParts.push('*🧾 NOVO PEDIDO CONFIRMADO*');
    whatsappMessageParts.push('');
    whatsappMessageParts.push(`*Cliente:* ${dadosForm.nome}`);
    whatsappMessageParts.push(`*Endereço:* ${enderecoCompleto}`);
    whatsappMessageParts.push(`*Data:* ${dadosForm.data} às ${dadosForm.horario}`);
    whatsappMessageParts.push('');
    whatsappMessageParts.push('*ITENS:*');

    itensCarrinho.forEach((item: any) => {
      whatsappMessageParts.push(`- ${item.quantidade}x ${item.nome} (R$ ${(item.precoUnitario * item.quantidade).toFixed(2)})`);
    });

    whatsappMessageParts.push('');
    whatsappMessageParts.push(`*TOTAL:* R$ ${totalCalculado.toFixed(2)}`);

    const numMae = '5571992661385';
    const whatsappUrl = `https://wa.me/${numMae}?text=${encodeURIComponent(whatsappMessageParts.join('\n'))}`;
    const popup = window.open('', '_blank');

    this.apiService.enviarPedido(payload).subscribe({
      next: () => {
        if (popup) {
          popup.location.href = whatsappUrl;
        } else {
          window.open(whatsappUrl, '_blank');
        }

        if (typeof this.cartService.clear === 'function') this.cartService.clear();
        this.step = 1;
        this.orderForm.reset();
      },
      error: (err: any) => {
        if (popup) popup.close();
        alert(err.error || 'Ocorreu um erro ao salvar o seu pedido.');
      }
    });
  }
}

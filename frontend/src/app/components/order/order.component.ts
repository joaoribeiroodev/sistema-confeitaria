import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CartService } from '../../services/cart.service';
import { ApiService } from '../../services/api.service';

export enum SaborOpcao {
  FRANGO = 'Frango',
  CARNE = 'Carne'
}

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

  // Sincronizado: preco alterado para precoUnitario e saborSelecionado pré-definido como padrão
  produtos: any[] = [
    // salgados fritos
    { id: 1, nome: 'Coxinha de Frango', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },
    { id: 2, nome: 'Quibe', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },
    { id: 3, nome: 'Boliviano', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS', sabores: [SaborOpcao.FRANGO, SaborOpcao.CARNE], saborSelecionado: SaborOpcao.FRANGO },
    { id: 4, nome: 'Risole', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },
    { id: 5, nome: 'Bolinho misto (queijo e presunto)', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },
    { id: 6, nome: 'Pastel frito', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS', sabores: [SaborOpcao.FRANGO, SaborOpcao.CARNE], saborSelecionado: SaborOpcao.FRANGO },
    { id: 7, nome: 'Salgados Congelados (todos)', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },

    // salgados assados
    { id: 8, nome: 'Empada de Frango', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 9, nome: 'Barquete', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 10, nome: 'Saltenha', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 11, nome: 'Empada', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 12, nome: 'Pastel de Forno', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS', sabores: [SaborOpcao.FRANGO, SaborOpcao.CARNE], saborSelecionado: SaborOpcao.FRANGO },
    { id: 13, nome: 'Pãozinho recheado', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 14, nome: 'Pãozinho sem recheio', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },

    // doces finos
    { id: 15, nome: 'Ameixa', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 16, nome: 'Limão', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 17, nome: 'Maracujá', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 18, nome: 'Amendoim', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 19, nome: 'Nozes', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 20, nome: 'Damasco', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 21, nome: 'Prestígio', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },

    // doces simples
    { id: 22, nome: 'Brigadeiro', precoUnitario: 1.80, categoria: 'DOCES_SIMPLES' },
    { id: 23, nome: 'Casadinho', precoUnitario: 1.80, categoria: 'DOCES_SIMPLES' },
    { id: 24, nome: 'Beijinho', precoUnitario: 1.80, categoria: 'DOCES_SIMPLES' },
    { id: 25, nome: 'Pastel doce', precoUnitario: 1.80, categoria: 'DOCES_SIMPLES' },
    { id: 26, nome: 'Empadinha doce', precoUnitario: 1.80, categoria: 'DOCES_SIMPLES' },
    { id: 27, nome: 'Brigadeiro de leite ninho', precoUnitario: 1.80, categoria: 'DOCES_SIMPLES' }
  ];

  constructor(
    private fb: FormBuilder,
    public cartService: CartService,
    private apiService: ApiService,
    private http: HttpClient
  ) { }

  ngOnInit() {
    const savedClient = JSON.parse(localStorage.getItem('cliente_dados') || '{}');

    this.orderForm = this.fb.group({
      nome: [savedClient.nome || '', Validators.required],
      telefone: [savedClient.telefone || '', Validators.required],
      cpf: [savedClient.cpf || '', [Validators.required, this.validarCPF]],
      data: ['', Validators.required],
      horario: ['', Validators.required],
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
    let cep = this.orderForm.get('cep')?.value?.replace(/\D/g, '');

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

  getQuantidade(id: number | string): number {
    try {
      const itens = this.cartService.getSnapshot() || [];
      const item = itens.find((i: any) => i.id === id);
      return item ? item.quantidade : 0;
    } catch (e) {
      return 0;
    }
  }

  // Corrigido: Lendo dinamicamente a propriedade reativa prod.saborSelecionado do HTML
  adicionarProduto(prod: any) {
    const sabor = prod.saborSelecionado;
    const nomeFinal = sabor ? `${prod.nome} (${sabor})` : prod.nome;
    const idUnico = sabor ? `${prod.id}-${sabor}` : prod.id;

    const itemCarrinho = {
      id: idUnico,
      nome: nomeFinal,
      precoUnitario: prod.precoUnitario,
      quantidade: 1
    };

    const s = this.cartService as any;
    if (typeof s.adicionarItem === 'function') s.adicionarItem(itemCarrinho);
    else if (typeof s.adicionar === 'function') s.adicionar(itemCarrinho);
    else if (typeof s.addItem === 'function') s.addItem(itemCarrinho);
    else if (typeof s.add === 'function') s.add(itemCarrinho);
  }

  removerProduto(id: number | string) {
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

      // Tratamento seguro para enviar IDs puros e válidos para o banco de dados
      const idNumericoOriginal = typeof item.id === 'string' ? parseInt(item.id.split('-')[0], 10) : item.id;

      return {
        produto: { id: idNumericoOriginal },
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
    whatsappMessageParts.push('*NOVO PEDIDO CONFIRMADO*');
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

    const numMae = '5571987669537';
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

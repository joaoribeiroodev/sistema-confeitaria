import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
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
  whatsappUrlFallback: string = '';
  horarioOcupado: boolean = false;

  produtos: any[] = [
    // Salgados fritos
    { id: 1, nome: 'Coxinha de Frango', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },
    { id: 2, nome: 'Quibe', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },
    { id: 3, nome: 'Boliviano', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS', sabores: [SaborOpcao.FRANGO, SaborOpcao.CARNE], saborSelecionado: SaborOpcao.FRANGO },
    { id: 4, nome: 'Risole', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },
    { id: 5, nome: 'Bolinho misto (queijo e presunto)', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },
    { id: 6, nome: 'Pastel frito', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS', sabores: [SaborOpcao.FRANGO, SaborOpcao.CARNE], saborSelecionado: SaborOpcao.FRANGO },
    { id: 7, nome: 'Salgados Congelados (todos)', precoUnitario: 1.80, categoria: 'SALGADOS_FRITOS' },

    // Salgados assados
    { id: 8, nome: 'Empada de Frango', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 9, nome: 'Barquete', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 10, nome: 'Saltenha', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 11, nome: 'Empada', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 12, nome: 'Pastel de Forno', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS', sabores: [SaborOpcao.FRANGO, SaborOpcao.CARNE], saborSelecionado: SaborOpcao.FRANGO },
    { id: 13, nome: 'Pãozinho recheado', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },
    { id: 14, nome: 'Pãozinho sem recheio', precoUnitario: 1.80, categoria: 'SALGADOS_ASSADOS' },

    // Doces finos
    { id: 15, nome: 'Ameixa', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 16, nome: 'Limão', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 17, nome: 'Maracujá', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 18, nome: 'Amendoim', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 19, nome: 'Nozes', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 20, nome: 'Damasco', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },
    { id: 21, nome: 'Prestígio', precoUnitario: 2.00, categoria: 'DOCES_FINOS' },

    // Doces simples
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
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    const savedClient = JSON.parse(localStorage.getItem('cliente_dados') || '{}');

    this.orderForm = this.fb.group({
      nome: [savedClient.nome || '', Validators.required],
      telefone: [savedClient.telefone || '', [Validators.required, this.validarTelefone]],
      data: ['', Validators.required],
      horario: ['', Validators.required],
      cep: [savedClient.cep || '', [Validators.required, Validators.minLength(8)]],
      logradouro: [savedClient.logradouro || '', Validators.required],
      numero: [savedClient.numero || '', Validators.required],
      complemento: [savedClient.complemento || '', Validators.required],
      bairro: [savedClient.bairro || '', Validators.required],
      cidade: [savedClient.cidade || '', Validators.required],
      uf: [savedClient.uf || '', Validators.required]
    });

    this.orderForm.get('data')?.valueChanges.subscribe((data: any) => {
      if (data) {
        this.apiService.validarData(data).subscribe({
          next: (disponivel: any) => {
            this.agendaLotada = !Boolean(disponivel);
          },
          error: () => {
            this.agendaLotada = false;
          }
        });
      }
    });
  }

  validarTelefone = (control: AbstractControl): { [key: string]: boolean } | null => {
    if (!control.value) return null;
    const numeroLimpo = control.value.toString().replace(/[^\d]+/g, '');
    if (numeroLimpo.length !== 11 || /^(\d)\1{10}$/.test(numeroLimpo)) {
      return { telefoneInvalido: true };
    }
    return null;
  };

  buscarCep(): void {
    const cepValue = this.orderForm.get('cep')?.value;
    const cep = cepValue ? cepValue.toString().replace(/\D/g, '') : '';

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

  avancarParaMenu(): void {
    if (this.orderForm.valid && !this.agendaLotada) {
      const data = this.orderForm.get('data')?.value;
      const horario = this.orderForm.get('horario')?.value;

      this.apiService.verificarHorario(data, horario).subscribe({
        next: (disponivel: boolean) => {
          if (disponivel) {
            this.horarioOcupado = false;
            localStorage.setItem('cliente_dados', JSON.stringify(this.orderForm.value));
            this.step = 2;
            this.cdr.detectChanges();
          } else {
            this.horarioOcupado = true;
            alert('Este horário já foi reservado por outro cliente. Por favor, escolha outro horário.');
          }
        },
        error: () => {
          alert('Ocorreu um erro ao verificar o horário. Tente novamente.');
        }
      });
    } else {
      this.orderForm.markAllAsTouched();
    }
  }

  // 🌟 MODIFICADO: Agora consulta a quantidade usando o ID puro e o Sabor selecionado na tela
  getQuantidade(prod: any): number {
    try {
      const sabor = prod.saborSelecionado || null;
      return this.cartService.getItemQuantity(prod.id, sabor);
    } catch (e) {
      return 0;
    }
  }

  // 🌟 MODIFICADO: Adiciona o item ao carrinho repassando o ID como número e guardando o sabor isolado
  adicionarProduto(prod: any): void {
    const sabor = prod.saborSelecionado || null;
    const nomeFinal = sabor ? `${prod.nome} (${sabor})` : prod.nome;

    const itemCarrinho = {
      id: prod.id, // Número puro
      nome: nomeFinal,
      precoUnitario: prod.precoUnitario,
      quantidade: 1,
      sabor: sabor
    };

    this.cartService.adicionarItem(itemCarrinho);
  }

  // 🌟 MODIFICADO: Remove o item informando o ID puro e o sabor selecionado
  removerProduto(prod: any): void {
    const sabor = prod.saborSelecionado || null;
    this.cartService.removerItem(prod.id, sabor);
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

  finalizarPedido(): void {
    const dadosForm = this.orderForm.value;
    const itensCarrinho = this.cartService.getSnapshot() || [];

    const complementoFormatado = dadosForm.complemento ? ` - ${dadosForm.complemento}` : '';
    const enderecoCompleto = `${dadosForm.logradouro}, ${dadosForm.numero}${complementoFormatado}, ${dadosForm.bairro}, ${dadosForm.cidade} - ${dadosForm.uf}, CEP: ${dadosForm.cep}`;

    let totalCalculado = 0;

    // 🌟 MODIFICADO: Mapeamento limpo enviando o ID numérico nativo para o back-end Java
    const itensPayload = itensCarrinho.map((item: any) => {
      totalCalculado += (item.precoUnitario * item.quantidade);
      
      return {
        produto: { id: item.id }, // ID numérico puro!
        quantidade: item.quantidade,
        precoPraticado: item.precoUnitario,
        sabor: item.sabor || null // Repassa string ou nulo sem misturar com o ID
      };
    });

    const payload = {
      cliente: {
        nome: dadosForm.nome,
        telefone: dadosForm.telefone,
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
        this.whatsappUrlFallback = whatsappUrl;

        if (popup) {
          popup.location.href = whatsappUrl;
        } else {
          window.open(whatsappUrl, '_blank');
        }

        this.cartService.clear();
        this.step = 3;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        if (popup) popup.close();

        let mensagemErro = 'Ocorreu um erro inesperado ao salvar o seu pedido.';

        if (err.error && typeof err.error === 'string') {
          mensagemErro = err.error;
        } else if (err.error && err.error.message) {
          mensagemErro = err.error.message;
        }

        mensagemErro = mensagemErro
          .replace(/^\d{3}\s[A-Z_]+\s/, '')
          .replace(/^"/, '')
          .replace(/"$/, '');

        alert('Atenção: ' + mensagemErro);
        this.cdr.detectChanges();
      }
    });
  }

  voltarAoInicio(): void {
    this.orderForm.reset();
    this.whatsappUrlFallback = '';
    this.step = 1;
  }
}
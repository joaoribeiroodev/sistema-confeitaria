package com.sistema.confeitaria.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDate dataEncomenda;
    private LocalTime horarioEncomenda;
    private Double valorTotal;

    @Column(length = 20)
    private String status = "PENDENTE";

    @Column(name = "tipo_entrega", length = 20)
    private String tipoEntrega;

    // 🌟 NOVO CAMPO: Forma de pagamento (PIX ou DINHEIRO)
    @Column(name = "forma_pagamento", length = 20)
    private String formaPagamento;

    // Mapeamento bidirecional correto com ItemPedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;
}
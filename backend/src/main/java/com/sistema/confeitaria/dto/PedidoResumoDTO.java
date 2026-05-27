package com.sistema.confeitaria.dto;

import com.sistema.confeitaria.model.Pedido;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PedidoResumoDTO {
    private Long id;
    private String nomeCliente;
    private LocalDate dataEncomenda;
    private BigDecimal valorTotal;

    public PedidoResumoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.nomeCliente = pedido.getCliente() != null ? pedido.getCliente().getNome() : "-";
        this.dataEncomenda = pedido.getDataEncomenda();
        // A conversão de Double para BigDecimal é feita aqui
        this.valorTotal = pedido.getValorTotal() != null ? BigDecimal.valueOf(pedido.getValorTotal()) : BigDecimal.ZERO;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataEncomenda() {
        return dataEncomenda;
    }

    public void setDataEncomenda(LocalDate dataEncomenda) {
        this.dataEncomenda = dataEncomenda;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
package com.sistema.confeitaria.dto;

import com.sistema.confeitaria.model.Pedido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime; // <-- IMPORTANTE ADICIONAR O IMPORT

public class PedidoResumoDTO {
    private Long id;
    private String nomeCliente;
    private String enderecoCliente; // <-- NOVO CAMPO
    private LocalDate dataEncomenda;
    private LocalTime horarioEncomenda; // <-- NOVO CAMPO
    private BigDecimal valorTotal;

    public PedidoResumoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.nomeCliente = pedido.getCliente() != null ? pedido.getCliente().getNome() : "-";
        this.enderecoCliente = pedido.getCliente() != null ? pedido.getCliente().getEndereco() : "-"; // <-- NOVO MAPEAMENTO
        this.dataEncomenda = pedido.getDataEncomenda();
        this.horarioEncomenda = pedido.getHorarioEncomenda(); // <-- NOVO MAPEAMENTO
        // A conversão de Double para BigDecimal é feita aqui
        this.valorTotal = pedido.getValorTotal() != null ? BigDecimal.valueOf(pedido.getValorTotal()) : BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEnderecoCliente() {
        return enderecoCliente;
    }

    public void setEnderecoCliente(String enderecoCliente) {
        this.enderecoCliente = enderecoCliente;
    }

    public LocalDate getDataEncomenda() {
        return dataEncomenda;
    }

    public void setDataEncomenda(LocalDate dataEncomenda) {
        this.dataEncomenda = dataEncomenda;
    }

    public LocalTime getHorarioEncomenda() {
        return horarioEncomenda;
    }

    public void setHorarioEncomenda(LocalTime horarioEncomenda) {
        this.horarioEncomenda = horarioEncomenda;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
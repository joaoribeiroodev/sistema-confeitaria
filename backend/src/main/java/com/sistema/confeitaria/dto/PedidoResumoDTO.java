package com.sistema.confeitaria.dto;

import com.sistema.confeitaria.model.Pedido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class PedidoResumoDTO {
    private Long id;
    private String nomeCliente;
    private String enderecoCliente;
    private LocalDate dataEncomenda;
    private LocalTime horarioEncomenda;
    private BigDecimal valorTotal;
    private String tipoEntrega; // 🌟 NOVO CAMPO NO DTO

    public PedidoResumoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.nomeCliente = pedido.getCliente() != null ? pedido.getCliente().getNome() : "-";
        this.enderecoCliente = pedido.getCliente() != null ? pedido.getCliente().getEndereco() : "-";
        this.dataEncomenda = pedido.getDataEncomenda();
        this.horarioEncomenda = pedido.getHorarioEncomenda();
        this.valorTotal = pedido.getValorTotal() != null ? BigDecimal.valueOf(pedido.getValorTotal()) : BigDecimal.ZERO;
        this.tipoEntrega = pedido.getTipoEntrega() != null ? pedido.getTipoEntrega() : "ENTREGA"; // 🌟 MAPEAMENTO DO NOVO CAMPO
    }

    // Getters e Setters
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

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }
}
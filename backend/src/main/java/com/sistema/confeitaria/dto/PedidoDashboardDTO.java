package com.sistema.confeitaria.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDashboardDTO {
    
    private Long id;
    private String clienteNome;
    private String clienteTelefone;
    private String clienteEndereco;
    private LocalDate dataEncomenda;
    private LocalTime horarioEncomenda;
    private Double valorTotal;
    private String status;
    private List<ItemPedidoDTO> itens; // Lista baseada na classe interna abaixo

    // CLASSE INTERNA UNIFICADA (Evita criar múltiplos ficheiros)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoDTO {
        private String produtoNome;
        private Integer quantidade;
        private Double precoPraticado;
    }
}
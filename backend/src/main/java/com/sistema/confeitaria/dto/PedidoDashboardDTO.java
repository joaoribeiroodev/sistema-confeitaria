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
    private LocalDate dataEncomenda;
    private LocalTime horarioEncomenda;
    private Double valorTotal;
    private String status;
    private String tipoEntrega;
    private String formaPagamento;
    private ClienteDTO cliente;      
    private List<ItemPedidoDTO> itens; 

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ClienteDTO {
        private String nome;
        private String telefone;
        private String endereco;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ItemPedidoDTO {
        private ProdutoDTO produto; 
        private Integer quantidade;
        private Double precoPraticado;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ProdutoDTO {
        private String nome;
    }
}
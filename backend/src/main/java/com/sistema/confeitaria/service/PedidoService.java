package com.sistema.confeitaria.service;

import com.sistema.confeitaria.model.Pedido;
import com.sistema.confeitaria.model.ItemPedido;
import com.sistema.confeitaria.dto.PedidoResumoDTO;
import com.sistema.confeitaria.dto.PedidoDashboardDTO;
import com.sistema.confeitaria.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final int limiteDiario;

    public PedidoService(PedidoRepository pedidoRepository, 
                         @Value("${confeitaria.limite-pedidos-diario}") int limiteDiario) {
        this.pedidoRepository = pedidoRepository;
        this.limiteDiario = limiteDiario;
    }

    public boolean verificarDisponibilidade(LocalDate data) {
        return pedidoRepository.countByDataEncomenda(data) < limiteDiario;
    }

    public boolean verificarHorarioDisponivel(LocalDate data, LocalTime horario) {
        return !pedidoRepository.existsByDataEncomendaAndHorarioEncomenda(data, horario);
    }

    @Transactional
    public Pedido salvar(Pedido pedido) {
        if (!verificarDisponibilidade(pedido.getDataEncomenda())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Agenda lotada para este dia!");
        }

        if (!verificarHorarioDisponivel(pedido.getDataEncomenda(), pedido.getHorarioEncomenda())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este horário já foi reservado por outro cliente.");
        }

        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedido);
            }
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoDashboardDTO> listarTodos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(this::converterParaDashboardDTO)
                .collect(Collectors.toList());
    }

    public Page<PedidoResumoDTO> listarPedidosPaginados(Pageable pageable) {
        Page<Pedido> pedidos = pedidoRepository.findAll(pageable);
        return pedidos.map(PedidoResumoDTO::new); 
    }

    @Transactional
    public PedidoDashboardDTO atualizarStatus(Long id, String status) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado!"));
        
        pedido.setStatus(status.toUpperCase());
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        
        return converterParaDashboardDTO(pedidoAtualizado);
    }

    private PedidoDashboardDTO converterParaDashboardDTO(Pedido pedido) {
        PedidoDashboardDTO.ClienteDTO clienteDTO = new PedidoDashboardDTO.ClienteDTO(
            pedido.getCliente() != null ? pedido.getCliente().getNome() : "Cliente Não Informado",
            pedido.getCliente() != null ? pedido.getCliente().getTelefone() : "-",
            pedido.getCliente() != null ? pedido.getCliente().getEndereco() : "N/A"
        );

        List<PedidoDashboardDTO.ItemPedidoDTO> itensDTO = new ArrayList<>();
        if (pedido.getItens() != null) {
            itensDTO = pedido.getItens().stream().map(item -> {
                
                // LÓGICA DO SABOR AQUI
                String nomeProduto = item.getProduto() != null ? item.getProduto().getNome() : "Produto Não Identificado";
                if (item.getSabor() != null && !item.getSabor().trim().isEmpty()) {
                    nomeProduto += " - " + item.getSabor();
                }

                PedidoDashboardDTO.ProdutoDTO produtoDTO = new PedidoDashboardDTO.ProdutoDTO(nomeProduto);
                
                return new PedidoDashboardDTO.ItemPedidoDTO(
                    produtoDTO,
                    item.getQuantidade(),
                    item.getPrecoPraticado()
                );
            }).collect(Collectors.toList());
        }

        return new PedidoDashboardDTO(
            pedido.getId(),
            pedido.getDataEncomenda(),
            pedido.getHorarioEncomenda(),
            pedido.getValorTotal(),
            pedido.getStatus(),
            clienteDTO,
            itensDTO
        );
    }
}
package com.sistema.confeitaria.service;

import com.sistema.confeitaria.model.Pedido;
import com.sistema.confeitaria.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

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

    public Pedido salvar(Pedido pedido) {
        if (!verificarDisponibilidade(pedido.getDataEncomenda())) {
            throw new RuntimeException("Agenda lotada para este dia!");
        }
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }
}
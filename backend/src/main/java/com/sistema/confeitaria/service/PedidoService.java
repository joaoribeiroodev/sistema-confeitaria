package com.sistema.confeitaria.service;

import com.sistema.confeitaria.model.Pedido;
import com.sistema.confeitaria.dto.PedidoResumoDTO;
import com.sistema.confeitaria.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public boolean verificarHorarioDisponivel(LocalDate data, LocalTime horario) {
        // Retorna true se NÃO existir um pedido nesse dia e horário
        return !pedidoRepository.existsByDataEncomendaAndHorarioEncomenda(data, horario);
    }

    public Pedido salvar(Pedido pedido) {
        // 1. Verifica se a agenda do dia já está lotada
        if (!verificarDisponibilidade(pedido.getDataEncomenda())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Agenda lotada para este dia!");
        }

        // 2. NOVO: Verifica se o horário exato já foi reservado
        if (!verificarHorarioDisponivel(pedido.getDataEncomenda(), pedido.getHorarioEncomenda())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este horário já foi reservado por outro cliente. Por favor, escolha outro horário.");
        }

        // Se passar pelas duas validações, salva o pedido!
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // Método de paginação movido para o lugar certo com segurança:
    public Page<PedidoResumoDTO> listarPedidosPaginados(Pageable pageable) {
        Page<Pedido> pedidos = pedidoRepository.findAll(pageable);
        // Transforma a Entidade no DTO limpo
        return pedidos.map(PedidoResumoDTO::new); 
    }
}
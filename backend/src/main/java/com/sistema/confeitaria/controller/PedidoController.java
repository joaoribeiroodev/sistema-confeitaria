package com.sistema.confeitaria.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import com.sistema.confeitaria.model.Pedido;
import com.sistema.confeitaria.dto.PedidoDashboardDTO;
import com.sistema.confeitaria.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody Pedido pedido) {
        try {
            return ResponseEntity.ok(pedidoService.salvar(pedido));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/validar-data")
    public ResponseEntity<Boolean> validarData(@RequestParam String data) {
        LocalDate localDate = LocalDate.parse(data);
        return ResponseEntity.ok(pedidoService.verificarDisponibilidade(localDate));
    }

    // Retorna List<PedidoDashboardDTO> em vez de List<Pedido>
    @GetMapping("/admin/listar")
    public ResponseEntity<List<PedidoDashboardDTO>> listarParaAdmin() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/validar-horario")
    public ResponseEntity<Boolean> validarHorario(@RequestParam String data, @RequestParam String horario) {
        try {
            LocalDate localDate = LocalDate.parse(data);
            java.time.LocalTime localTime = java.time.LocalTime.parse(horario.length() == 5 ? horario + ":00" : horario);
            
            boolean disponivel = pedidoService.verificarHorarioDisponivel(localDate, localTime);
            return ResponseEntity.ok(disponivel);
        } catch (Exception e) {
            return ResponseEntity.ok(true);
        }
    }

    @GetMapping("/verificar-horario")
    public ResponseEntity<Boolean> verificarHorario(
        @RequestParam LocalDate data, 
        @RequestParam LocalTime horario) {
    
        boolean disponivel = pedidoService.verificarHorarioDisponivel(data, horario);
        return ResponseEntity.ok(disponivel);
    }

    // O retorno agora entrega o DTO mapeado com segurança
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            return ResponseEntity.ok(pedidoService.atualizarStatus(id, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
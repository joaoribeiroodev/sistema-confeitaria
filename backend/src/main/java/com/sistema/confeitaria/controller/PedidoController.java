package com.sistema.confeitaria.controller;

import com.sistema.confeitaria.model.Pedido;
import com.sistema.confeitaria.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/admin/listar")
    public ResponseEntity<List<Pedido>> listarParaAdmin() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }
}
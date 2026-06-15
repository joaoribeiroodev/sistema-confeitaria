package com.sistema.confeitaria.controller;

import com.sistema.confeitaria.dto.AgendaBloqueioDTO;
import com.sistema.confeitaria.dto.AgendaDiaDTO;
import com.sistema.confeitaria.dto.BloquearAgendaRequest;
import com.sistema.confeitaria.service.AgendaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @GetMapping("/api/pedidos/slots-disponiveis")
    public ResponseEntity<List<String>> listarSlotsDisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(agendaService.listarSlotsDisponiveis(data));
    }

    @GetMapping("/api/admin/agenda")
    public ResponseEntity<AgendaDiaDTO> obterStatusDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(agendaService.obterStatusDia(data));
    }

    @GetMapping("/api/admin/agenda/bloqueios")
    public ResponseEntity<List<AgendaBloqueioDTO>> listarBloqueios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(agendaService.listarBloqueios(inicio, fim));
    }

    @PostMapping("/api/admin/agenda/bloquear")
    public ResponseEntity<AgendaBloqueioDTO> bloquear(@RequestBody BloquearAgendaRequest request) {
        return ResponseEntity.ok(agendaService.bloquear(request.getData(), request.getHorario(), request.getMotivo()));
    }

    @PostMapping("/api/admin/agenda/desbloquear")
    public ResponseEntity<AgendaBloqueioDTO> desbloquear(@RequestBody BloquearAgendaRequest request) {
        return ResponseEntity.ok(agendaService.desbloquear(request.getData(), request.getHorario()));
    }

    @PutMapping("/api/admin/agenda/bloqueios/{id}/alternar")
    public ResponseEntity<AgendaBloqueioDTO> alternarBloqueio(@PathVariable Long id) {
        return ResponseEntity.ok(agendaService.alternarBloqueio(id));
    }
}

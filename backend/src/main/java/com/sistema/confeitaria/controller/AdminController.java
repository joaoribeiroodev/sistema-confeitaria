package com.sistema.confeitaria.controller;

import com.sistema.confeitaria.dto.DashboardMetricsDTO;
import com.sistema.confeitaria.service.DashboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final DashboardService dashboardService;

    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metricas")
    public ResponseEntity<DashboardMetricsDTO> getMetricas() {
        return ResponseEntity.ok(dashboardService.calcularMetricasMensais());
    }

    @GetMapping("/historico")
    public ResponseEntity<Page<?>> getHistorico(Pageable pageable) {
        return ResponseEntity.ok(dashboardService.listarPedidosPaginados(pageable));
    }

    @GetMapping("/exportar-csv")
    public ResponseEntity<byte[]> exportarCsv() {
        String csvData = dashboardService.gerarCsvMesCorrente();
        byte[] csvBytes = csvData.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-vendas.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }
}

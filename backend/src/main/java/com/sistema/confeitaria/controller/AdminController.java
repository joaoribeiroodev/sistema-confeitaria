package com.sistema.confeitaria.controller;

import com.sistema.confeitaria.dto.DashboardMetricsDTO;
import com.sistema.confeitaria.model.Pedido;
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
@CrossOrigin(origins = "*") // Nota: Em produção, substitua "*" pelo domínio real do seu frontend Angular
public class AdminController {

    private final DashboardService dashboardService;

    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metricas")
    public ResponseEntity<DashboardMetricsDTO> getMetricas() {
        return ResponseEntity.ok(dashboardService.calcularMetricasMensais());
    }

    // Melhoria: Tipagem explícita no lugar do curinga <?>
    @GetMapping("/historico")
    public ResponseEntity<Page<Pedido>> getHistorico(Pageable pageable) {
        return ResponseEntity.ok(dashboardService.listarPedidosPaginados(pageable));
    }

    @GetMapping("/exportar-csv")
    public ResponseEntity<byte[]> exportarCsv() {
        String csvData = dashboardService.gerarCsvMesCorrente();
        
        // Melhoria: Adicionado BOM (Byte Order Mark) para o Excel reconhecer acentos perfeitamente
        byte[] utf8Bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] csvBytes = csvData.getBytes(StandardCharsets.UTF_8);
        
        byte[] csvComBom = new byte[utf8Bom.length + csvBytes.length];
        System.arraycopy(utf8Bom, 0, csvComBom, 0, utf8Bom.length);
        System.arraycopy(csvBytes, 0, csvComBom, utf8Bom.length, csvBytes.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-vendas.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvComBom);
    }
}
package com.sistema.confeitaria.service;

import com.sistema.confeitaria.dto.DashboardMetricsDTO;
import com.sistema.confeitaria.model.Pedido;
import com.sistema.confeitaria.repository.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final PedidoRepository pedidoRepository;

    public DashboardService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public DashboardMetricsDTO calcularMetricasMensais() {
        // Uso do YearMonth para deixar o código mais seguro e sem cálculo manual de dias
        LocalDate inicioMes = YearMonth.now().atDay(1);
        LocalDate fimMes = YearMonth.now().atEndOfMonth();

        BigDecimal faturamento = pedidoRepository.calcularFaturamentoPeriodo(inicioMes, fimMes);
        if (faturamento == null) faturamento = BigDecimal.ZERO;

        List<Map<String, Object>> picos = mapResultSet(
            pedidoRepository.buscarPicosDeVenda(inicioMes, fimMes), 
            new String[]{"data", "vendas"}
        );
        List<Map<String, Object>> maisVendidos = mapResultSet(
            pedidoRepository.buscarProdutosMaisVendidos(inicioMes, fimMes), 
            new String[]{"nome", "quantidade"}
        );

        return new DashboardMetricsDTO(faturamento, picos, maisVendidos);
    }

    // Alterado para Page<Pedido> alinhando com as boas práticas do Controller
    public Page<Pedido> listarPedidosPaginados(Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }

    public String gerarCsvMesCorrente() {
        LocalDate inicioMes = YearMonth.now().atDay(1);
        LocalDate fimMes = YearMonth.now().atEndOfMonth();

        List<Pedido> pedidosDoMes = pedidoRepository.findByDataEncomendaBetween(inicioMes, fimMes);

        StringBuilder csv = new StringBuilder();
        csv.append("ID Pedido;Cliente;Data Encomenda;Horario;Valor Total\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Pedido pedido : pedidosDoMes) {
            String nomeCliente = pedido.getCliente() != null ? pedido.getCliente().getNome() : "-";
            
            // Formatando o decimal de ponto para vírgula (Padrão PT-BR no Excel)
            String valorTotalFormatado = pedido.getValorTotal() != null ? 
                pedido.getValorTotal().toString().replace(".", ",") : "0,00";

            csv.append(pedido.getId()).append(";")
               .append(nomeCliente).append(";")
               .append(pedido.getDataEncomenda().format(formatter)).append(";")
               .append(pedido.getHorarioEncomenda()).append(";")
               .append(valorTotalFormatado).append("\n");
        }
        return csv.toString();
    }

    private List<Map<String, Object>> mapResultSet(List<Object[]> rows, String[] keys) {
        List<Map<String, Object>> mapped = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> entry = new HashMap<>();
            for (int i = 0; i < row.length && i < keys.length; i++) {
                entry.put(keys[i], row[i]);
            }
            mapped.add(entry);
        }
        return mapped;
    }
}
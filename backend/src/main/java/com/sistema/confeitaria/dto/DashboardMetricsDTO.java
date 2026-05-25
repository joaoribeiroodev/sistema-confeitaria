package com.sistema.confeitaria.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardMetricsDTO(
    BigDecimal faturamentoMensal,
    List<Map<String, Object>> picosVenda,
    List<Map<String, Object>> produtosMaisVendidos
) {}
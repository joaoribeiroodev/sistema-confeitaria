package com.sistema.confeitaria.repository;

import com.sistema.confeitaria.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    long countByDataEncomenda(LocalDate dataEncomenda);

    // 1. Método automático do Spring para buscar pedidos entre duas datas
    List<Pedido> findByDataEncomendaBetween(LocalDate inicio, LocalDate fim);

    // 🌟 NOVO: Busca e ordena todos os pedidos do intervalo para compor as linhas do Excel
    List<Pedido> findByDataEncomendaBetweenOrderByDataEncomendaAscHorarioEncomendaAsc(LocalDate inicio, LocalDate fim);

    // 2. Adicionado "AND p.status <> 'CANCELADO'" para ignorar pedidos cancelados no faturamento
    @Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.dataEncomenda BETWEEN :inicio AND :fim AND p.status <> 'CANCELADO'")
    BigDecimal calcularFaturamentoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // 3. Agrupa as vendas por data para achar o dia com maior pico (Ignorando cancelados)
    @Query("SELECT p.dataEncomenda, COUNT(p.id) FROM Pedido p WHERE p.dataEncomenda BETWEEN :inicio AND :fim AND p.status <> 'CANCELADO' GROUP BY p.dataEncomenda ORDER BY COUNT(p.id) DESC")
    List<Object[]> buscarPicosDeVenda(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // 4. Adicionado "AND p.status <> 'CANCELADO'" na query nativa para não contar produtos de pedidos cancelados
    @Query(value = "SELECT pr.nome AS nome, SUM(ip.quantidade) AS quantidade " +
            "FROM itens_pedido ip " +
            "JOIN produtos pr ON ip.produto_id = pr.id " +
            "JOIN pedidos p ON ip.pedido_id = p.id " +
            "WHERE p.data_encomenda BETWEEN :inicio AND :fim " +
            "AND p.status <> 'CANCELADO' " + 
            "GROUP BY pr.nome ORDER BY quantidade DESC LIMIT 5", nativeQuery = true)
    List<Object[]> buscarProdutosMaisVendidos(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    boolean existsByDataEncomendaAndHorarioEncomenda(LocalDate dataEncomenda, LocalTime horarioEncomenda);
}
package com.sistema.confeitaria.repository;

import com.sistema.confeitaria.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    long countByDataEncomenda(LocalDate dataEncomenda);

    // 1. Método automático do Spring para buscar pedidos entre duas datas
    List<Pedido> findByDataEncomendaBetween(LocalDate inicio, LocalDate fim);

    // 2. Calcula a soma do valor total dos pedidos no período
    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.dataEncomenda BETWEEN :inicio AND :fim")
    BigDecimal calcularFaturamentoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // 3. Agrupa as vendas por data para achar o dia com maior pico
    @Query("SELECT p.dataEncomenda, COUNT(p.id) FROM Pedido p WHERE p.dataEncomenda BETWEEN :inicio AND :fim GROUP BY p.dataEncomenda ORDER BY COUNT(p.id) DESC")
    List<Object[]> buscarPicosDeVenda(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // 4. Busca os produtos mais vendidos por quantidade (Agora separando por sabor, caso o produto tenha)
    @Query(value = "SELECT CASE WHEN ip.sabor IS NOT NULL THEN CONCAT(pr.nome, ' (', ip.sabor, ')') ELSE pr.nome END AS nome, SUM(ip.quantidade) AS quantidade " +
            "FROM itens_pedido ip " +
            "JOIN produtos pr ON ip.produto_id = pr.id " +
            "JOIN pedidos p ON ip.pedido_id = p.id " +
            "WHERE p.data_encomenda BETWEEN :inicio AND :fim " +
            "GROUP BY nome ORDER BY quantidade DESC LIMIT 5", nativeQuery = true)
    List<Object[]> buscarProdutosMaisVendidos(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
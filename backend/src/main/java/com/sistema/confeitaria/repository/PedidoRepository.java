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

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) FROM Pedido p WHERE p.dataEncomenda = :data AND p.status <> 'CANCELADO'")
    long countAtivosByDataEncomenda(@org.springframework.data.repository.query.Param("data") LocalDate data);

    @org.springframework.data.jpa.repository.Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pedido p WHERE p.dataEncomenda = :data AND p.horarioEncomenda = :horario AND p.status <> 'CANCELADO'")
    boolean existsAtivoByDataEncomendaAndHorarioEncomenda(
            @org.springframework.data.repository.query.Param("data") LocalDate data,
            @org.springframework.data.repository.query.Param("horario") LocalTime horario);

    // 1. Método automático do Spring para buscar pedidos entre duas datas
    List<Pedido> findByDataEncomendaBetween(LocalDate inicio, LocalDate fim);

    // 2. Busca e ordena todos os pedidos do intervalo para compor as linhas do Excel
    List<Pedido> findByDataEncomendaBetweenOrderByDataEncomendaAscHorarioEncomendaAsc(LocalDate inicio, LocalDate fim);

    // 3. Soma o faturamento estritamente dos pedidos ENTREGUES
    @Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.dataEncomenda BETWEEN :inicio AND :fim AND p.status = 'ENTREGUE'")
    BigDecimal calcularFaturamentoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // 4. Agrupa os picos de venda considerando apenas os pedidos ENTREGUES
    @Query("SELECT p.dataEncomenda, COUNT(p.id) FROM Pedido p WHERE p.dataEncomenda BETWEEN :inicio AND :fim AND p.status = 'ENTREGUE' GROUP BY p.dataEncomenda ORDER BY COUNT(p.id) DESC")
    List<Object[]> buscarPicosDeVenda(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // 5. Busca o Top 5 produtos mais vendidos considerando apenas pedidos ENTREGUES
    @Query(value = "SELECT pr.nome AS nome, SUM(ip.quantidade) AS quantidade " +
            "FROM itens_pedido ip " +
            "JOIN produtos pr ON ip.produto_id = pr.id " +
            "JOIN pedidos p ON ip.pedido_id = p.id " +
            "WHERE p.data_encomenda BETWEEN :inicio AND :fim " +
            "AND p.status = 'ENTREGUE' " + 
            "GROUP BY pr.nome ORDER BY quantidade DESC LIMIT 5", nativeQuery = true)
    List<Object[]> buscarProdutosMaisVendidos(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // 6. Busca o produto Top 1 considerando apenas pedidos ENTREGUES
    @Query(value = "SELECT pr.nome " +
            "FROM itens_pedido ip " +
            "JOIN produtos pr ON ip.produto_id = pr.id " +
            "JOIN pedidos p ON ip.pedido_id = p.id " +
            "WHERE p.data_encomenda BETWEEN :inicio AND :fim " +
            "AND p.status = 'ENTREGUE' " + 
            "GROUP BY pr.id, pr.nome ORDER BY SUM(ip.quantidade) DESC LIMIT 1", nativeQuery = true)
    String buscarProdutoMaisVendidoTop1(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    boolean existsByDataEncomendaAndHorarioEncomenda(LocalDate dataEncomenda, LocalTime horarioEncomenda);
}
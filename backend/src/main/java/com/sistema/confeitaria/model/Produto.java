package com.sistema.confeitaria.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "produtos")
@Data
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    private Double precoUnitario;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;
}
package com.sistema.confeitaria.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "agenda_bloqueios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgendaBloqueio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column
    private LocalTime horario;

    @Column(length = 255)
    private String motivo;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", updatable = false, insertable = false)
    private LocalDateTime criadoEm;
}
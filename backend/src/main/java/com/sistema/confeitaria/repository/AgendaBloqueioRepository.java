package com.sistema.confeitaria.repository;

import com.sistema.confeitaria.model.AgendaBloqueio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaBloqueioRepository extends JpaRepository<AgendaBloqueio, Long> {

    List<AgendaBloqueio> findByDataBetweenOrderByDataAscHorarioAsc(LocalDate inicio, LocalDate fim);

    List<AgendaBloqueio> findByDataOrderByHorarioAsc(LocalDate data);

    boolean existsByDataAndHorarioIsNullAndAtivoTrue(LocalDate data);

    boolean existsByDataAndHorarioAndAtivoTrue(LocalDate data, LocalTime horario);

    Optional<AgendaBloqueio> findByDataAndHorarioIsNull(LocalDate data);

    Optional<AgendaBloqueio> findByDataAndHorario(LocalDate data, LocalTime horario);
}

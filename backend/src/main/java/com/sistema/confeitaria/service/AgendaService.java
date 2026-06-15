package com.sistema.confeitaria.service;

import com.sistema.confeitaria.dto.AgendaBloqueioDTO;
import com.sistema.confeitaria.dto.AgendaDiaDTO;
import com.sistema.confeitaria.dto.SlotAgendaDTO;
import com.sistema.confeitaria.model.AgendaBloqueio;
import com.sistema.confeitaria.repository.AgendaBloqueioRepository;
import com.sistema.confeitaria.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    private static final List<LocalTime> HORARIOS_PADRAO = List.of(
        LocalTime.of(7, 0), LocalTime.of(8, 0), LocalTime.of(9, 0),
        LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(12, 0),
        LocalTime.of(13, 0), LocalTime.of(14, 0), LocalTime.of(15, 0),
        LocalTime.of(16, 0), LocalTime.of(17, 0), LocalTime.of(18, 0)
    );

    private final AgendaBloqueioRepository agendaBloqueioRepository;
    private final PedidoRepository pedidoRepository;
    private final int limiteDiario;

    public AgendaService(AgendaBloqueioRepository agendaBloqueioRepository,
                         PedidoRepository pedidoRepository,
                         @Value("${confeitaria.limite-pedidos-diario}") int limiteDiario) {
        this.agendaBloqueioRepository = agendaBloqueioRepository;
        this.pedidoRepository = pedidoRepository;
        this.limiteDiario = limiteDiario;
    }

    public boolean isDataDisponivel(LocalDate data) {
        if (data.isBefore(LocalDate.now())) {
            return false;
        }
        if (isDiaBloqueado(data)) {
            return false;
        }
        return pedidoRepository.countAtivosByDataEncomenda(data) < limiteDiario;
    }

    public boolean isHorarioDisponivel(LocalDate data, LocalTime horario) {
        if (!isDataDisponivel(data)) {
            return false;
        }
        if (isHorarioBloqueado(data, horario)) {
            return false;
        }
        return !pedidoRepository.existsAtivoByDataEncomendaAndHorarioEncomenda(data, horario);
    }

    public List<String> listarSlotsDisponiveis(LocalDate data) {
        if (!isDataDisponivel(data)) {
            return List.of();
        }
        return HORARIOS_PADRAO.stream()
            .filter(horario -> isHorarioDisponivel(data, horario))
            .map(horario -> String.format("%02d:%02d", horario.getHour(), horario.getMinute()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgendaDiaDTO obterStatusDia(LocalDate data) {
        AgendaDiaDTO dto = new AgendaDiaDTO();
        dto.setData(data);
        dto.setLimiteDiario(limiteDiario);

        int pedidosAtivos = (int) pedidoRepository.countAtivosByDataEncomenda(data);
        dto.setPedidosAtivos(pedidosAtivos);
        dto.setDiaLotado(pedidosAtivos >= limiteDiario);

        Optional<AgendaBloqueio> bloqueioDia = agendaBloqueioRepository.findByDataAndHorarioIsNull(data);
        boolean diaBloqueado = bloqueioDia.map(b -> Boolean.TRUE.equals(b.getAtivo())).orElse(false);
        dto.setDiaBloqueado(diaBloqueado);
        bloqueioDia.filter(b -> Boolean.TRUE.equals(b.getAtivo()))
            .ifPresent(b -> dto.setBloqueioDiaId(b.getId()));

        List<AgendaBloqueio> bloqueiosHorario = agendaBloqueioRepository.findByDataOrderByHorarioAsc(data);

        List<SlotAgendaDTO> slots = new ArrayList<>();
        for (LocalTime horario : HORARIOS_PADRAO) {
            String status;
            Long bloqueioId = null;

            if (diaBloqueado) {
                status = "BLOQUEADO";
            } else if (pedidosAtivos >= limiteDiario) {
                status = "LOTADO";
            } else {
                Optional<AgendaBloqueio> bloqueioHorario = bloqueiosHorario.stream()
                    .filter(b -> horario.equals(b.getHorario()))
                    .findFirst();

                if (bloqueioHorario.isPresent() && Boolean.TRUE.equals(bloqueioHorario.get().getAtivo())) {
                    status = "BLOQUEADO";
                    bloqueioId = bloqueioHorario.get().getId();
                } else if (pedidoRepository.existsAtivoByDataEncomendaAndHorarioEncomenda(data, horario)) {
                    status = "OCUPADO";
                } else {
                    status = "DISPONIVEL";
                    if (bloqueioHorario.isPresent()) {
                        AgendaBloqueio b = bloqueioHorario.get();
                        if (!Boolean.TRUE.equals(b.getAtivo())) {
                            bloqueioId = b.getId();
                        }
                    }
                }
            }

            slots.add(new SlotAgendaDTO(horario, status, bloqueioId));
        }

        dto.setHorarios(slots);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<AgendaBloqueioDTO> listarBloqueios(LocalDate inicio, LocalDate fim) {
        return agendaBloqueioRepository.findByDataBetweenOrderByDataAscHorarioAsc(inicio, fim)
            .stream()
            .map(AgendaBloqueioDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public AgendaBloqueioDTO bloquear(LocalDate data, LocalTime horario, String motivo) {
        if (data.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível bloquear datas passadas.");
        }

        if (horario == null) {
            return bloquearDiaInteiro(data, motivo);
        }

        if (isDiaBloqueado(data)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O dia inteiro já está bloqueado.");
        }

        AgendaBloqueio bloqueio = agendaBloqueioRepository.findByDataAndHorario(data, horario)
            .orElseGet(() -> {
                AgendaBloqueio novo = new AgendaBloqueio();
                novo.setData(data);
                novo.setHorario(horario);
                return novo;
            });

        bloqueio.setAtivo(true);
        bloqueio.setMotivo(motivo);
        return new AgendaBloqueioDTO(agendaBloqueioRepository.save(bloqueio));
    }

    @Transactional
    public AgendaBloqueioDTO desbloquear(LocalDate data, LocalTime horario) {
        if (horario == null) {
            return desbloquearDiaInteiro(data);
        }

        AgendaBloqueio bloqueio = agendaBloqueioRepository.findByDataAndHorario(data, horario)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloqueio não encontrado."));

        bloqueio.setAtivo(false);
        return new AgendaBloqueioDTO(agendaBloqueioRepository.save(bloqueio));
    }

    @Transactional
    public AgendaBloqueioDTO alternarBloqueio(Long id) {
        AgendaBloqueio bloqueio = agendaBloqueioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloqueio não encontrado."));

        bloqueio.setAtivo(!Boolean.TRUE.equals(bloqueio.getAtivo()));
        return new AgendaBloqueioDTO(agendaBloqueioRepository.save(bloqueio));
    }

    private AgendaBloqueioDTO bloquearDiaInteiro(LocalDate data, String motivo) {
        AgendaBloqueio bloqueio = agendaBloqueioRepository.findByDataAndHorarioIsNull(data)
            .orElseGet(() -> {
                AgendaBloqueio novo = new AgendaBloqueio();
                novo.setData(data);
                novo.setHorario(null);
                return novo;
            });

        bloqueio.setAtivo(true);
        bloqueio.setMotivo(motivo);
        return new AgendaBloqueioDTO(agendaBloqueioRepository.save(bloqueio));
    }

    private AgendaBloqueioDTO desbloquearDiaInteiro(LocalDate data) {
        AgendaBloqueio bloqueio = agendaBloqueioRepository.findByDataAndHorarioIsNull(data)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloqueio de dia não encontrado."));

        bloqueio.setAtivo(false);
        return new AgendaBloqueioDTO(agendaBloqueioRepository.save(bloqueio));
    }

    private boolean isDiaBloqueado(LocalDate data) {
        return agendaBloqueioRepository.existsByDataAndHorarioIsNullAndAtivoTrue(data);
    }

    private boolean isHorarioBloqueado(LocalDate data, LocalTime horario) {
        return agendaBloqueioRepository.existsByDataAndHorarioAndAtivoTrue(data, horario);
    }
}
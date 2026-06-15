package com.sistema.confeitaria.dto;

import java.time.LocalTime;

public class SlotAgendaDTO {

    private LocalTime horario;
    private String status;
    private Long bloqueioId;

    public SlotAgendaDTO() {}

    public SlotAgendaDTO(LocalTime horario, String status, Long bloqueioId) {
        this.horario = horario;
        this.status = status;
        this.bloqueioId = bloqueioId;
    }

    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getBloqueioId() { return bloqueioId; }
    public void setBloqueioId(Long bloqueioId) { this.bloqueioId = bloqueioId; }
}

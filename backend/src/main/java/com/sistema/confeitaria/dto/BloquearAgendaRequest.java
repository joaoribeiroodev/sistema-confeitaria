package com.sistema.confeitaria.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BloquearAgendaRequest {

    private LocalDate data;
    private LocalTime horario;
    private String motivo;

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}

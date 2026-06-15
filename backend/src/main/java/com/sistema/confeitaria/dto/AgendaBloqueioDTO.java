package com.sistema.confeitaria.dto;

import com.sistema.confeitaria.model.AgendaBloqueio;
import java.time.LocalDate;
import java.time.LocalTime;

public class AgendaBloqueioDTO {

    private Long id;
    private LocalDate data;
    private LocalTime horario;
    private String motivo;
    private Boolean ativo;
    private String tipo;

    public AgendaBloqueioDTO() {}

    public AgendaBloqueioDTO(AgendaBloqueio bloqueio) {
        this.id = bloqueio.getId();
        this.data = bloqueio.getData();
        this.horario = bloqueio.getHorario();
        this.motivo = bloqueio.getMotivo();
        this.ativo = bloqueio.getAtivo();
        this.tipo = bloqueio.getHorario() == null ? "DIA_INTEIRO" : "HORARIO";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}

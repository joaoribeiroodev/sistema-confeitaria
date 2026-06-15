package com.sistema.confeitaria.dto;

import java.time.LocalDate;
import java.util.List;

public class AgendaDiaDTO {

    private LocalDate data;
    private boolean diaBloqueado;
    private boolean diaLotado;
    private int pedidosAtivos;
    private int limiteDiario;
    private Long bloqueioDiaId;
    private List<SlotAgendaDTO> horarios;

    public AgendaDiaDTO() {}

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public boolean isDiaBloqueado() { return diaBloqueado; }
    public void setDiaBloqueado(boolean diaBloqueado) { this.diaBloqueado = diaBloqueado; }

    public boolean isDiaLotado() { return diaLotado; }
    public void setDiaLotado(boolean diaLotado) { this.diaLotado = diaLotado; }

    public int getPedidosAtivos() { return pedidosAtivos; }
    public void setPedidosAtivos(int pedidosAtivos) { this.pedidosAtivos = pedidosAtivos; }

    public int getLimiteDiario() { return limiteDiario; }
    public void setLimiteDiario(int limiteDiario) { this.limiteDiario = limiteDiario; }

    public Long getBloqueioDiaId() { return bloqueioDiaId; }
    public void setBloqueioDiaId(Long bloqueioDiaId) { this.bloqueioDiaId = bloqueioDiaId; }

    public List<SlotAgendaDTO> getHorarios() { return horarios; }
    public void setHorarios(List<SlotAgendaDTO> horarios) { this.horarios = horarios; }
}
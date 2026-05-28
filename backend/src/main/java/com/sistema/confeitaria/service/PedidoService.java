package com.sistema.confeitaria.service;

import com.sistema.confeitaria.model.Pedido;
import com.sistema.confeitaria.model.ItemPedido;
import com.sistema.confeitaria.dto.PedidoResumoDTO;
import com.sistema.confeitaria.dto.PedidoDashboardDTO;
import com.sistema.confeitaria.repository.PedidoRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final int limiteDiario;

    public PedidoService(PedidoRepository pedidoRepository, 
                         @Value("${confeitaria.limite-pedidos-diario}") int limiteDiario) {
        this.pedidoRepository = pedidoRepository;
        this.limiteDiario = limiteDiario;
    }

    public boolean verificarDisponibilidade(LocalDate data) {
        return pedidoRepository.countByDataEncomenda(data) < limiteDiario;
    }

    public boolean verificarHorarioDisponivel(LocalDate data, LocalTime horario) {
        return !pedidoRepository.existsByDataEncomendaAndHorarioEncomenda(data, horario);
    }

    @Transactional
    public Pedido salvar(Pedido pedido) {
        if (!verificarDisponibilidade(pedido.getDataEncomenda())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Agenda lotada para este dia!");
        }

        if (!verificarHorarioDisponivel(pedido.getDataEncomenda(), pedido.getHorarioEncomenda())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este horário já foi reservado por outro cliente.");
        }

        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedido);
            }
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoDashboardDTO> listarTodos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(this::converterParaDashboardDTO)
                .collect(Collectors.toList());
    }

    public Page<PedidoResumoDTO> listarPedidosPaginados(Pageable pageable) {
        Page<Pedido> pedidos = pedidoRepository.findAll(pageable);
        return pedidos.map(PedidoResumoDTO::new); 
    }

    @Transactional
    public PedidoDashboardDTO atualizarStatus(Long id, String status) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado!"));
        
        pedido.setStatus(status.toUpperCase());
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        
        return converterParaDashboardDTO(pedidoAtualizado);
    }

    // 🌟 NOVO MÉTODO: Geração inteligente do arquivo .XLSX com dados do mês e formato R$
    @Transactional(readOnly = true)
    public byte[] gerarRelatorioExcelMensal(int ano, int mes) throws IOException {
        LocalDate dataInicio = LocalDate.of(ano, mes, 1);
        LocalDate dataFim = dataInicio.withDayOfMonth(dataInicio.lengthOfMonth());

        List<Pedido> pedidos = pedidoRepository.findByDataEncomendaBetweenOrderByDataEncomendaAscHorarioEncomendaAsc(dataInicio, dataFim);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Relatório Mensal");
            sheet.setDisplayGridlines(true); // Garante que as linhas de grade apareçam no Excel

            // 1. Definição de Estilos e Fontes
            Font fonteCabecalho = workbook.createFont();
            fonteCabecalho.setFontName("Arial");
            fonteCabecalho.setFontHeightInPoints((short) 11);
            fonteCabecalho.setBold(true);
            fonteCabecalho.setColor(IndexedColors.WHITE.getIndex());

            CellStyle estiloCabecalho = workbook.createCellStyle();
            estiloCabecalho.setFont(fonteCabecalho);
            estiloCabecalho.setFillForegroundColor(IndexedColors.ROSE.getIndex()); // Cor elegante para a Nalva
            estiloCabecalho.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloCabecalho.setAlignment(HorizontalAlignment.CENTER);
            estiloCabecalho.setVerticalAlignment(VerticalAlignment.CENTER);

            // Estilos de Bordas Comuns
            CellStyle estiloBase = workbook.createCellStyle();
            Font fonteNormal = workbook.createFont();
            fonteNormal.setFontName("Arial");
            fonteNormal.setFontHeightInPoints((short) 10);
            estiloBase.setFont(fonteNormal);
            setBordasFinass(estiloBase);

            // Formato de Data
            CellStyle estiloData = workbook.createCellStyle();
            estiloData.cloneStyleFrom(estiloBase);
            estiloData.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));
            estiloData.setAlignment(HorizontalAlignment.CENTER);

            // Formato de Horário e Alinhamentos Centrais
            CellStyle estiloCentro = workbook.createCellStyle();
            estiloCentro.cloneStyleFrom(estiloBase);
            estiloCentro.setAlignment(HorizontalAlignment.CENTER);

            // 🌟 AQUI ESTÁ O FORMATO DE MOEDA NATIVO R$ (Mantém o número somável no Excel)
            CellStyle estiloMoeda = workbook.createCellStyle();
            estiloMoeda.cloneStyleFrom(estiloBase);
            estiloMoeda.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));
            estiloMoeda.setAlignment(HorizontalAlignment.RIGHT);

            // Estilo Zebrado (Fundo cinza bem claro para legibilidade)
            CellStyle estiloMoedaZebra = workbook.createCellStyle();
            estiloMoedaZebra.cloneStyleFrom(estiloMoeda);
            estiloMoedaZebra.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            estiloMoedaZebra.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 2. Criação do Cabeçalho
            String[] cabecalhos = {"ID Pedido", "Cliente", "WhatsApp", "Endereço Completo", "Data Encomenda", "Horário Encomenda", "Tipo Entrega", "Status", "Valor Total"};
            Row rowCabecalho = sheet.createRow(0);
            rowCabecalho.setHeightInPoints(24);

            for (int i = 0; i < cabecalhos.length; i++) {
                Cell cell = rowCabecalho.createCell(i);
                cell.setCellValue(cabecalhos[i]);
                cell.setCellStyle(estiloCabecalho);
            }

            // 3. Preenchimento das Linhas de Dados
            int rowNum = 1;
            DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Pedido pedido : pedidos) {
                Row row = sheet.createRow(rowNum++);
                boolean isZebra = (rowNum % 2 == 0);

                // Aplicação de estilos customizados para cada tipo de dado
                Cell cId = row.createCell(0);
                cId.setCellValue(pedido.getId());
                cId.setCellStyle(estiloCentro);

                Cell cNome = row.createCell(1);
                cNome.setCellValue(pedido.getCliente() != null ? pedido.getCliente().getNome() : "-");
                cNome.setCellStyle(estiloBase);

                Cell cTel = row.createCell(2);
                cTel.setCellValue(pedido.getCliente() != null ? pedido.getCliente().getTelefone() : "-");
                cTel.setCellStyle(estiloCentro);

                Cell cEnd = row.createCell(3);
                cEnd.setCellValue(pedido.getCliente() != null ? pedido.getCliente().getEndereco() : "-");
                cEnd.setCellStyle(estiloBase);

                Cell cData = row.createCell(4);
                if (pedido.getDataEncomenda() != null) {
                    cData.setCellValue(pedido.getDataEncomenda().format(formatterData));
                }
                cData.setCellStyle(estiloData);

                Cell cHora = row.createCell(5);
                cHora.setCellValue(pedido.getHorarioEncomenda() != null ? pedido.getHorarioEncomenda().toString() : "-");
                cHora.setCellStyle(estiloCentro);

                Cell cTipo = row.createCell(6);
                cTipo.setCellValue(pedido.getTipoEntrega() != null ? pedido.getTipoEntrega() : "ENTREGA");
                cTipo.setCellStyle(estiloCentro);

                Cell cStatus = row.createCell(7);
                cStatus.setCellValue(pedido.getStatus());
                cStatus.setCellStyle(estiloCentro);

                // Coluna do Faturamento em R$ (Passando como Double para o Excel aplicar a máscara)
                Cell cValor = row.createCell(8);
                cValor.setCellValue(pedido.getValorTotal() != null ? pedido.getValorTotal() : 0.0);
                cValor.setCellStyle(estiloMoeda);
            }

            // 4. Linha de Totalizador com Fórmula do Excel
            Row rowTotal = sheet.createRow(rowNum);
            Cell labelTotal = rowTotal.createCell(7);
            labelTotal.setCellValue("Total Geral:");
            Font fonteNegrito = workbook.createFont();
            fonteNegrito.setFontName("Arial");
            fonteNegrito.setBold(true);
            CellStyle estiloLabelTotal = workbook.createCellStyle();
            estiloLabelTotal.setFont(fonteNegrito);
            estiloLabelTotal.setAlignment(HorizontalAlignment.RIGHT);
            labelTotal.setCellStyle(estiloLabelTotal);

            Cell cellFormula = rowTotal.createCell(8);
            // Injeta a fórmula SUM de forma nativa dinâmica baseado na quantidade de linhas geradas
            cellFormula.setCellFormula("SUM(I2:I" + rowNum + ")"); 
            CellStyle estiloFormulaTotal = workbook.createCellStyle();
            estiloFormulaTotal.setFont(fonteNegrito);
            estiloFormulaTotal.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));
            estiloFormulaTotal.setAlignment(HorizontalAlignment.RIGHT);
            cellFormula.setCellStyle(estiloFormulaTotal);

            // Auto-ajuste automático do tamanho das colunas para não cortar os textos
            for (int i = 0; i < cabecalhos.length; i++) {
                sheet.autoSizeColumn(i);
                // Dá um espaçamento extra de segurança na largura
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1200);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void setBordasFinass(CellStyle estilo) {
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    }

    private PedidoDashboardDTO converterParaDashboardDTO(Pedido pedido) {
        PedidoDashboardDTO.ClienteDTO clienteDTO = new PedidoDashboardDTO.ClienteDTO(
            pedido.getCliente() != null ? pedido.getCliente().getNome() : "Cliente Não Informado",
            pedido.getCliente() != null ? pedido.getCliente().getTelefone() : "-",
            pedido.getCliente() != null ? pedido.getCliente().getEndereco() : "N/A"
        );

        List<PedidoDashboardDTO.ItemPedidoDTO> itensDTO = new ArrayList<>();
        if (pedido.getItens() != null) {
            itensDTO = pedido.getItens().stream().map(item -> {
                String nomeProduto = item.getProduto() != null ? item.getProduto().getNome() : "Produto Não Identificado";
                if (item.getSabor() != null && !item.getSabor().trim().isEmpty()) {
                    nomeProduto += " - " + item.getSabor();
                }

                PedidoDashboardDTO.ProdutoDTO produtoDTO = new PedidoDashboardDTO.ProdutoDTO(nomeProduto);
                
                return new PedidoDashboardDTO.ItemPedidoDTO(
                    produtoDTO,
                    item.getQuantidade(),
                    item.getPrecoPraticado()
                );
            }).collect(Collectors.toList());
        }

        return new PedidoDashboardDTO(
            pedido.getId(),
            pedido.getDataEncomenda(),
            pedido.getHorarioEncomenda(),
            pedido.getValorTotal(),
            pedido.getStatus(),
            clienteDTO,
            itensDTO
        );
    }
}
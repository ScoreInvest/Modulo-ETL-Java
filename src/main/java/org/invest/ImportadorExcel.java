package org.invest;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportadorExcel {

    private final List<List<Object>> dados = new ArrayList<>();

    public ImportadorExcel(String caminho) throws IOException {

        try (FileInputStream arquivo = new FileInputStream(caminho);
             Workbook workbook = WorkbookFactory.create(arquivo)) {

            Sheet planilha = workbook.getSheetAt(0);
            DataFormatter formatador = new DataFormatter();

            for (Row linha : planilha) {

                List<Object> valores = new ArrayList<>();

                for (int i = 0; i < linha.getLastCellNum(); i++) {

                    Cell celula = linha.getCell(
                            i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
                    );

                    if (celula == null) {
                        valores.add("");
                    } else {
                        valores.add(formatador.formatCellValue(celula));
                    }
                }

                dados.add(valores);
            }
        }
    }

    public List<List<Object>> getDados() {
        return dados;
    }

    public void exibirDados() {
        for (List<Object> linha : dados) {
            System.out.println(linha);
        }
    }
}
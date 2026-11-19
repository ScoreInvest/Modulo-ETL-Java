
package org.invest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private static final DateTimeFormatter FORMATADOR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final String ARQUIVO = "log_banco.txt";

    private static ExportadorLogs exportadorLogs;

    public static void configurar(ExportadorLogs exportador) {
        exportadorLogs = exportador;
    }

    public static void registrar(
            String level,
            String tabela,
            String detalhes
    ) {

        LocalDateTime agora = LocalDateTime.now();

        String descricao = "tabela: " + tabela + " | " + detalhes;

        String linha = String.format(
                "[%s] %s | %s",
                agora.format(FORMATADOR),
                level,
                descricao
        );

        System.out.println(linha);

        // Salva no arquivo TXT.
        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(ARQUIVO, true))) {

            writer.println(linha);

        } catch (IOException e) {

            System.out.println(
                    "Erro ao gravar o log: " + e.getMessage()
            );
        }

        // Salva no banco de dados.
        if (exportadorLogs != null) {

            RegistroLog registro = new RegistroLog(
                    agora,
                    level,
                    descricao
            );

            exportadorLogs.exportar(registro);
        }
    }
}

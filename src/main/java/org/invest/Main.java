
package org.invest;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        System.setProperty("log4j2.statusLoggerLevel", "OFF");

        // Estabelece conexão com o banco.
        JdbcTemplate jdbcTemplate = Conexao.conectar();

        // Inicializa os exportadores.
        ExportadorExcel exportadorExcel =
                new ExportadorExcel(jdbcTemplate);

        ExportadorLogs exportadorLogs =
                new ExportadorLogs(jdbcTemplate);

        // Configura o sistema de logs.
        Log.configurar(exportadorLogs);

        Log.registrar("INICIO", "-", "Sistema iniciado");

        String caminho = "cotacoes.xlsx";

        // Lê o Excel.
        ImportadorExcel leitor = new ImportadorExcel(caminho);

        List<List<Object>> dados = leitor.getDados();

        Log.registrar(
                "LEITURA",
                "-",
                "Arquivo " + caminho + " lido: " +
                        (dados.size() - 1) + " linha(s) de dados"
        );

        Log.registrar(
                "CONEXAO",
                "scoreinvest",
                "Conexão com o banco estabelecida"
        );

        // Importa as cotações.
        exportadorExcel.exportar(dados);

        Log.registrar("FIM", "-", "Sistema encerrado");
    }
}

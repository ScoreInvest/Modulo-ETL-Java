
package org.invest;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.File;
import java.util.UUID;
import java.io.IOException;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {

        NotificadorSlack slack = new NotificadorSlack();

        slack.enviarMensagem(
                "Iniciando a importação das cotações"
        );

        System.setProperty("log4j2.statusLoggerLevel", "OFF");

        S3Client s3Client = new S3Provider().getS3Client();

        String nomeBucket = "s3-scoreinvest-teste2";

        try {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(nomeBucket)
                    .build());
            System.out.println("Bucket criado!");
        } catch (BucketAlreadyOwnedByYouException e) {
            System.out.println("Bucket já existe, seguindo...");
        }

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(nomeBucket)
                        .key("cotacoes.xlsx")
                        .build(),
                RequestBody.fromFile(new File("cotacoes.xlsx")));
        System.out.println("Planilha enviada para o S3!");

        for (S3Object object : s3Client.listObjects(
                ListObjectsRequest.builder().bucket(nomeBucket).build()).contents()) {
            System.out.println("Objeto: " + object.key());
        }

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

        slack.enviarMensagem(
                "A importação das cotações foi concluída."
        );

        Log.registrar("FIM", "-", "Sistema encerrado");


    }
}

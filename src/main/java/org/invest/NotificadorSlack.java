
package org.invest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class NotificadorSlack {

    //TODO: ADICIONAR LINK WEBHOOK
    private static final String WEBHOOK_URL ="";

    private final HttpClient httpClient;

    public NotificadorSlack() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void enviarMensagem(String mensagem) {

        try {
            String json = "{\"text\":\"" + escaparJson(mensagem) + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WEBHOOK_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200
                    || !"ok".equals(response.body())) {

                throw new RuntimeException(
                        "Erro ao enviar mensagem ao Slack: "
                                + response.statusCode()
                                + " - " + response.body()
                );
            }

        } catch (Exception e) {
            System.err.println(
                    "Falha na notificação Slack: " + e.getMessage()
            );
        }
    }

    private String escaparJson(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

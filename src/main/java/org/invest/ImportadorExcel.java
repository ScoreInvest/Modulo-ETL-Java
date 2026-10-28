
package org.invest;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class ImportadorExcel {

    private final JdbcTemplate jdbcTemplate;
    private static final int MAX_INSERCOES = 1000;

    public ImportadorExcel(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void importar(List<List<Object>> dados) {

        String sql = """
        INSERT INTO cotacoes (
            data_cotacao,
            ticker,
            preco_abertura,
            preco_fechamento,
            preco_maximo,
            preco_minimo,
            volume
        )
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        int inseridos = 0;
        int erros = 0;

        for (int i = 1; i < dados.size(); i++) {

            if (inseridos >= MAX_INSERCOES) {
                Log.registrar("LIMITE", "cotacoes",
                        "Limite de " + MAX_INSERCOES + " inserções atingido, importação interrompida");
                break;
            }

            List<Object> linha = dados.get(i);

            try {
                if (linha.size() < 7) {
                    throw new IllegalArgumentException(
                            "Linha " + (i + 1) + " possui menos de 7 colunas."
                    );
                }

               String dataTexto = linha.get(0).toString().trim();
               LocalDate data = LocalDate.parse(dataTexto.substring(0, 10));
              
               String ticker = linha.get(1).toString().trim();

                BigDecimal abertura = decimal(linha.get(2));
                BigDecimal fechamento = decimal(linha.get(3));
                BigDecimal maxima = decimal(linha.get(4));
                BigDecimal minima = decimal(linha.get(5));
                BigDecimal volume = decimal(linha.get(6));

                jdbcTemplate.update(sql, Date.valueOf(data), ticker,
                        abertura, fechamento, maxima, minima, volume);

                inseridos++;
                Log.registrar("INSERT", "cotacoes",
                        "ticker=" + ticker + ", data=" + data);

            } catch (DuplicateKeyException e) {
                erros++;
                Log.registrar("ERRO", "cotacoes",
                        "Linha " + (i + 1) + ": cotação já cadastrada (data + ticker repetidos)");

            } catch (Exception e) {
                erros++;
                Log.registrar("ERRO", "cotacoes",
                        "Linha " + (i + 1) + ": " + e.getMessage());
            }
        }

        Log.registrar("IMPORTACAO", "cotacoes",
                inseridos + " inserido(s), " + erros + " erro(s)");
    }

    private BigDecimal decimal(Object valor) {

        String texto = valor.toString()
                .trim()
                .replace(",", ".");

        return new BigDecimal(texto);
    }
}

package org.invest;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;

public class ExportadorLogs extends Exportador<RegistroLog> {

    public ExportadorLogs(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void exportar(RegistroLog log) {

        String sql = """
            INSERT INTO logs (`timestamp`, `level`, `desc`)
            VALUES (?, ?, ?)
            """;

        try {
            jdbcTemplate.update(
                    sql,
                    Timestamp.valueOf(log.timestamp()),
                    log.level(),
                    log.descricao()
            );

        } catch (Exception e) {
            System.err.println(
                    "Erro ao salvar log no banco: " + e.getMessage()
            );
        }
    }
}

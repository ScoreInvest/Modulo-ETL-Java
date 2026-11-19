package org.invest;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class Exportador<T> {

    protected final JdbcTemplate jdbcTemplate;

    public Exportador(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public abstract void exportar(T dados);
}

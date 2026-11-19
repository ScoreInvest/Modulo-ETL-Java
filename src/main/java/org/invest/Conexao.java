package org.invest;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {

    public static JdbcTemplate conectar() {

        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setURL(
                "jdbc:mysql://localhost:3306/scoreinvest"
        );

        dataSource.setUser("dba");
        dataSource.setPassword("scoreinvest");

        return new JdbcTemplate(dataSource);
    }
}

package org.invest;

import java.time.LocalDateTime;

public record RegistroLog(
        LocalDateTime timestamp,
        String level,
        String descricao
) {
}

/*
 * Copyright (C) 2023 Lucas Nishimura < lucas at nishisan.dev >
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.nishisan.requests.common.spring.servlet.config;

import dev.nishisan.requests.common.spring.servlet.ExceptionStatusAdvice;
import dev.nishisan.requests.common.spring.servlet.ResponseStatusAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
        name = "nishi.requests.common.enabled",  // O nome da propriedade
        havingValue = "true",                    // Só ative se o valor for "true"
        matchIfMissing = true                    // ❗ ESSENCIAL: Se a propriedade não existir, considere como "true" (ativado por padrão)
)
public class NishiRequestsCommonAutoConfiguration {
    @Bean
    public ResponseStatusAdvice responseStatusAdvice() {
        return new ResponseStatusAdvice();
    }

    @Bean
    public ExceptionStatusAdvice exceptionStatusAdvice() {
        return new ExceptionStatusAdvice();
    }

}

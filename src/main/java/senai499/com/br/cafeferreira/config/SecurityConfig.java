
package senai499.com.br.cafeferreira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Criptografia das senhas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuração de acesso ao sistema
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // Páginas públicas
                        .requestMatchers(
                                "/",
                                "/login",
                                "/criar-conta",
                                "/css/**",
                                "/js/**",
                                "/imagens/**",
                                "/imagem/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Área administrativa
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Área dos funcionários
                        .requestMatchers("/funcionario/**")
                        .hasAnyRole("FUNCIONARIO", "ADMIN")

                        // Área dos clientes
                        .requestMatchers("/cliente/**")
                        .hasRole("CLIENTE")

                        .requestMatchers(
                                "/pedido",
                                "/cardapio",
                                "/pedidos/finalizar",
                                "/pedidos/finalizar/**"
                        ).permitAll()

                        // Demais páginas exigem autenticação
                        .anyRequest().authenticated()
                )

                // Configuração do login
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/login/sucesso", true)
                        .failureUrl("/login?erro=true")
                        .permitAll()
                )

                // Configuração do logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}

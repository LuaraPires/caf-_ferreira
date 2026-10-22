
package senai499.com.br.cafeferreira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // Páginas e arquivos públicos
                        .requestMatchers(
                                "/login",
                                "/criar-conta",
                                "/css/**",
                                "/js/**",
                                "/imagens/**",
                                "/imagem/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Acesso do administrador
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Acesso do funcionário
                        .requestMatchers("/funcionario/**")
                        .hasAnyRole("FUNCIONARIO", "ADMIN")

                        // Acesso do cliente
                        .requestMatchers(
                                "/pedidos/finalizar",
                                "/pedidos/finalizar/**",
                                "/cliente/**"
                        ).hasRole("CLIENTE")

                        // Demais rotas
                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("senha")

                        // Página aberta depois do login
                        .defaultSuccessUrl("/cliente/inicio", true)

                        .failureUrl("/login?erro")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}

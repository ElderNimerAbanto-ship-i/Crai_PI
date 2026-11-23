package Crai.crai.config;

import Crai.crai.exception.CustomAccessDeniedHandler;
import Crai.crai.exception.CustomAuthenticationEntryPoint;
import Crai.crai.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, AuthenticationProvider authenticationProvider,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.authenticationProvider = authenticationProvider;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // publicos
                        .requestMatchers("/error").permitAll()
                        // nueva linea para el despliegue
                        .requestMatchers("/", "/index.html", "/*.js", "/*.css", "/favicon.ico", "/media/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/reservas-publicas/**").permitAll()
                        // catalogo: cualquiera puede ver
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/libros/**", "/api/v1/categorias/**", "/api/v1/ejemplares/**",
                                "/api/v1/salas/**", "/api/v1/escuelas/**").permitAll()
                        // catalogo: solo admin crea, edita y elimina
                        .requestMatchers(
                                "/api/v1/libros/**", "/api/v1/categorias/**", "/api/v1/ejemplares/**",
                                "/api/v1/salas/**", "/api/v1/escuelas/**").hasAuthority("ADMIN")
                        // gestion: solo admin
                        .requestMatchers("/api/v1/usuarios/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/reportes/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/prestamos/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/ingresos/**").hasAuthority("ADMIN")
                        // reservas de sala: cualquier usuario logueado
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint) // error 401
                        .accessDeniedHandler(accessDeniedHandler)           // error 403
                );
        return http.build();
    }
}
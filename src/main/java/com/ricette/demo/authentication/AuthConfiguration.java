package com.ricette.demo.authentication;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.ricette.demo.model.Credentials.ADMIN_ROLE;
import static com.ricette.demo.model.Credentials.GENERIC_USER_ROLE;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    /**
     * Questo metodo contiene le impostazioni della configurazione
     * di autenticatzione e autorizzazione.
     */
    @Autowired
    private DataSource datasource;

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity)
            throws Exception{
                httpSecurity
                .csrf().and().cors().disable()
                .authorizeHttpRequests()
                // .requestMatchers("/**").permitAll()
                // chiunque (autenticato o no) può accedere alle pagine index, login, register, ai css e alle immagini
                .requestMatchers(HttpMethod.GET,"/rest/**","/","/login","/register","/css/**", "/images/**", "favicon.ico", "/categories", "/category/{id}", "/recipes", "/recipe/{id}", "/search", "/users", "/user/{id}").permitAll()
                // chiunque (autenticato o no) può mandare richieste POST al punto di accesso per login e register
                .requestMatchers(HttpMethod.POST,"/register", "/login").permitAll()
                .requestMatchers(HttpMethod.GET,"/admin/**").hasAnyAuthority(ADMIN_ROLE)
                .requestMatchers(HttpMethod.POST,"/admin/**").hasAnyAuthority(ADMIN_ROLE)
                .requestMatchers(HttpMethod.GET,"/genericUser/**").hasAnyAuthority(GENERIC_USER_ROLE,"OIDC_USER")
                .requestMatchers(HttpMethod.POST,"/genericUser/**").hasAnyAuthority(GENERIC_USER_ROLE,"OIDC_USER")
                // tutti gli utenti autenticati possono accere alle pagine rimanenti
                .anyRequest().authenticated()

                // LOGIN: qui definiamo il login
                .and().formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/default")
                .failureUrl("/login?error=true")

                //google
                .and().oauth2Login()
                .loginPage("/login")
                .defaultSuccessUrl("/default")

                // LOGOUT: qui definiamo il logout
                .and()
                .logout()
                // il logout è attivato con una richiesta GET a "/logout"
                .logoutUrl("/logout")
                // in caso di successo, si viene reindirizzati alla home
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .clearAuthentication(true).permitAll();
        return httpSecurity.build();
    }

    /**
     * Questo metodo definisce le query SQL per ottenere username e password
     */
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication()
                //per accedere alle credenziali salvate
                .dataSource(this.datasource)
                //ricpueriamo username e ruolo
                .authoritiesByUsernameQuery("SELECT username, ruolo FROM credentials WHERE username=?")
                //ricuperiamo username, password e un flag
                .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    }

    /**
     * Questo metodo definisce il componente "passwordEncoder",
     * usato per criptare e decriptare la password nella sorgente dati.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

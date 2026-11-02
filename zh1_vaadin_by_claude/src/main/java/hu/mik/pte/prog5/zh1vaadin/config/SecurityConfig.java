package hu.mik.pte.prog5.zh1vaadin.config;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import hu.mik.pte.prog5.zh1vaadin.ui.view.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 2-2. feladat: a felhasznalok be tudjanak jelentkezni.
 * 2-1. feladat: a bejelentkezes a sajat {@link LoginView} kepernyon keresztul tortenjen.
 * 8-1. feladat: global method security engedelyezese.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfig {

    /**
     * 2-1. feladat: a Vaadin sajat security configurerje koti be a LoginView-t
     * login kepernyokent (es engedi ki a /VAADIN/** eroforrasokat).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.with(VaadinSecurityConfigurer.vaadin(),
                vaadin -> vaadin.loginView(LoginView.class));
        return http.build();
    }

    /**
     * 2-2. feladat: a kiindulo allapotban ez a metodus nem volt bean, es ures
     * InMemoryUserDetailsManager-t adott vissza, ezert senki nem tudott belepni.
     */
    @Bean
    public UserDetailsManager userDetailsManager() {
        var admin = User.builder()
                .username("admin")
                .password("{noop}password")
                .roles("ADMIN", "REGISTER", "CAT", "DOG", "PARROT", "TURTLE", "FERRET")
                .build();
        var dog = User.builder()
                .username("dog")
                .password("{noop}password")
                .roles("DOG")
                .build();
        var cat = User.builder()
                .username("cat")
                .password("{noop}password")
                .roles("REGISTER", "DOG")
                .build();
        var uncommon = User.builder()
                .username("uncommon")
                .password("{noop}password")
                .roles("TURTLE", "PARROT", "FERRET")
                .build();
        return new InMemoryUserDetailsManager(admin, dog, cat, uncommon);
    }
}

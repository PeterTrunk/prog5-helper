package hu.mik.pte.prog5.crud_mvc.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity(securedEnabled = true,jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth ->
                                auth
                                        .requestMatchers("/css/**","/js/**","/images/**","/webjars/**").permitAll()
                                        .requestMatchers("/error").permitAll()
                                        .dispatcherTypeMatchers(DispatcherType.FORWARD,DispatcherType.ERROR).permitAll()
//                        .requestMatchers("/admin").denyAll()
//                        .requestMatchers(HttpMethod.POST,"/admin").denyAll()
//                        .requestMatchers("/admin").hasAuthority("EDIT_FORM")
//                        .requestMatchers("/admin").hasRole("ADMIN")
                                        .requestMatchers("/register", "/login").permitAll()
//                        .anyRequest().permitAll()
                                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
        ;

        return http.build();
    }

//    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User
                .withUsername("user")
                .password(encoder.encode("user"))
                //ROLE_USER, ROLE_VIEWER
//                .roles("USER","VIEWER")
                //EDIT_FORM,DELETE_CAR
                .authorities("ROLE_USER","EDIT_FORM","DELETE_CAR")
                .build();

        UserDetails admin = User
                .withUsername("admin")
                .roles("USER","ADMIN")
                .password(encoder.encode("admin"))
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

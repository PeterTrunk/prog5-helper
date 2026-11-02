package hu.mik.pte.prog5.zh1vaadin.config;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfig {

    public UserDetailsManager userDetailsManager() {
        var admin = User.builder()
                .username("admin")
                .password("{noop}password")
                .roles("ADMIN","REGISTER","CAT","DOG","PARROT","TURTLE","FERRET")
                .build();
        var dog = User.builder()
                .username("dog")
                .password("{noop}password")
                .roles("DOG")
                .build();
        var cat = User.builder()
                .username("cat")
                .password("{noop}password")
                .roles("REGISTER","DOG")
                .build();
        var uncommon = User.builder()
                .username("uncommon")
                .password("{noop}password")
                .roles("TURTLE","PARROT","FERRET")
                .build();
        return new InMemoryUserDetailsManager();
    }
}

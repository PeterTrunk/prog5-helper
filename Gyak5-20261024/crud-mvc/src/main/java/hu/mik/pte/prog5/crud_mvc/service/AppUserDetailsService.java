package hu.mik.pte.prog5.crud_mvc.service;

import hu.mik.pte.prog5.crud_mvc.model.AppUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final Map<String, AppUser> appUsers = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public AppUserDetailsService(PasswordEncoder encoder) {
        this.passwordEncoder = encoder;
        this.appUsers.put("user", new AppUser("user", encoder.encode("user"), Set.of("USER")));
        this.appUsers.put("admin", new AppUser("admin", encoder.encode("admin"), Set.of("USER", "ADMIN")));
    }

    public boolean exists(String username){
        return this.appUsers.containsKey(username);
    }

    public void register(String username, String rawPassword){
        AppUser user = new AppUser(username,this.passwordEncoder.encode(rawPassword),Set.of("USER"));
        if(this.appUsers.putIfAbsent(username,user) != null){
            throw new IllegalStateException("User already exists!");
        }
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser user = this.appUsers.get(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return User.builder()
                .username(user.username())
                .password(user.passwordHash())
                .roles(user.roles().toArray(String[]::new))
                .build();
    }
}

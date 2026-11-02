package hu.mik.pte.prog5.crud_mvc.controller;

import hu.mik.pte.prog5.crud_mvc.model.RegisterForm;
import hu.mik.pte.prog5.crud_mvc.service.AppUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final AppUserDetailsService userDetailsService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public RegisterController(AppUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registerForm") RegisterForm registerForm, BindingResult bindingResult, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        //TODO validations (exists,confirmPassword)
        this.userDetailsService.register(registerForm.getUsername(), registerForm.getPassword());
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(registerForm.getUsername());
        Authentication auth = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        this.securityContextRepository.saveContext(context,httpServletRequest,httpServletResponse);
        return "redirect:/cars";


    }
}

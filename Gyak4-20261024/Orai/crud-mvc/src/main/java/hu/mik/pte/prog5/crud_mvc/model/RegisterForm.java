package hu.mik.pte.prog5.crud_mvc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {
    String username;
    String password;
    String confirmPassword;
}

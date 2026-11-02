package hu.mik.pte.prog5.crud_mvc.model;

import java.util.Set;

public record AppUser (String username, String passwordHash, Set<String> roles) {
}

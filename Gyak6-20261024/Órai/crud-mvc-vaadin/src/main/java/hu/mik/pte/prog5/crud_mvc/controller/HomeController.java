package hu.mik.pte.prog5.crud_mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "redirect:/cars";
    }
}

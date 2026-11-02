package hu.mik.pte.prog5.crud_mvc.controller;

import hu.mik.pte.prog5.crud_mvc.model.Car;
import hu.mik.pte.prog5.crud_mvc.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public String list(Model model){
        model.addAttribute("cars",this.carService.list());
        return "cars/list";
    }

    @GetMapping("/new")
    public String newForm(Model model){
        model.addAttribute("car",new Car());
        return "cars/form";
    }
    @PostMapping
    public String create(@Valid @ModelAttribute("car") Car car, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "cars/form";
        }
        this.carService.save(car);
        return "redirect:/cars";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,Model model){
        Car car = this.carService.findById(id);
        model.addAttribute("car",car);
        return "cars/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,@Valid @ModelAttribute("car") Car car){
        car.setId(id);
        this.carService.save(car);
        return "redirect:/cars";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id){
        this.carService.deleteById(id);
        return "redirect:/cars";
    }
}

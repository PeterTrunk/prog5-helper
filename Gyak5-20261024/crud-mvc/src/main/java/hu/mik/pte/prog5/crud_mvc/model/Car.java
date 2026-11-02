package hu.mik.pte.prog5.crud_mvc.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@Setter
@Getter
@RequiredArgsConstructor
public class Car {
    Long id;
    @NotBlank(message = "Nem lehet üres")
    String make;
    @NotBlank(message = "Nem lehet üres")
    String model;
    @NotNull()
    @Min(value = 1950, message = "Nem lehet 1950 előtti")
    Integer manufactureYear;

   public Car(String make, String model, int year) {
        this.make = make;
        this.model = model;
        this.manufactureYear = year;
    }

}

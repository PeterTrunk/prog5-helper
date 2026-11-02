package hu.mik.pte.prog5.crud_mvc;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@StyleSheet(Lumo.STYLESHEET)
//@StyleSheet(Aura.STYLESHEET)
@SpringBootApplication
public class CrudMvcApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(CrudMvcApplication.class, args);
	}

}

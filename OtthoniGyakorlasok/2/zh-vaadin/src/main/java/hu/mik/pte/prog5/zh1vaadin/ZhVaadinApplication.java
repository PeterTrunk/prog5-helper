package hu.mik.pte.prog5.zh1vaadin;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@StyleSheet(Lumo.STYLESHEET)
@SpringBootApplication
public class ZhVaadinApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(ZhVaadinApplication.class, args);
	}

}

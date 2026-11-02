# ZH1 (zh-vaadin) – megoldás feladatonként

Ez a dokumentum a `zh1.pdf` feladatait követi sorrendben, és megmondja, **melyik fájl melyik
kódrészlete** oldja meg az adott pontot. A projekt a `MintaZHFeladatok/zh-vaadin/zh-vaadin`
kiinduló állapotából készült, `zh1_vaadin_by_claude` néven.

**Build és futtatás ellenőrizve:**

| Lépés | Eredmény |
|---|---|
| `mvn clean package` | ✅ BUILD SUCCESS, `Tests run: 1, Failures: 0` |
| `mvn -Pproduction package` | ✅ BUILD SUCCESS (frontend bundle lefordul), indulva: `Vaadin is running in production mode` |
| `java -jar target/zh-vaadin-0.0.1-SNAPSHOT.jar` | ✅ `Tomcat started on port 8083` |
| `-Dspring.profiles.active=dev` | ✅ `Tomcat started on port 8084` |
| `GET /` anonim | ✅ `302 → /login` |
| `POST /login` rossz jelszóval | ✅ `302 → /login?error` |
| `POST /login` jó jelszóval | ✅ `302 → /` |
| `GET /list`, `GET /register` belépve | ✅ `200` |

---

## 0. Verziófrissítések (a feladatkiírásban nincs, de a gépeden lévő verziókhoz kellett)

A kiinduló projekt egy előző évi ZH, ezért a `pom.xml`-t a Gyak2–Gyak6-ban használt
verziókra igazítottam.

**`pom.xml`**

| Mi | Kiinduló | Most | Miért |
|---|---|---|---|
| `spring-boot-starter-parent` | `3.5.6` | `4.1.1` | ez van a Gyak2–Gyak6 projektekben |
| `java.version` | `21` | `25` | a gépen telepített JDK (Temurin 25.0.5) |
| `vaadin.version` | `24.9.3` | `25.3.2` | a Gyak6 (`crud-mvc-vaadin`) verziója |

**Ebből következő, kötelező kódbeli igazítások:**

1. **`spring-boot-starter-test` → `spring-boot-starter-webmvc-test`**
   Spring Boot 4-ben átszervezték a startereket (ahogy a `spring-boot-starter-web` is
   `spring-boot-starter-webmvc` lett – lásd Gyak2–Gyak6 pom-jait).

2. **Új függőség: `com.vaadin:vaadin-dev`**
   Vaadin 25-ben a `vaadin-spring-boot-starter` **már nem hozza be tranzitívan** a dev-módú
   frontend szervert. Enélkül az alkalmazás indításkor elszáll:
   ```
   java.lang.RuntimeException: 'vaadin-dev-server' not found.
   ```
   A Gyak6-os pom-ban ezért szerepel külön. Ott `<optional>true</optional>`-lal –
   **itt szándékosan nem optional**, mert a `spring-boot-maven-plugin` repackage lépése
   az optional függőségeket kihagyja a futtatható jar-ból, így a `java -jar` megint elszállna.
   (IDE-ből / `mvn spring-boot:run`-nal az optional is működne.)

3. **`SecurityConfig` átírása a modern Vaadin security API-ra**
   A Vaadin 24-es `VaadinWebSecurity` ősosztály helyett a 25-ös
   `VaadinSecurityConfigurer.vaadin()` konfigurer – ez a Gyak6-ban is így szerepel.
   A kiinduló fájlban lévő `@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)`
   Vaadin 25-ben **is létezik**, ezért megtartottam.

4. **A `src/main/frontend/generated/` könyvtár törölve** – ez a Vaadin 24.9.3 által generált,
   verzióhoz kötött kód; a 25.3.2-es build magától újragenerálja.

---

## 1. Port beállítás

> *„Állítsa be a konfigurációban, hogy az alkalmazás alapértelmezetten a 8083-as, dev profil
> esetén pedig a 8084-es porton fusson."*

**`src/main/resources/application.properties`**
```properties
vaadin.launch-browser=true
spring.application.name=zh-vaadin

# 1. feladat - alapertelmezett port
server.port=8083
```

**`src/main/resources/application-dev.properties`** (új fájl)
```properties
# 1. feladat - 'dev' profil eseten ezen a porton fusson az alkalmazas
server.port=8084
```

A Spring Boot a `application-<profil>.properties` fájlokat automatikusan ráolvassa az
alap `application.properties`-re, ha az adott profil aktív (`--spring.profiles.active=dev`).

> 💡 Ugyanez egy fájlban, többdokumentumos formában is megoldható lett volna:
> ```properties
> server.port=8083
> #---
> spring.config.activate.on-profile=dev
> server.port=8084
> ```
> A külön fájl az idiomatikusabb.

---

## 2. Ki-/bejelentkezés

### 2-1. Bejelentkező képernyő

> *„Készítse el a LoginView osztályt… Vaadin beépített bejelentkező komponensét használja…
> a '/login' útvonalon… hiba esetén kerüljön jelölésre, hogy hiba történt!"*

**`ui/view/LoginView.java`** (teljesen újraírva)

| Követelmény | Kódrészlet |
|---|---|
| `/login` útvonal | `@Route("login")` |
| belépés előtt is elérhető | `@AnonymousAllowed` |
| beépített Vaadin komponens | `private final LoginForm loginForm = new LoginForm();` |
| a Spring Security ide POST-ol | `this.loginForm.setAction("login");` |
| hibajelzés | `beforeEnter(...)` → `this.loginForm.setError(true);` |

```java
@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle(this.getTranslation("title.login"));
        this.loginForm.setI18n(i18n);

        this.loginForm.setAction("login");
        this.loginForm.setForgotPasswordButtonVisible(false);
        ...
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            this.loginForm.setError(true);
        }
    }
}
```

A `beforeEnter` váza (az `error` query paraméter vizsgálata + `//TODO hibakezelés`) **már a
kiinduló projektben benne volt** – csak a `setError(true)` hívás hiányzott belőle.

**A képernyő bekötése login oldalként** – `config/SecurityConfig.java`:
```java
http.with(VaadinSecurityConfigurer.vaadin(),
        vaadin -> vaadin.loginView(LoginView.class));
```
A `loginView(...)` intézi a form login beállítását, a `/login?error` hiba-átirányítást,
a `/VAADIN/**` belső erőforrások kiengedését és a Vaadin-specifikus CSRF/UIDL kezelést.

> **Megjegyzés:** a `LoginView` – a Gyak6 `LoginView`-jához hasonlóan – a `MainLayout`-ba van
> foglalva (`layout = MainLayout.class`). A fejléc tartalma `isAuthenticated()`-tel védett,
> így a bejelentkező képernyőn nem jelenik meg benne menü és logout gomb.

### 2-2. Bejelentkezés

> *„A rendszerbe jelenleg nem tudnak bejelentkezni a SecurityConfig osztályba rögzített
> felhasználók. Egészítse ki a konfigurációt…"*

**`config/SecurityConfig.java`** – három hiba volt a kiinduló állapotban:

| Hiba | Javítás |
|---|---|
| az osztály nem volt Spring konfiguráció | `@Configuration` + `@EnableWebSecurity` |
| a `userDetailsManager()` nem volt bean | `@Bean` |
| `return new InMemoryUserDetailsManager();` **üresen** tért vissza | `return new InMemoryUserDetailsManager(admin, dog, cat, uncommon);` |

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager() {
        var admin = User.builder().username("admin").password("{noop}password")
                .roles("ADMIN","REGISTER","CAT","DOG","PARROT","TURTLE","FERRET").build();
        var dog = ...;  var cat = ...;  var uncommon = ...;
        return new InMemoryUserDetailsManager(admin, dog, cat, uncommon);   // ← a lényeg
    }
}
```

> A jelszavak `{noop}` prefixe a kiinduló projektből maradt: a Spring Security alapértelmezett
> `DelegatingPasswordEncoder`-e ebből tudja, hogy nincs hashelés. **Fontos:** emiatt
> **nem szabad** saját `PasswordEncoder` beant (pl. `BCryptPasswordEncoder`) definiálni,
> mert akkor a `{noop}`-os jelszavak nem működnének. Éles rendszerben persze BCrypt kell.

**Bejelentkezési adatok:** `admin` / `dog` / `cat` / `uncommon`, jelszó mindenkinek `password`.

---

## 3. PetService

> *„Implementálja a PetService interface-t… a 'mock' implementációt használja… a dependency
> injection során nem hivatkozhat közvetlenül az implementációra, csak annak a nevére és az
> interface-re. (Az importok között nem szerepelhet az InMemoryPetRepository.)"*

**`service/PetServiceImpl.java`** (új fájl)

```java
@Service("petService")
public class PetServiceImpl implements PetService, SecurityService {

    private final PetRepository petRepository;          // ← az INTERFACE a mező típusa

    public PetServiceImpl(@Qualifier("mock") PetRepository petRepository) {
        this.petRepository = petRepository;             // ← a 'mock' bean NEVE választ
    }

    @Override public List<Pet> listAll()  { return this.petRepository.findAll(); }
    @Override public Pet save(Pet pet)    { return this.petRepository.save(pet); }
    @Override public List<PetSpecies> listSpecies() { return List.of(PetSpecies.values()); }
}
```

A kulcs a `@Qualifier("mock")`: az `InMemoryPetRepository` `@Repository("mock")`-ként,
a `DBPetRepository` `@Repository("db")`-ként van regisztrálva. **Az importok között sehol
nem szerepel az `InMemoryPetRepository`** – ellenőrizhető a fájl import-blokkjában.

> Ez pontosan a **Gyak3**-ban tanult „egy interface – több implementáció" minta
> (`DataService` + `@Service("plain")` / `@Service("mock")`), és a *02. előadás 3. diájának*
> kérdésére a válasz. Ott `@Profile`-lal is meg volt oldva; itt a feladat a bean nevét kéri.

**Hogy tudjuk, hogy tényleg a `mock` jött be?** A `DBPetRepository` minden metódusa
`UnsupportedOperationException`-t dob – ha rosszul injektálnánk, a lista azonnal elszállna.
A verifikáló teszt 5 állatot kapott vissza, tehát az in-memory implementáció aktív.

---

## 4. Header

> *„Implementálja a MainLayout osztályt. A fejlécben szerepeljen a bejelentkezett felhasználó
> neve, egy menü a 5-es és 6-os feladatban létrehozandó képernyőkhöz, illetve egy
> kijelentkezés gomb."*

**`ui/MainLayout.java`** (a kiinduló állapot egy üres `extends AppLayout` volt)

A felépítés a **Gyak6 `MainLayout`-ját követi**: `VerticalLayout` + `RouterLayout`, külön
`content` `Div`-vel, `createHeader()` metódussal és `showRouterLayoutContent()` felülírással.

```java
@AnonymousAllowed
public class MainLayout extends VerticalLayout implements RouterLayout {

    private final Div content = new Div();
    private final AuthenticationContext authenticationContext;

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        this.setSizeFull();
        this.setMaxWidth("900px");
        this.getStyle().set("margin", "0 auto");
        this.content.setWidthFull();
        this.add(this.createHeader(), this.content);
        this.setFlexGrow(1, this.content);
    }

    private Header createHeader() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);

        if (this.authenticationContext.isAuthenticated()) {      // a login kepernyon ures a fejlec
            HorizontalLayout menu = new HorizontalLayout(
                    new RouterLink(this.getTranslation("header.menu.pet.list"), ListView.class));

            if (this.authenticationContext.hasRole("REGISTER")) {           // ← 8-2. feladat
                menu.add(new RouterLink(this.getTranslation("header.menu.pet.register"),
                         RegisterView.class));
            }
            bar.add(menu);
            bar.expand(menu);

            String username = this.authenticationContext.getPrincipalName().orElse("");
            Button logout = new Button(this.getTranslation("header.menu.logout"),
                    e -> this.authenticationContext.logout());
            bar.add(new Span(this.getTranslation("header.username") + " " + username), logout);
        }

        Header header = new Header(bar);
        header.setWidthFull();
        return header;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.content.removeAll();
        if (content != null) {
            this.content.getElement().appendChild(content.getElement());
        }
    }
}
```

> A kiinduló váz `extends AppLayout` volt. Az `AppLayout` maga is `RouterLayout`, tehát az is
> működne (kevesebb kóddal) – itt viszont az órai mintához igazodtam. Ha a vizsgán a kiosztott
> csontvázhoz akarsz ragaszkodni, az `AppLayout` + `addToNavbar(...)` változat is helyes.

| Követelmény | Kódrészlet |
|---|---|
| felhasználó neve | `authenticationContext.getPrincipalName()` + `header.username` kulcs |
| menü a két képernyőhöz | `new RouterLink(..., ListView.class)` / `RegisterView.class` |
| kijelentkezés gomb | `new Button(..., e -> authenticationContext.logout())` |
| minden szöveg i18n-ből | `getTranslation("header.menu.pet.list")` stb. |

A `com.vaadin.flow.spring.security.AuthenticationContext` a Vaadin Spring integráció beanje –
**ugyanaz, amit a Gyak6 `MainLayout`-ja is használ**. A `logout()` a Vaadin-konform
kijelentkezés (elengedi a UI-t és a sessiont is).

A `@AnonymousAllowed` azért kell a layouten, mert a `LoginView` ugyan nem ebben fut, de a
Vaadin a layout-láncot is ellenőrzi; enélkül a navigáció elakadna.

---

## 5. Listázás

### 5-1. ListView

> *„…az oszlopok sorrendje fontos! A táblázatban lévő azonosító oszlop csak akkor látszódjon,
> ha 'ADMIN' jogosultságú felhasználó nyitja meg. (SecurityService.getRolesAsString)"*

**`ui/view/ListView.java`** (a kiinduló állapot egy üres osztály volt)

```java
public class ListView extends VerticalLayout implements HasDynamicTitle, SecurityService {

    private final Grid<Pet> grid = new Grid<>(Pet.class, false);

    public ListView(PetService petService) {
        // oszlopsorrend: azonosito, nev, faj
        if (this.getRolesAsString().contains("ADMIN")) {                 // ← csak ADMIN-nak
            this.grid.addColumn(Pet::getUid).setHeader(this.getTranslation("pet.id"));
        }
        this.grid.addColumn(Pet::getName).setHeader(this.getTranslation("pet.name"));
        this.grid.addColumn(pet -> this.getTranslation("pet.species." + pet.getSpecies().name()))
                 .setHeader(this.getTranslation("pet.species"));

        this.grid.setItems(petService.listAll());
        ...
    }
}
```

Fontos részletek:
- **`new Grid<>(Pet.class, false)`** – a `false` tiltja le az automatikus oszlopgenerálást,
  így mi határozzuk meg az oszlopokat **és a sorrendjüket**.
- Az osztály **`implements SecurityService`** – így a megadott `getRolesAsString()`
  default metódus közvetlenül hívható (a feladat ezt kéri zárójelben).
- A faj oszlop nem az enum nevét írja ki, hanem a lokalizált változatát:
  `pet.species.CAT=macska` stb. → `getTranslation("pet.species." + pet.getSpecies().name())`.

### 5-2. ListView routing

> *„Állítsa be, hogy a ListView a /list útvonalon legyen elérhető, a MainLayout-ba foglalva."*

```java
@Route(value = "list", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class) // bejelentkezes utan a '/' is ide vezet
@PermitAll
public class ListView ...
```

- `@Route(value = "list", layout = MainLayout.class)` – pontosan a kért útvonal és keret.
- `@PermitAll` – minden bejelentkezett felhasználó láthatja (a Vaadin nézet-szintű
  hozzáférés-vezérlése alapból **tilt** minden annotálatlan nézetet).
- **`@RouteAlias(value = "", ...)` – saját kiegészítés:** a Spring Security sikeres
  bejelentkezés után a `/` címre irányít. Ha oda nincs nézet kötve, a felhasználó 404-et
  kapna. Ezzel a gyökér is a listára visz. (A `/list` ettől függetlenül működik.)

---

## 6. Állat regisztráció

### 6-1. RegisterView

> *„A faj listát a PetService.listSpecies metódusa adja vissza a PetSpecies enum alapján
> dinamikusan. A form és a modell között bindolással teremtsen kapcsolatot. Figyeljen arra,
> hogy minden mező kitöltése kötelező legyen!"*

**`ui/view/RegisterView.java`**

A kiinduló állapot egy váz volt, benne **útmutatásként** két kikommentelt sor
(`// this.species.setItems`, `// this.species.setItemLabelGenerator`) – pontosan ezeket
kellett kitölteni:

```java
private final TextField name = new TextField(this.getTranslation("pet.name"));
private final ComboBox<PetSpecies> species = new ComboBox<>(this.getTranslation("pet.species"));
private final Binder<Pet> binder = new Binder<>(Pet.class);

public RegisterView(PetService petService) {
    this.petService = petService;

    // a fajlista a service-bol, dinamikusan a PetSpecies enum alapjan
    this.species.setItems(petService.listSpecies());
    this.species.setItemLabelGenerator(s -> this.getTranslation("pet.species." + s.name()));

    // bindolas + kotelezo mezok
    this.binder.forField(this.name).asRequired().bind("name");
    this.binder.forField(this.species).asRequired().bind("species");

    Button submit = new Button(this.getTranslation("register.submit"), e -> this.save());
    this.add(new FormLayout(this.name, this.species), submit);
}

private void save() {
    Pet pet = new Pet();
    if (this.binder.writeBeanIfValid(pet)) {   // csak ervenyes allapotban ment
        this.petService.save(pet);
        UI.getCurrent().navigate(ListView.class);
    }
}
```

| Követelmény | Kódrészlet |
|---|---|
| faj lista a service-ből, dinamikusan | `species.setItems(petService.listSpecies())`, a service `List.of(PetSpecies.values())`-t ad |
| lokalizált faj-feliratok | `setItemLabelGenerator(s -> getTranslation("pet.species." + s.name()))` |
| form ↔ modell bindolás | `Binder<Pet>` + `forField(...).bind("name")` / `.bind("species")` |
| minden mező kötelező | `.asRequired()` |
| mentés | `binder.writeBeanIfValid(pet)` → `petService.save(pet)` |

**Miért `Binder` és nem `BeanValidationBinder`?**
A `Pet` entitáson nincsenek `jakarta.validation` annotációk (`@NotBlank` stb.), így a
`BeanValidationBinder`-nek nem lenne mit érvényesítenie. A kötelezőséget a Binder
`asRequired()` hívása adja. (A Gyak6-ban fordítva volt: ott a `Car`-on **voltak** annotációk,
ezért ott a `BeanValidationBinder` a helyes választás.)

**Miért `asRequired()` paraméter nélkül?**
A `translations.properties` nem tartalmaz hibaüzenet-kulcsot, a feladat pedig előírja, hogy
minden megjelenő szöveg onnan jöjjön. A paraméter nélküli `asRequired()` a Vaadin beépített
kötelező-jelölését használja, így nem kell új kulcsot kitalálni.

**Új `Pet` létrehozása:** a `new Pet()` `uid`-ja `null`, ezért az `InMemoryPetRepository.save()`
generál neki `UUID`-t (a meglévő rekordot pedig lecserélné – ez az *insert vs update* ág).

### 6-2. RegisterView routing

```java
@Route(value = "register", layout = MainLayout.class)
@RolesAllowed("REGISTER")
public class RegisterView ...
```

---

## 7. Képernyők címe

> *„Mindegyik képernyőn állítson be lokalizációnak megfelelő címet!"*

Mindhárom nézet `implements HasDynamicTitle`:

| Nézet | Metódus | Kulcs |
|---|---|---|
| `LoginView` | `getPageTitle()` | `title.login` → *Bejelentkezés* |
| `ListView` | `getPageTitle()` | `title.pet.list` → *Állat lista* |
| `RegisterView` | `getPageTitle()` | `title.pet.register` → *Állat regisztrálás* |

```java
@Override
public String getPageTitle() {
    return this.getTranslation("title.pet.list");
}
```

> **Miért `HasDynamicTitle` és nem `@PageTitle`?**
> A `@PageTitle("...")` annotáció értéke fordítási idejű **konstans String**, abba nem fér bele
> a `getTranslation(...)` hívás. Lokalizált címhez a `HasDynamicTitle` interface kell.
> (Ugyanez a megkülönböztetés a Gyak6 `VaadinBasics` osztályában is szerepel, ott a
> `//@PageTitle("Cím")` pont ezért van kikommentelve.)

A `LoginView` ezen felül a bejelentkező form fejlécét is lokalizálja:
```java
LoginI18n i18n = LoginI18n.createDefault();
i18n.getForm().setTitle(this.getTranslation("title.login"));
this.loginForm.setI18n(i18n);
```

---

## 8. Jogosultság

### 8-1. Global method security

> *„Engedélyezze a global method security-t."*

**`config/SecurityConfig.java`**
```java
@EnableMethodSecurity
```
Ez kapcsolja be a `@PreAuthorize` / `@PostAuthorize` / `@PreFilter` / `@PostFilter`
annotációk feldolgozását (a `@Secured`-höz `securedEnabled = true`, a `@RolesAllowed`-hoz
`jsr250Enabled = true` kellene – itt egyikre sincs szükség **metódus** szinten).

> **Figyelem:** a nézeteken lévő `@PermitAll` / `@RolesAllowed` / `@AnonymousAllowed`
> **nem** Spring method security, hanem a Vaadin saját nézet-hozzáférés-vezérlése
> (`NavigationAccessControl`), amit a `VaadinSecurityConfigurer` kapcsol be. Ezekhez
> nem kell a `jsr250Enabled`.

### 8-2. Állat regisztráció

> *„…az állat regisztrációhoz fűződő metódusokat csak azok a felhasználók tudják meghívni,
> akik rendelkeznek 'REGISTER' jogosultsággal. A menüben lévő regisztrációs gomb is csak
> ugyanezzel a jogosultsággal rendelkező felhasználóknak jelenjen meg. A RegisterView csak
> akkor legyen látogatható, ha rendelkezik a felhasználó ezzel a jogosultsággal."*

Három helyen, három rétegben:

**1. Szolgáltatás réteg** – `service/PetServiceImpl.java`:
```java
@Override
@PreAuthorize("hasRole('REGISTER')")
public Pet save(Pet pet) { ... }

@Override
@PreAuthorize("hasRole('REGISTER')")
public List<PetSpecies> listSpecies() { ... }
```

**2. Menü** – `ui/MainLayout.java`:
```java
if (this.authenticationContext.hasRole("REGISTER")) {
    menu.add(new RouterLink(this.getTranslation("header.menu.pet.register"), RegisterView.class));
}
```

**3. Nézet** – `ui/view/RegisterView.java`:
```java
@RolesAllowed("REGISTER")
```

> A `hasRole('REGISTER')` a `ROLE_REGISTER` authority-t keresi – a `User.builder().roles("REGISTER")`
> pont ezt a prefixelt formát hozza létre. Ha `hasAuthority(...)`-t használnánk, ki kellene írni
> a `ROLE_` prefixet. (*03. előadás, 4. dia* – szerepkör vs. engedély.)

### 8-3. Állatok szűrése (bónusz)

> *„A global method security segítségével érje el, hogy a 'PetService.listAll()' metódus
> implementációja csak azokat az állatokat adja vissza, amikhez az adott felhasználónak joga van…
> használja a SecurityService 'getRolesAsString()' metódusát. Figyeljen oda az objektumok
> típusára az összehasonlításkor!"*

**`service/PetServiceImpl.java`**
```java
@Service("petService")
public class PetServiceImpl implements PetService, SecurityService {   // ← SecurityService mixin

    @Override
    @PostFilter("@petService.getRolesAsString().contains(filterObject.species.name())")
    public List<Pet> listAll() {
        return this.petRepository.findAll();
    }
```

A megoldás három eleme:

1. **`@PostFilter`** – a metódus **visszatérési** kollekcióját szűri elemenként;
   a `filterObject` az aktuális `Pet`. (A `@PreFilter` itt nem jó: annak bemenő
   kollekció-paraméter kellene.)

2. **`@petService`** – a SpEL-ben a `@beanNév` szintaxis bean-re hivatkozik. A method
   security kifejezés gyökérobjektuma nem a cél bean, ezért a `getRolesAsString()` nem
   hívható közvetlenül; a `@Service("petService")` explicit névvel teszem hivatkozhatóvá.
   Ezért implementálja az osztály a `SecurityService`-t is.

3. **A típus-csapda** (amire a feladat külön figyelmeztet):
   `getRolesAsString()` → `List<String>` (pl. `["ADMIN","REGISTER","CAT","DOG",...]`),
   `filterObject.species` → `PetSpecies` **enum**.
   Közvetlen `contains(filterObject.species)` **mindig false** lenne. Ezért kell a
   **`.name()`** (vagy `.toString()`) hívás, ami a `PetSpecies.CAT`-ből `"CAT"` String-et csinál.

**Ellenőrzés** (ideiglenes integrációs teszttel, utólag eltávolítva):

| Felhasználó | Szerepkörök | `listAll()` eredménye |
|---|---|---|
| `admin` | ADMIN, REGISTER, CAT, DOG, PARROT, TURTLE, FERRET | 5 db – `Cirmos:CAT, Pityu:PARROT, Ubul:DOG, Bandita:FERRET, Béla:TURTLE` |
| `dog` | DOG | 1 db – `Ubul:DOG` |
| `uncommon` | TURTLE, PARROT, FERRET | 3 db – `Pityu:PARROT, Bandita:FERRET, Béla:TURTLE` |
| `cat` | REGISTER, DOG | csak a kutyák (a `CAT` szerepköre hiányzik – a felhasználónév félrevezető!) |

És a 8-2-re:

| Felhasználó | `save()` / `listSpecies()` |
|---|---|
| `dog` (nincs REGISTER) | `AccessDeniedException` ✅ |
| `cat` (van REGISTER) | sikeres mentés, a repository generál `uid`-t ✅ |

---

## 9. Módosított és új fájlok áttekintése

| Fájl | Állapot | Feladat |
|---|---|---|
| `pom.xml` | **módosítva** | 0. (verziók, `vaadin-dev`, `webmvc-test`) |
| `ZhVaadinApplication.java` | **módosítva** | – (`AppShellConfigurator` + Lumo téma, Gyak6 mintájára) |
| `src/main/resources/application.properties` | **módosítva** | 1. |
| `src/main/resources/application-dev.properties` | **új** | 1. |
| `config/SecurityConfig.java` | **átírva** | 2-1, 2-2, 8-1 |
| `service/PetServiceImpl.java` | **új** | 3, 8-2, 8-3 |
| `ui/MainLayout.java` | **átírva** | 4, 8-2 |
| `ui/view/LoginView.java` | **átírva** | 2-1, 7 |
| `ui/view/ListView.java` | **átírva** | 5-1, 5-2, 7 |
| `ui/view/RegisterView.java` | **átírva** | 6-1, 6-2, 7, 8-2 |
| `src/main/frontend/generated/` | **törölve** | 0. (Vaadin 24-es generátum) |

**Érintetlenül hagyott (adott) fájlok:** `entity/Pet.java`, `entity/PetSpecies.java`,
`repository/PetRepository.java`, `repository/InMemoryPetRepository.java`,
`repository/DBPetRepository.java`, `service/PetService.java`, `service/SecurityService.java`,
`vaadin-i18n/translations.properties`.

### Igazodás az órai (Gyak6) mintához

| Elem | Órai minta (Gyak6) | Itt |
|---|---|---|
| Security váz | `VaadinSecurityConfigurer.vaadin()` + `loginView(...)` | ✅ azonos |
| `LoginView` | `@Route(value="login", layout = MainLayout.class)`, `LoginForm`, `setAction("login")` | ✅ azonos |
| `MainLayout` | `VerticalLayout implements RouterLayout`, `content` Div, `createHeader()` | ✅ azonos |
| Alkalmazásosztály | `AppShellConfigurator` + `@StyleSheet(Lumo.STYLESHEET)` | ✅ azonos |
| Lista nézet | `Grid<>(X.class, false)`, `@RouteAlias(value="")`, `HasDynamicTitle` | ✅ azonos |
| i18n | `getTranslation(...)` + `vaadin-i18n/translations*.properties` | ✅ azonos |
| Űrlap-kötés | `BeanValidationBinder<Car>` (a `Car`-on vannak validációs annotációk) | ⚠️ `Binder<Pet>` + `asRequired()` – a `Pet`-en nincsenek annotációk, így a `BeanValidationBinder` semmit nem ellenőrizne |
| `PasswordEncoder` | `@Bean BCryptPasswordEncoder` | ⚠️ **nincs** – a kiinduló jelszavak `{noop}` alakúak, BCrypt bean esetén senki nem tudna belépni |
| Metódus-szintű jog | `@PostFilter("filterObject.owner == authentication.name …")` – csak beépített SpEL | ⚠️ `@PostFilter("@petService.getRolesAsString()…")` – a feladat írja elő a `SecurityService` használatát, ehhez bean-referencia kell |
| Nyelvválasztó | `ComboBox<Locale>` a fejlécben | ⚠️ nincs – a ZH-ban csak egy `translations.properties` van, nincs `_hu`/`_en` változat |

> A `translations.properties`-be **egyetlen új kulcsot sem** kellett felvenni – a feladat
> ígérete („minden szükséges üzenet megtalálható") tartható volt.

---

## 10. Futtatás

```bash
# fordítás + teszt
mvn clean package

# futtatás alapértelmezetten (8083)
java -jar target/zh-vaadin-0.0.1-SNAPSHOT.jar

# futtatás dev profillal (8084)
java -Dspring.profiles.active=dev -jar target/zh-vaadin-0.0.1-SNAPSHOT.jar
# vagy
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

> **IntelliJ-ből:** a Project SDK legyen a **JDK 25**. A Maven alapértelmezett JRE-je nálad
> JDK 17 (`mvn -v`), ezért parancssori buildnél `JAVA_HOME`-ot JDK 25-re kell állítani:
> `C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot`

**Belépési adatok:**

| Felhasználó | Jelszó | Szerepkörök |
|---|---|---|
| `admin` | `password` | ADMIN, REGISTER, CAT, DOG, PARROT, TURTLE, FERRET |
| `cat` | `password` | REGISTER, DOG |
| `dog` | `password` | DOG |
| `uncommon` | `password` | TURTLE, PARROT, FERRET |

Az `admin`-nal látszik az *Azonosító* oszlop és a *Regisztráció* menüpont;
a `dog`-gal egyik sem, és csak `Ubul` látszik a listában.

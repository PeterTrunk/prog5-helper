# Programozás 5 – ZH1 összegzés (Gyak2–Gyak6)

> Készült a `Gyak2`–`Gyak6` mappák **Orai/Órai** (órai munka) és **Source** (kiindulási / kiosztott
> forrás) tartalmának, valamint az `Eloadas_anyagok` PDF-jeinek összevetésével.
> Az előadás-hivatkozások formája: *(00. ea., 18. dia)* – a diaszám a PDF oldalszámával egyezik.

---

## 0. Az előadásanyagok térképe (hol mit keress)

| PDF | Cím | Dia | Téma |
|---|---|---|---|
| `00_spring.pdf` | Spring és az MVC | 3–7 | Spring keretrendszer, konfiguráció, enterprise integráció, tesztelés, adatelérés |
| | | 8 | Spring JDBC dióhéjban |
| | | **9** | **IoC konténer, Bean, ApplicationContext** |
| | | 10 | Hogyan működik a Spring (konfiguráció → konténer → kész rendszer) |
| | | **11** | **Annotáció vs XML konfiguráció** (`@Component`, `@Scope`, `@PostConstruct`, `@PreDestroy`) |
| | | 12–13 | Mikor melyiket használjuk (annotáció / XML előny-hátrány) |
| | | **14–16** | **AOP**: mit emelünk ki, kódismétlés/keveredés, proxy-s működés |
| | | **17** | **MVC minta** (Model–View–Controller háromszög) |
| | | **18** | **Spring MVC, DispatcherServlet** |
| | | 19 | ApplicationContext a webappokban (`ContextLoaderListener`) |
| | | 20 | `web.xml` konfiguráció (Boot-nál már nem kell – ld. 02. ea., 7. dia) |
| | | **21** | **Kérések kezelése**: URL → DispatcherServlet → Kontroller → Nézet |
| | | **22** | **Kontrollerek** (`@Controller`, `@RequestMapping`, `Model`) |
| | | **23** | **Nézetek, ViewResolver** |
| `01_spring.pdf` | Spring MVC | 6–11 | A 00. ea. MVC-részének ismétlése: MVC(6), Spring MVC(7), kéréskezelés(8), kontroller(9), ApplicationContext(10), ViewResolver(11) |
| `02_spring_boot.pdf` | Spring boot | **3** | **Ismétlés – DI, „mi van ha egy interface-nek több implementációja van?”** |
| | | **4** | **Bean scope**: singleton / prototype / request / session |
| | | **5–6** | **Maven profile** (`mvn clean install -P dev`) |
| | | **7** | **Spring Boot**: nincs `web.xml`, auto-konfiguráció, önálló jar |
| | | 8 | `application.properties` (nálunk `application.yaml`) |
| | | 9 | Dependency management – parent pom |
| | | 10 | Embedded Tomcat |
| | | 11 | DevTools, LiveReload, JRebel |
| `03_spring_secu.pdf` | Spring Security | 2–3 | Azonosítás + jogosultság, szerepkörök, erőforrások |
| | | **4** | **Engedélyek**: felhasználó → szerepkör → engedély → erőforrás |
| | | 5 | FUSZEMAT (funkció-szerepkör mátrix) – „sosem adunk jogot direktben usernek” |
| | | **6** | **Bejövő HTTP szűrés**: `AuthenticationFilter`, `UsernamePasswordAuthenticationFilter`, `BasicAuthenticationFilter` |
| | | **7** | **`UsernamePasswordAuthenticationToken`** |
| | | 8 | `AuthenticationManager` |
| | | 9 | `AuthenticationProvider`-ek (DaoAuthenticationProvider, OAuth2, LDAP…) |
| | | **10** | **`UserDetailsService.loadUserByUsername`** |
| | | 11 | `Authentication` interface (`getAuthorities`, `getPrincipal`…) |
| | | **12** | **`SecurityContext` kézi beállítása** – pontosan ez a Gyak4 regisztráció utáni auto-login |

> ⚠️ **Amit az előadások NEM fednek le, de a gyakorlaton volt:** Thymeleaf (Gyak5),
> i18n/lokalizáció (Gyak5–6), Vaadin Flow (Gyak6), Bean Validation (Gyak3-tól), Lombok (Gyak3-tól).
> Ezekre csak a gyakorlati kód és a hivatalos doksi a forrás.

---

## 1. Közös alap minden projektben

| Elem | Érték | Megjegyzés |
|---|---|---|
| Spring Boot parent | `4.1.1` | kivéve MintaZH: `3.5.6` |
| Java | `25` | kivéve MintaZH: `21` |
| Packaging | `war` | a JSP-s projekteknél szükséges |
| Port | `8090` | `server.port` az `application.yaml`-ban |
| Teszt | `contextLoads()` | üres smoke-teszt mindenhol |

Visszatérő csomagstruktúra: `controller` / `service` / `model` (+ `config`, `aspect`, `views`,
`annotations`). Ez a **rétegzett architektúra** – a kontroller nem tartalmaz üzleti logikát.

---

## 2. Gyak2 – Spring MVC alapok (`mvc` projekt)

**Téma:** DispatcherServlet életciklus, kontroller-annotációk, JSP + JSTL nézetek, Interceptor, AOP.
**Előadás:** 00. ea. 17–23. dia (és ugyanaz 01. ea. 6–11. dia), AOP: 00. ea. 14–16. dia.

### 2.1 Konfiguráció

`application.yaml` – **ViewResolver beállítás** (00. ea., 23. dia):
```yaml
spring.mvc.view.prefix: /WEB-INF/jsp/
spring.mvc.view.suffix: .jsp
```
Vagyis a kontroller `return "students"` visszatérése → `/WEB-INF/jsp/students.jsp`.
A `WEB-INF` alá tett JSP-ket a böngésző **közvetlenül nem érheti el**, csak forward útján – ez
szándékos védelem.

`pom.xml` lényege: `spring-boot-starter-webmvc`, `spring-boot-starter-validation`,
`tomcat-embed-jasper` (**provided** – a JSP fordító; embedded Tomcat: 02. ea., 10. dia),
`spring-boot-starter-aspectj`, JSTL API + Glassfish implementáció.

### 2.2 `StudentController` – a kontroller-annotációk katalógusa

| Annotáció | Mire való |
|---|---|
| `@Controller` + `@RequestMapping("/students")` | osztályszintű URL-prefix |
| `@GetMapping("/{id}")` | HTTP metódus-specifikus rövidítés |
| `@PathVariable(value="id") Long studentId` | URL-részlet → paraméter |
| `@RequestParam("name") String name` | query string (`?name=...`) → paraméter |
| `@RequestBody Student student` | JSON törzs → objektum (Jackson) |
| `@ResponseBody` | **nincs** nézetfeloldás, a visszatérési érték maga a válasz törzse |
| `Model model` + `model.addAttribute(...)` | adat átadása a nézetnek |

### 2.3 `StudentService` – in-memory „adatréteg”

`@Service`, `Map<Long, Student>` tároló, `@PostConstruct private void init()` a kezdőadatokhoz
(`@PostConstruct` az előadásban: 00. ea., 11. dia, az annotációs konfiguráció példájában).
`findByName` Java Stream-mel (`filter` + `toList`), `save` pedig megkülönbözteti a
*create* (id == null) és az *update* ágat.

### 2.4 `LoggingInterceptor` – a kéréskezelés 4 fázisa

Ez a **legfontosabb didaktikai elem** a gyakorlaton: a `HandlerInterceptor` három metódusa
pontosan a 00. ea. 21. diáján lévő folyamatábrát naplózza ki:

```
[1] DispatchServlet received: GET /students/2   ← preHandle
[2] HandlerMapping resolved to: <handler>       ← preHandle
[3] Controller finished, view name: ...         ← postHandle
[4] View rendered, response sent. Status 200    ← afterCompletion
```

Regisztrálása: `WebConfig implements WebMvcConfigurer` → `addInterceptors(registry)`.

> 🐞 **Csapda:** a `postHandle` feltétel nélkül hívja a `modelAndView.getViewName()`-et.
> `@ResponseBody`-s végpontnál a `modelAndView` **null** → NPE. Éles kódban `if (modelAndView != null)`
> kellene.

### 2.5 `LoggingAspect` – AOP a gyakorlatban

00. ea. 14–16. dia megvalósítása:
```java
@Aspect @Component
@Around("execution(public * hu.mik.pte.prog5.mvc.service.StudentService.*(..))")
public Object logArroundServiceCall(ProceedingJoinPoint joinPoint) throws Throwable { ... }
```
- **pointcut**: `execution(...)` kifejezés – mely metódusokra illeszkedjen
- **advice**: `@Around` – a `joinPoint.proceed()` *előtt* és *után* is fut (ezért tud időt mérni)
- A Spring ehhez **proxy**-t hoz létre a `StudentService` köré (16. dia ábrája)

### 2.6 JSP / JSTL nézetek

`students.jsp`, `search-results.jsp`, `student-details.jsp`, `error/404.jsp`, `error/error_page.jsp`.
Elemek: `<%@ taglib prefix="c" uri="jakarta.tags.core" %>`, `<c:forEach items var>`, `<c:if test>`,
EL kifejezések (`${student.name}`), `${pageContext.request.contextPath}` (context-path-helyes linkek),
`${param.name}` (a keresőmező visszatöltése).

### 2.7 Kikommentelt / „furcsa” részek és az okuk

| Hely | Kikommentelt kód | Miért |
|---|---|---|
| `StudentController` ctor | `// @Autowired` | **Felesleges.** Spring 4.3 óta ha egy osztálynak **egyetlen** konstruktora van, az implicit `@Autowired`. Konstruktoros injektálás a javasolt forma (final mező, kötelező függőség, tesztelhetőség). |
| `StudentController` | `// @RequestMapping(value="/students", method=RequestMethod.GET)` | A **régi, bőbeszédű** forma; helyette a `@GetMapping` kompozit annotáció. Funkcionálisan azonos, de olvashatóbb → az előadás 22. diáján még a régi szerepel. |
| `StudentController` | `// @GetMapping("/students/{name}/{age}/{id}")` | Demonstrációs maradék a több `@PathVariable`-ről. **Nem működne**, mert az osztályszintű `/students` prefix miatt `/students/students/...` lenne belőle. |
| `StudentController` | `// return "students";` | `@ResponseBody` mellett **nincs** nézetfeloldás – a `"students"` szó szerint a válasz törzse lenne, nem a JSP. A kettő kizárja egymást. |
| `StudentController` | `//@RequestMapping(..., method = RequestMethod.POST)` | Ua., mint fent, csak POST-ra → `@PostMapping`. |

### 2.8 További hibák, amikre a ZH-n figyelj

- **`search()` visszatérése `"search-result"`, a fájl viszont `search-results.jsp`** → a ViewResolver
  nem találja a nézetet, 500-as hiba. Klasszikus elgépelés-típusú ZH-csapda.
- **`src/main/resources/templates/students.jsp`** – ez egy **rossz helyre került duplikátum**.
  A `classpath:/templates/` a *Thymeleaf* konvenciója; JSP-t onnan a Jasper **nem** szolgál ki,
  annak `src/main/webapp/WEB-INF/...` alatt kell lennie. (Ld. Gyak5, ahol tényleg Thymeleaf lesz.)
- **`web/WEB-INF/web.xml`** – üres, 6.1-es deskriptor. A 00. ea. 20. diáján még *ebben* kellett a
  DispatcherServletet bekötni; Spring Bootban erre **nincs szükség** (02. ea., 7. dia:
  „Nincs szükség web.xml-re”). Itt is csak egy üres, örökölt váz maradt.
- `requests.http` / `.idea/httpRequests/` – IntelliJ HTTP kliens a REST-végpontok kipróbálásához
  (GET `/students/2`, POST `/students` JSON-nel).

---

## 3. Gyak3 – CRUD, Bean Validation, Lombok, profilok (`crud-mvc`)

**Téma:** teljes CRUD űrlapokkal, szerveroldali validáció, Lombok, *egy interface – több implementáció*.
**Előadás:** 02. ea. 3. dia (több implementáció), 4. dia (bean scope), 5–6. dia (Maven profile);
a MVC-alap továbbra is 00. ea. 21–23. dia.

### 3.1 `CarController` – a CRUD-séma

| Útvonal | Metódus | Szerep |
|---|---|---|
| `GET /cars` | `list` | lista, `model.addAttribute("cars", ...)` |
| `GET /cars/new` | `newForm` | üres `Car` a modellbe → űrlap |
| `POST /cars` | `create` | `@Valid @ModelAttribute("car") Car car, BindingResult` |
| `GET /cars/{id}/edit` | `editForm` | meglévő betöltése az űrlapra |
| `POST /cars/{id}` | `update` | mentés |
| `POST /cars/{id}/delete` | `delete` | törlés (POST, nem GET!) |

Két fontos minta:
1. **PRG (Post-Redirect-Get):** minden módosító művelet `return "redirect:/cars";` – így a frissítés
   (F5) nem küldi el újra a POST-ot.
2. **Validációs ciklus:** `if (bindingResult.hasErrors()) return "cars/form";` – hiba esetén
   *ugyanaz* a nézet jön vissza, a beírt adatokkal és a hibaüzenetekkel együtt.

### 3.2 Bean Validation a `Car` modellen

```java
@NotBlank(message = "Nem lehet üres") String make;
@NotBlank(message = "Nem lehet üres") String model;
@NotNull @Min(value = 1950, message = "Nem lehet 1950 előtti") Integer manufactureYear;
```
A `spring-boot-starter-validation` hozza a Hibernate Validatort. A `@Valid` kapcsolja be a
kontrollerben, a `BindingResult` gyűjti a hibákat, a `<form:errors>` jeleníti meg.

### 3.3 Lombok

`@Getter @Setter @ToString @RequiredArgsConstructor` – a boilerplate eltüntetése.
A `@RequiredArgsConstructor` a **`final` mezőkre** generál konstruktort – ezért működik a
`CarController`/`CarService` konstruktoros injektálása egyetlen `@Autowired` nélkül is.

> ⚠️ `Car`-on a `@RequiredArgsConstructor` **nincs** final mező mellett → paraméter nélküli
> konstruktort generál. Erre szüksége is van a Springnek a `@ModelAttribute` adatkötéshez
> (`new Car()` a `newForm`-ban). A 3 paraméteres konstruktor kézzel írt.

### 3.4 Profilok – „mi van, ha egy interface-nek több implementációja van?” (02. ea., 3. dia)

```java
public interface DataService { Collection<Car> getValues(); }

@Service(value = "plain") @Profile("plain") class InMemoryDataService  implements DataService
@Service(value = "mock")  @Profile("mock")  class InMemoryMockDataService implements DataService
```
Két megoldás egyszerre bemutatva:
- **`@Profile`** – a profil dönti el, melyik bean kerül egyáltalán a konténerbe;
- **elnevezett bean** (`@Service("plain")`) – `@Qualifier`-rel vagy a paraméternév egyeztetésével
  választható ki (ez a MintaZH 3. feladatának pontos mintája!).

A `CarService` már csak az **interface-re** hivatkozik (`private final DataService dataService`) –
ez a DI lényege (00. ea., 9. dia).

> 🐞 **Futtatási csapda:** az `application.yaml`-ban **nincs `spring.profiles.active`**.
> Így egyik `DataService` sem jön létre → `NoSuchBeanDefinitionException` induláskor.
> Indítsd pl. `-Dspring-boot.run.profiles=mock`-kal, VM-opcióval
> (`-Dspring.profiles.active=mock`) vagy IntelliJ run-configból. A ZH-n ez önállóan is
> kérdés lehet („állítsa be az alapértelmezett profilt”).

### 3.5 Spring form taglib (JSP)

`form.jsp`: `<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>`
```jsp
<form:form modelAttribute="car" action=".../cars${car.id == null ? '' : '/'.concat(car.id)}" method="post">
  <form:input path="make" id="make"/>
  <form:errors path="make" cssClass="error"/>
```
Egy űrlap szolgálja ki a *create* és az *update* esetet is – az `action` EL-kifejezéssel áll elő.
`list.jsp`: `<c:forEach>`, `<c:if test="${empty cars}">` üres állapot, a törlés **POST űrlap**
`onsubmit="return confirm(...)"` megerősítéssel (GET-tel törölni biztonsági/REST hiba lenne).

### 3.6 Kikommentelt részek és az okuk

| Hely | Kikommentelt kód | Miért |
|---|---|---|
| `CarService.init()` | `// this.save(this.create("Skoda","Octavia",2021));` + Suzuki | **Refaktorálás nyoma.** Eredetileg bedrótozott kezdőadat volt itt; ezt váltotta ki a `DataService` interface + profilok. Megtartva összehasonlításképpen: a hardcode-olt teszt-adat *nem ajánlott*, mert nem cserélhető, nem tesztelhető, nem profilfüggő. |

### 3.7 `Source` vs `Orai` eltérés

A `Gyak3-20261024/Source/` a kiosztott készletet tartalmazza (`form.jsp`, `list.jsp`, `style.css`,
`pom.xml`) – a `Source/pom.xml`-ből **hiányzik a Lombok dependency**, az órai `pom.xml`-ben
viszont már benne van. Ez volt az első órai lépés.

---

## 4. Gyak4 – Spring Security (`crud-mvc` + security)

**Téma:** autentikáció, autorizáció, jelszó-hashelés, saját `UserDetailsService`, regisztráció +
auto-login, metódusszintű jogosultság.
**Előadás:** 03. ea. teljes egészében – különösen 6. dia (filterek), 10. dia (`UserDetailsService`),
12. dia (`SecurityContext` beállítása).

### 4.1 `SecurityConfig`

```java
@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
```
- `@EnableWebSecurity(debug = true)` → részletes filter-chain log (03. ea., 6. dia szemléltetése)
- `@EnableMethodSecurity` → ez kapcsolja be a metódusszintű annotációkat.
  `securedEnabled = true` → `@Secured`, `jsr250Enabled = true` → `@RolesAllowed`.
  (`@PreAuthorize`/`@PostAuthorize` alapból aktív.)

A `SecurityFilterChain` bean (a modern, `WebSecurityConfigurerAdapter` **nélküli** forma):
```java
.requestMatchers("/css/**","/js/**","/images/**","/webjars/**").permitAll()
.requestMatchers("/error").permitAll()
.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
.requestMatchers("/register", "/login").permitAll()
.anyRequest().authenticated()
```
> 💡 A `dispatcherTypeMatchers(FORWARD, ERROR)` **kötelező JSP mellett**: a ViewResolver
> `forward`-dal jut el a `/WEB-INF/views/...jsp`-ig, és enélkül a Security a belső forwardot is
> külön kérésként vizsgálná → végtelen redirect / 403.

Jelszókezelés: `@Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }` –
**soha nem tárolunk nyílt jelszót**.

### 4.2 `AppUserDetailsService` (03. ea., 10. dia)

```java
@Service
public class AppUserDetailsService implements UserDetailsService {
    private final Map<String, AppUser> appUsers = new ConcurrentHashMap<>();
    public UserDetails loadUserByUsername(String username) { ... User.builder()... }
    public void register(String username, String rawPassword) { ... putIfAbsent ... }
}
```
- `AppUser` **record** (`username`, `passwordHash`, `roles`) – immutable adathordozó
- `ConcurrentHashMap` + `putIfAbsent` – szálbiztos „már létezik?” ellenőrzés egy lépésben
- `UsernameNotFoundException` dobása, ha nincs ilyen user
- `User.builder().roles(...)` – a `roles()` automatikusan `ROLE_` prefixet tesz elé

### 4.3 `RegisterController` – regisztráció + azonnali bejelentkeztetés

Ez **szó szerint a 03. ea. 12. diája** (`Setting SecurityContext`), kiegészítve a session-be mentéssel:
```java
Authentication auth = UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(auth);
SecurityContextHolder.setContext(context);
this.securityContextRepository.saveContext(context, request, response);   // ← HttpSessionSecurityContextRepository
```
> 💡 A `saveContext(...)` hívás nélkül a bejelentkezés **csak az aktuális kérésig élne** –
> a `SecurityContextHolder` `ThreadLocal`-ban tárol. A sessionbe mentés teszi tartóssá.
> A `UsernamePasswordAuthenticationToken.authenticated(...)` (3 paraméteres) factory a *már
> hitelesített* tokent jelzi; a 2 paraméteres `unauthenticated(...)` a hitelesítés *előtti* állapot.

### 4.4 `Car.owner` + `CarService.list()`

Új mező: `String owner`. A `list()` lekérdezi a bejelentkezett felhasználót:
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
var authorities = auth.getAuthorities();
return new ArrayList<>(this.carList.values());
```
Ez az előkészítése a tulajdonos szerinti szűrésnek (lásd a kikommentelt `@PostFilter`-t).

### 4.5 Kikommentelt részek és az okuk — **ez a gyakorlat legfontosabb része**

#### `SecurityConfig.securityFilterChain` – URL-szintű szabályok tárháza

| Kikommentelt | Mit demonstrál / miért nincs bekapcsolva |
|---|---|
| `.requestMatchers("/admin").denyAll()` | mindenkitől megtagad – demó, nincs `/admin` végpont a projektben |
| `.requestMatchers(HttpMethod.POST,"/admin").denyAll()` | ugyanaz **HTTP-metódusra szűkítve** – a `requestMatchers` túlterhelt változata |
| `.requestMatchers("/admin").hasAuthority("EDIT_FORM")` | **authority** (finomszemcsés engedély) alapján |
| `.requestMatchers("/admin").hasRole("ADMIN")` | **role** alapján – a `hasRole("ADMIN")` automatikusan `ROLE_ADMIN`-t keres, a `hasAuthority`-nak ki kell írni. A kettő összekeverése klasszikus hibaforrás. (03. ea., 4. dia: szerepkör ↔ engedély megkülönböztetése) |
| `.anyRequest().permitAll()` | ⛔ **Szándékosan kikapcsolva – nem biztonságos.** Mindent publikussá tenne. A helyette aktív `.anyRequest().authenticated()` a „secure by default” elv: amit nem engedtünk ki explicit, az védett. |

#### `//@Bean public UserDetailsService userDetailsService(...)`

A teljes metódus `@Bean` nélkül maradt – **több okból is:**
1. **Ütközés:** az `AppUserDetailsService` `@Service`-ként már szolgáltat egy `UserDetailsService`-t.
   Két ilyen bean mellett a Boot auto-konfigurációja nem tudná eldönteni, melyiket használja.
2. **Funkcióhiány:** az `InMemoryUserDetailsManager` fix felhasználókészlet –
   a **regisztráció** (új user felvétele futás közben) nem valósítható meg vele kényelmesen.
   Ezért cserélte le az órán a saját, `ConcurrentHashMap`-alapú implementációra.

Ezen belül további tanulságos kommentek:
| Kikommentelt | Miért |
|---|---|
| `//.roles("USER","VIEWER")` a `user`-nél | A `.roles(...)` és az `.authorities(...)` **nem kombinálható** – a `User.builder()`-ben a később hívott **felülírja** a korábbit. Ezért maradt csak az `.authorities("ROLE_USER","EDIT_FORM","DELETE_CAR")`, ahol a `ROLE_` prefixet kézzel kell kiírni. A komment pont ezt a két írásmódot állítja szembe. |

#### `CarService.list()` fölötti annotáció-torony

```java
//  @PreAuthorize("hasRole('ADMIN')")
//  @PostAuthorize("hasRole('ADMIN')")
//  @PreFilter("hasRole('ADMIN')")
//  @PostFilter("filterObject.owner == authentication.name or hasRole('ADMIN') or hasAuthority('EDIT_FORM')")
//  @Secured("ROLE_ADMIN")
//  @RolesAllowed("ADMIN")
```
Ezek **egymás alternatívái**, az órán egyesével kipróbálva – egyszerre nem lehetnek aktívak,
ezért maradt mind kikommentelve:

| Annotáció | Mikor fut | Megjegyzés |
|---|---|---|
| `@PreAuthorize` | hívás **előtt** | SpEL kifejezés, a legáltalánosabb |
| `@PostAuthorize` | hívás **után** | a `returnObject` is vizsgálható; a metódus már lefutott (mellékhatás!) |
| `@PreFilter` | hívás előtt, a **bemenő kollekciót** szűri | ⚠️ **Itt értelmetlen**: a `list()`-nek nincs kollekció paramétere. Valószínűleg csak a teljesség kedvéért került oda. |
| `@PostFilter` | hívás után, a **visszaadott kollekciót** szűri | `filterObject` = az aktuális elem. Ez indokolja, hogy a `list()` `new ArrayList<>(carList.values())`-t ad vissza és nem a `carList.values()`-t: a `@PostFilter` **módosítható (mutable) listát** vár, a `values()` view-ja viszont a `HashMap`-et módosítaná / `UnsupportedOperationException`-t dobhat. |
| `@Secured("ROLE_ADMIN")` | hívás előtt | régebbi, **nem SpEL** – teljes szerepkörnevet vár (`ROLE_` prefixszel). `securedEnabled = true` kell hozzá. |
| `@RolesAllowed("ADMIN")` | hívás előtt | JSR-250 szabvány, `jsr250Enabled = true` kell hozzá. Prefix nélkül. |

#### `annotations/IsAdmin.java` – befejezetlen

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
public interface IsAdmin { //TODO }
```
**Hiányos, nem működik**: `interface`-ként van deklarálva `@interface` helyett, az importált
`ElementType`/`Target` használatlan. A szándék egy **meta-annotáció** volt:
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')")
public @interface IsAdmin { }
```
Ezzel a hosszú SpEL kifejezések kiválthatók egy beszédes, újrahasznosítható annotációval.

### 4.6 Nézet

`register.jsp` – `<form:form modelAttribute="registerForm">`, `<form:password>`,
`autocomplete="username"` / `"new-password"` (böngésző-jelszókezelő támogatás).
A `RegisterForm`-ban van `confirmPassword` mező, de **nincs hozzá validáció** –
a kontrollerben `//TODO validations (exists, confirmPassword)` jelzi.
A `BindingResult` paraméter fel van véve, de nincs használva.

> 🐞 A `registerForm()` `return "/register";` – **vezető perjellel**. Ez a `prefix + name + suffix`
> összefűzéssel `/WEB-INF/views//register.jsp` lesz; itt még működik, de hibás forma.
> Gyak5-ben már javítva: `return "register";`.

---

## 5. Gyak5 – Thymeleaf + i18n + saját login (`crud-mvc`, `gyak5-thymeleaf` branch)

**Téma:** a JSP nézetréteg lecserélése Thymeleaf-re, fragmentek, lokalizáció, egyedi login/logout.
**Előadás:** nincs közvetlen fedés; a ViewResolver-elv a 00. ea. 23. diájához kapcsolódik,
a Security-rész a 03. ea.-hoz.
**Git:** `master (init) → gyak4-orai (security) → gyak5-thymeleaf (feat: thymeleaf)`.

### 5.1 Mi változott a `pom.xml`-ben

| Kikerült | Bekerült |
|---|---|
| `tomcat-embed-jasper` | `spring-boot-starter-thymeleaf` |
| `jakarta.servlet.jsp.jstl-api` | `thymeleaf-extras-springsecurity6` |
| `jakarta.servlet.jsp.jstl` (glassfish) | `spring-boot-devtools` (runtime, optional) |
| `spring-boot-starter-aspectj` | |

A **DevTools** a 02. ea. 11. diája: auto-restart classpath-változásra + LiveReload.

Nézetek helye: `src/main/resources/templates/` (classpath), nem `webapp/WEB-INF/`.

### 5.2 Thymeleaf-szintaxis, ami a gyakorlaton előjött

| Kifejezés | Jelentés |
|---|---|
| `th:text="${car.make}"` | változó-kifejezés, **HTML-escape-pel** (XSS-védelem) |
| `th:href="@{/cars}"` / `@{/cars/{id}/edit(id=${car.id})}` | **URL-kifejezés** – automatikusan hozzáteszi a context-path-t (a JSP-s `${pageContext.request.contextPath}` megfelelője) |
| `th:object="${car}"` + `th:field="*{make}"` | űrlap-kötés; a `*{...}` a `th:object`-hoz relatív |
| `th:errors="*{make}"` | validációs hibaüzenet |
| `th:each="car : ${cars}"`, `th:if="${cars.isEmpty()}"` | iteráció, feltétel |
| `#{car.list.title}` | **üzenet-kifejezés** – a `messages*.properties`-ből |
| `th:replace` / `th:insert` | fragment beillesztés |
| `sec:authorize="isAuthenticated()"`, `sec:authentication="name"` | a security-extras névtér |

### 5.3 Fragmentek – újrahasznosítható sablonrészek

`fragments/layout.html`:
```html
<head th:fragment="head(title)"> ... <title th:text="${title}"></title> ...
<header th:fragment="header(text)"> ... nyelvválasztó + logout ...
<footer th:fragment="footer(text)">
```
Használat: `<head th:replace="~{fragments/layout :: head('Car list')}"></head>`

`fragments/form-fields.html` – **paraméteres mezősablon**:
```html
<div th:fragment="field(name,label,type)">
  <label th:for="${name}" th:text="${label}"></label>
  <input th:type="${type}" th:id="${name}" th:field="*{__${name}__}"/>
  <span class="error" th:errors="*{__${name}__}"></span>
</div>
```
A `__${name}__` az **előfeldolgozó (preprocessing)** szintaxis: a `${name}` értéke *előbb*
behelyettesítődik, így lesz a `*{__${name}__}`-ből pl. `*{model}`. Enélkül nem lehetne
dinamikus a mezőnév.

> 🔍 **`th:replace` vs `th:insert`** – a `form.html` szándékosan mindkettőt használja egymás mellett:
> a `replace` **kicseréli** a host elemet a fragmentre, az `insert` pedig **beleteszi** a fragmentet a
> host elem *belsejébe* (így marad egy extra `<div>` burkolat). Jó összehasonlítási pont.

### 5.4 Lokalizáció (i18n)

- `messages.properties` (fallback, értéke `Car list default+` – hogy látszódjon, mikor esik ide),
  `messages_en.properties`, `messages_hu.properties`
- `application.yaml`: `spring.messages.fallback-to-system-locale: false` – **fontos**: enélkül
  ismeretlen locale esetén a gép nyelvi beállítása döntene, nem a `messages.properties`
- `WebConfig`:
  ```java
  @Bean LocaleResolver localeResolver() { SessionLocaleResolver r = ...; r.setDefaultLocale(Locale.of("en")); return r; }
  @Override addInterceptors(...) { LocaleChangeInterceptor i = ...; i.setParamName("lang"); registry.addInterceptor(i); }
  ```
  → a `?lang=hu` paraméter átállítja a nyelvet, a `SessionLocaleResolver` megjegyzi a sessionben.
- A `layout.html` headerje kétféle váltót is mutat: egyszerű linkeket (`<a href="?lang=en">`) **és**
  egy `<select name="lang" onchange="this.form.submit()">` legördülőt `th:selected="${#locale.language == 'hu'}"`-val.

### 5.5 Saját login/logout

```java
.formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/cars").failureUrl("/login?error"))
.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout"))
```
- `LoginController` szolgálja ki a `GET /login`-t → `login.html`
- a `login.html` űrlapja **kötött nevű mezőket** használ: `name="username"`, `name="password"`,
  `POST /login` – ezt a `UsernamePasswordAuthenticationFilter` várja (03. ea., 6–7. dia)
- a logout **POST űrlap** a layoutban – alapértelmezetten CSRF-védett, GET-tel nem működne

A `@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)` megmaradt, de ebben a
változatban nincs metódusszintű annotáció használatban.

### 5.6 Maradványok / hibák

| Hely | Probléma |
|---|---|
| `src/main/webapp/WEB-INF/views/*.jsp` (`cars/form.jsp`, `cars/list.jsp`, `cars/register.jsp`) | **Halott kód.** A Gyak4-ből maradtak, de a `tomcat-embed-jasper` kikerült a pom-ból → nincs JSP-motor, ezek a fájlok soha nem renderelődnek. |
| `application.yaml`: `spring.mvc.view.prefix/suffix` | Szintén maradvány: a JSP `InternalResourceViewResolver`-t konfigurálná. A Thymeleaf resolvernek magasabb a precedenciája, így ártalmatlan, de **félrevezető** – takarítani kellene. |
| `CarService.init()` | ugyanaz a kikommentelt hardcode-olt seed, mint Gyak3-ban (ld. 3.6) |
| `Car` modell | a Gyak4-ben felvett **`owner` mező eltűnt** – a `gyak5-thymeleaf` ág a Gyak3-as modellre épült vissza, mert itt a nézetréteg a téma, nem a tulajdonos szerinti szűrés |
| `form-fields.html` | az `<input>`-on `th:path` szerepel – ilyen attribútum **nincs** Thymeleafben (ez a JSP `form:input path=` öröksége). Hatástalan; a tényleges kötést a `th:field` végzi. |
| `login.html` | `<h1 th:text="Login">` – idézőjel nélkül a `Login` **változó-névként** értelmeződne; helyesen `th:text="'Login'"` (ahogy a többi helyen szerepel) vagy `#{...}`. |

---

## 6. Gyak6 – Vaadin Flow (`crud-mvc-vaadin`)

**Téma:** szerveroldali UI-komponens-modell; a HTML/sablon réteg teljes lecserélése Java kódra.
**Előadás:** nincs fedés – a Vaadin kizárólag gyakorlati anyag.
**Verzió:** Vaadin BOM `25.3.2`, `vaadin-spring-boot-starter`, `vaadin-dev` (optional).

### 6.1 Alkalmazás-váz

```java
@StyleSheet(Lumo.STYLESHEET)
//@StyleSheet(Aura.STYLESHEET)
@SpringBootApplication
public class CrudMvcApplication implements AppShellConfigurator
```
`AppShellConfigurator` – az alkalmazás-héj (PWA, téma, meta tagek) konfigurációs pontja.
A kikommentelt `Aura` a **másik beépített téma** – alternatíva, egyszerre csak egy lehet aktív.

### 6.2 Routing (`VaadinBasics` – tanulópélda osztály)

Ez az osztály szándékosan **a `@Route` összes változatát felsorolja kommentben**, mivel egy
osztályon csak **egyetlen** `@Route` lehet:

| Változat | Eredmény |
|---|---|
| `@Route()` | az osztálynévből képzett útvonal → `/vaadinbasics` |
| `@Route("basics")` | `/basics` |
| `@Route("basics/:id")` | kötelező paraméter → `/basics/42` |
| `@Route("basics/:id?")` | **opcionális** paraméter → `/basics` vagy `/basics/42` |
| `@Route("basics/:id([0-9]+)")` | regex-szel megkötött paraméter |
| `@Route("basics/:id?/edit")` | ← **ez az aktív** |
| `@RoutePrefix("admin")` | közös prefix → ténylegesen `/admin/basics/42/edit` |
| `@RouteAlias("")` | további URL ugyanarra a nézetre |

Paraméter kiolvasása: `BeforeEnterObserver` → `event.getRouteParameters().getLong("id")` (`Optional`).

Cím: `//@PageTitle("Cím")` **kikommentelve**, helyette `implements HasDynamicTitle` +
`getPageTitle()`. **Ok:** a `@PageTitle` **statikus String konstans** – nem tud lokalizált
(`getTranslation(...)`) vagy állapotfüggő címet adni. A `CarFormView` épp ezt használja ki:
„Új autó rögzítése” vs „Autó szerkesztése” az `id` meglététől függően.

### 6.3 Komponensek, amik előjöttek

| Komponens | Hol |
|---|---|
| `Button` + `addClickListener` / lambda a konstruktorban | mindenhol |
| `TextField`, `IntegerField` | `CarFormView` |
| `ComboBox<Locale>` + `setItemLabelGenerator` + `addValueChangeListener` | nyelvválasztó |
| `Notification.show(...)` | `VaadinBasics` |
| `Grid<Car>` – `addColumn`, `addComponentColumn`, `setItems`, `setEmptyStateText` | `CarListView` |
| `ConfirmDialog` – `setHeader/setText/setCancelable/addConfirmListener/open` | törlés megerősítése |
| `FormLayout`, `VerticalLayout`, `HorizontalLayout` | elrendezés |
| `H1/H3/Span/Div/Header/Footer`, `RouterLink` | HTML-elem wrapperek |
| `LoginForm`, `LoginI18n` | `LoginView` |

`Grid<Car> grid = new Grid<>(Car.class, false)` – a `false` azt jelenti: **ne** generáljon
automatikusan oszlopot minden property-ből, kézzel adjuk meg őket (és a sorrendjüket!).

### 6.4 `MainLayout implements RouterLayout`

A közös keret (header + tartalom + footer), a `@Route(..., layout = MainLayout.class)` köti be.
`showRouterLayoutContent(HasElement content)` felülírásával a tartalom egy `Div`-be kerül.
`@AnonymousAllowed` rajta – különben a login képernyő sem jelenne meg benne.

Bejelentkezett felhasználó a **Vaadin-specifikus `AuthenticationContext`**-ből:
```java
this.authenticationContext.isAuthenticated()
this.authenticationContext.getPrincipalName().orElse("")
this.authenticationContext.logout()      // ← a Vaadin-konform kijelentkezés
```

### 6.5 Adatkötés – `BeanValidationBinder`

```java
private final BeanValidationBinder<Car> binder = new BeanValidationBinder<>(Car.class);
//  private final Binder<Car> binder = new Binder<>(Car.class);

this.binder.forField(make).bind("make");
//  this.binder.forField(make).bind(Car::getMake, Car::setMake);
```

| Kikommentelt | Miért az aktív a jobb |
|---|---|
| `new Binder<>(Car.class)` | A sima `Binder` **nem veszi figyelembe** a `jakarta.validation` annotációkat (`@NotBlank`, `@Min`). A `BeanValidationBinder` automatikusan érvényesíti őket – így a Car-on már meglévő szabályok újrahasznosulnak, nem kell duplikálni a validációt. |
| `.bind(Car::getMake, Car::setMake)` | A getter/setter-párral történő kötés **típusbiztos**, fordítási időben ellenőrzött; a String-neves (`bind("make")`) reflexiót használ és csak futásidőben derül ki, ha elírod. Az órán mindkettő bemutatásra került – a String-es rövidebb, a lambdás biztonságosabb. |

Mentés: `binder.readBean(car)` (modell → űrlap) és `binder.writeBeanIfValid(car)`
(űrlap → modell, **csak ha érvényes**; `boolean`-t ad vissza).

### 6.6 Security Vaadinnal

```java
http.with(VaadinSecurityConfigurer.vaadin(), l -> l.loginView(LoginView.class));
//  http.formLogin(Customizer.withDefaults());
```
> **Miért van kikommentelve a `formLogin`?** A `VaadinSecurityConfigurer` **maga állítja be**
> a form login-t a megadott `LoginView`-ra, és emellett kezeli a Vaadin-specifikus igényeket
> (az internal `/VAADIN/**` erőforrások kiengedése, a CSRF és a Vaadin saját UIDL-kérései,
> a session-fixation). A kettő együtt **ütközne** / felülírná egymást.

Nézetszintű jogosultság annotációkkal (nem URL-mintákkal!):
- `@AnonymousAllowed` – `LoginView`, `MainLayout`
- `@PermitAll` – `CarListView`, `CarFormView`, `VaadinBasics`
- (`@RolesAllowed("ADMIN")` – a MintaZH-ban ez is kell)

`LoginView`: beépített `LoginForm`, `setAction("login")` (ide POST-ol),
`setForgotPasswordButtonVisible(false)`.

A felhasználók itt visszatértek az `InMemoryUserDetailsManager`-hez (nincs regisztráció),
`BCryptPasswordEncoder`-rel.

### 6.7 i18n Vaadinban

Más mechanizmus, mint a Spring MVC-ben!
- Fájlok: `src/main/resources/vaadin-i18n/translations.properties` + `_en` + `_hu`
  (a Vaadin `DefaultI18NProvider` konvenciója)
- Használat: `this.getTranslation("car.make")`, paraméterrel: `getTranslation("text.value-in-field", textField.getValue())`
  → a `.properties`-ben `{0}` placeholder
- Nyelvváltás: `VaadinSession.getCurrent().setLocale(locale)` + `UI.getCurrent().getPage().reload()`
  (a Vaadin komponensek a konstruktorban olvassák ki a szöveget, ezért kell újratöltés)
- **`ValidationMessages.properties`** – ez *nem* Vaadin, hanem **Bean Validation** (Hibernate Validator)
  konvenció. A `Car`-on `@NotBlank(message = "{validation.notBlank}")` – a kapcsos zárójel
  jelzi, hogy ez **kulcs**, nem szöveg, és innen oldódik fel.
  (Figyeld meg: `make` már kulcsot használ, `model` és `manufactureYear` még bedrótozott
  magyar szöveget – félbehagyott átállás.)

### 6.8 Kikommentelt részek összefoglalva

| Hely | Kikommentelt | Ok |
|---|---|---|
| `CrudMvcApplication` | `//@StyleSheet(Aura.STYLESHEET)` | alternatív téma, egyszerre egy lehet |
| `CarController` | `//@Controller`, `//@RequestMapping("/cars")` | ⛔ **Kikapcsolt bean.** A Vaadin vette át a UI-t; ha aktív maradna, a `/cars` MVC-végpont **ütközne** a `@Route("cars")`-szal, és olyan JSP nézeteket keresne (`cars/list`), amik már nem léteznek. |
| `HomeController` | `//@Controller` | Ugyanez: a `@GetMapping("/")` ütközne a `CarListView` `@RouteAlias("")`-ával a gyökér útvonalon. |
| `VaadinBasics` | 6 db `//@Route(...)` + `//@PageTitle` | alternatív útvonal-formák katalógusa; egyszerre csak egy `@Route` lehet |
| `SecurityConfig` | `//http.formLogin(Customizer.withDefaults())` | ütközne a `VaadinSecurityConfigurer`-rel (ld. 6.6) |
| `CarFormView` | `//new Binder<>(Car.class)`, `//.bind(Car::getMake, ...)` | a gyengébb / alternatív változatok (ld. 6.5) |
| `CarFormView` | `//formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1))` | reszponzív oszlopszám kényszerítése 1-re; csak stílus, nem funkció |
| `CarListView` | `//this.grid.addComponentColumn(this::createActions)` | **metódusreferencia** az aktív `i -> this.createActions(i)` lambda helyett – teljesen egyenértékű, csak szintaktikai alternatíva |
| `CarListView` | `//@Route("cars")` | a `layout` paraméter nélküli forma – akkor a nézet nem kerülne a `MainLayout`-ba |
| `CarService.init()` | ua. a hardcode-olt seed | ld. 3.6 |

### 6.9 Maradványok / hibák

- `application.yaml`-ban megint ott a JSP `view.prefix/suffix`, és a `webapp/WEB-INF/views/*.jsp`
  fájlok is – mindkettő **halott maradvány**.
- `CarListView` konstruktora: `this.grid.setItems(...)` **és** rögtön utána `refresh()` –
  ami ugyanazt csinálja. Duplikáció.
- `VaadinBasics`: két `textField` változó (mező és lokális), és a `this.add(...)`-ban a lokális
  **kétszer** szerepel. A `@RoutePrefix("admin")` miatt a tényleges URL `/admin/basics/:id?/edit`.
- `MainLayout` nyelvválasztója: `l.getDisplayLanguage(this.getLocale())` – a `VaadinBasics`-ben
  viszont `l.getDisplayLanguage(l)` (azaz a nyelv a saját nyelvén). Két eltérő megközelítés.
- A `pom.xml`-ben nincs `production` Maven profil (frontend build) – a MintaZH pom-jában van.

---

## 7. MintaZH (`MintaZHFeladatok/zh-vaadin`) – mire megy ki a játék

A `zh1.pdf` 8 feladata pontosan a fenti anyagra épül. Keresztreferencia:

| ZH feladat | Hol gyakoroltuk |
|---|---|
| **1.** Port beállítás + **dev profil**-függő port | Gyak2–6 `application.yaml` (`server.port`); profilok: Gyak3 §3.4, 02. ea. 5–6. dia |
| **2-1.** `LoginView` Vaadin `LoginForm`-mal, `/login` útvonalon, hibajelzéssel | Gyak6 `LoginView` + `BeforeEnterObserver` (a hibakezelés a `?error` query paraméterből) |
| **2-2.** A `SecurityConfig` felhasználói nem tudnak belépni | Gyak4/Gyak6 `SecurityConfig`; 03. ea. 10. dia |
| **3.** `PetService` implementáció, **interface-re** injektálva, a `mock` bean nevével | Gyak3 §3.4 – `DataService` + `@Service("mock")` + `@Qualifier` |
| **4.** `MainLayout` header: username, menü, logout | Gyak6 `MainLayout` + `AuthenticationContext` |
| **5-1.** `ListView` `Grid`-del, oszlopsorrenddel, szerepkörfüggő oszloppal | Gyak6 `CarListView` |
| **5-2./6-2.** Routing `layout = MainLayout.class`-szal | Gyak6 §6.2 |
| **6-1.** `RegisterView`, `ComboBox` enumból, **bindolás**, kötelező mezők | Gyak6 `CarFormView` + `BeanValidationBinder` |
| **7.** Lokalizált képernyőcímek | Gyak6 `HasDynamicTitle` + `getTranslation` (§6.2, §6.7) |
| **8-1.** Global method security engedélyezése | Gyak4 `@EnableMethodSecurity` |
| **8-2.** `REGISTER` jogosultság metóduson + menüben + nézeten | Gyak4 `@PreAuthorize`/`@RolesAllowed` (§4.5) + Gyak6 `@PermitAll`/`@RolesAllowed` |
| **8-3. (bónusz)** `listAll()` szűrése szerepkör szerint | Gyak4 kikommentelt **`@PostFilter`** – pontosan ez a minta! („figyelj a típusokra”: a `PetSpecies` enum vs a `getRolesAsString()` String-jei) |

### A MintaZH kiinduló állapot ismert hiányosságai (amiket javítani kell)

| Fájl | Mi a baj |
|---|---|
| `SecurityConfig` | Nincs rajta `@Configuration`, a `userDetailsManager()` nincs `@Bean`-nel annotálva, és a `return new InMemoryUserDetailsManager();` **üresen** tér vissza – a felépített `admin/dog/cat/uncommon` user-eket nem adja át. (2-2. feladat.) A `{noop}` prefix a jelszavakban: nincs hashelés – demóra jó, **élesben tilos**. |
| `PetService` | Csak interface, nincs implementáció (3. feladat). |
| `MainLayout` | Üres `AppLayout` leszármazott (4. feladat). |
| `ListView` | Teljesen üres osztály (5. feladat). |
| `LoginView` | Nincs `@Route`, nem komponens, a hibakezelés `//TODO` (2-1. feladat). |
| `RegisterView` | Váz: `ComboBox<PetSpecies>` + kikommentelt `setItems`/`setItemLabelGenerator` **útmutatás** (6. feladat). |
| `DBPetRepository` | Minden metódusa `UnsupportedOperationException` – szándékos: ez a „rossz” implementáció, hogy kiderüljön, ha nem a `mock`-ot injektáltad. |
| `application.properties` | Nincs `server.port`, nincs profil (1. feladat). |

---

## 8. Gyors ellenőrzőlista a ZH-ra

**Spring mag**
- [ ] IoC / DI, konstruktoros injektálás `final` mezővel (`@RequiredArgsConstructor`)
- [ ] Bean scope-ok: singleton (default), prototype, request, session — *02. ea., 4. dia*
- [ ] `@Component` / `@Service` / `@Controller` / `@Repository` / `@Configuration` + `@Bean`
- [ ] `@PostConstruct` inicializálás
- [ ] **Egy interface – több implementáció:** `@Profile`, `@Qualifier`, elnevezett bean, `@Primary`
- [ ] `spring.profiles.active`, `-P` Maven profil — *02. ea., 5–6. dia*

**Spring MVC**
- [ ] DispatcherServlet → HandlerMapping → Controller → ViewResolver → View — *00. ea., 21. dia*
- [ ] `@GetMapping`/`@PostMapping`, `@PathVariable`, `@RequestParam`, `@RequestBody`, `@ResponseBody`, `Model`
- [ ] `redirect:` és a PRG-minta
- [ ] `@Valid` + `BindingResult` + Bean Validation annotációk
- [ ] `HandlerInterceptor` és regisztrálása `WebMvcConfigurer`-rel
- [ ] AOP: `@Aspect`, `@Around`, `ProceedingJoinPoint`, pointcut-kifejezés — *00. ea., 14–16. dia*

**Nézetek**
- [ ] JSP + JSTL (`c:forEach`, `c:if`) + Spring form taglib (`form:form`, `form:input path`, `form:errors`)
- [ ] Thymeleaf: `th:text`, `@{...}`, `th:object`/`th:field`/`*{...}`, `th:each`, `th:if`, fragmentek, `#{...}`
- [ ] i18n: `messages_xx.properties`, `LocaleChangeInterceptor`, `SessionLocaleResolver`

**Security** — *03. ea.*
- [ ] `SecurityFilterChain` bean, `authorizeHttpRequests`, `requestMatchers`, `permitAll`/`authenticated`/`hasRole`/`hasAuthority`
- [ ] **role vs authority** (`ROLE_` prefix!)
- [ ] `PasswordEncoder` (BCrypt), `UserDetailsService.loadUserByUsername` — *10. dia*
- [ ] `formLogin` testreszabás, `logout`
- [ ] `@EnableMethodSecurity(securedEnabled, jsr250Enabled)` + `@PreAuthorize` / `@PostAuthorize` / `@PostFilter` / `@Secured` / `@RolesAllowed`
- [ ] `SecurityContextHolder` + `UsernamePasswordAuthenticationToken` kézi beállítása — *12. dia*

**Vaadin**
- [ ] `@Route` (paraméterek, opcionális, regex), `@RouteAlias`, `@RoutePrefix`, `layout = ...`
- [ ] `RouterLayout`, `BeforeEnterObserver`, `HasDynamicTitle` vs `@PageTitle`
- [ ] `Grid` (`addColumn`, `addComponentColumn`, `setItems`), `ComboBox`, `LoginForm`, `ConfirmDialog`
- [ ] `BeanValidationBinder`: `forField().bind()`, `readBean`, `writeBeanIfValid`
- [ ] `@AnonymousAllowed` / `@PermitAll` / `@RolesAllowed`, `VaadinSecurityConfigurer`, `AuthenticationContext`
- [ ] `getTranslation(...)`, `vaadin-i18n/translations*.properties`, `VaadinSession.setLocale`

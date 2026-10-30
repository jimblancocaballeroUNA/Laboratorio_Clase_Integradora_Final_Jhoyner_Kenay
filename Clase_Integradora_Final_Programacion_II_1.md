# Clase integradora final — Programación II

### Calidad de código, patrones, pruebas, depuración y defensa técnica

| Modalidad | Duración            | Entregable                                                                  |
| --------- | ------------------- | --------------------------------------------------------------------------- |
| Parejas   | 2 horas como máximo | Repositorio Git con el proyecto Maven, pruebas JUnit y `README.md` completo |

---

## 1. Propósito

Esta actividad integra varios de los principales contenidos trabajados durante el curso mediante un único ejercicio práctico.

El objetivo no es construir una aplicación grande, sino tomar un programa funcional y mejorarlo utilizando:

- Programación Orientada a Objetos.
- Colecciones.
- Excepciones personalizadas.
- Validaciones.
- Separación de responsabilidades.
- Patrón **Strategy**.
- Patrón **Observer**.
	- Pruebas con JUnit.
- Depuración.
- Documentación técnica.
- Git y Maven.
- Defensa técnica breve.

El sistema trabajará únicamente **en memoria**.

> En esta actividad no se utilizará JDBC, DAO ni conexión a base de datos.

---

## 2. Caso de trabajo

Se desarrollará un pequeño sistema para administrar **reservas de laboratorios**.

Cada reserva tendrá:

```text
Código
Solicitante
Laboratorio
Fecha
Hora de inicio
Hora final
Cantidad de personas
Estado
```

El sistema deberá:

```text
Registrar reservas
Validar datos
Evitar reservas superpuestas
Aplicar diferentes reglas de validación
Notificar cuando una reserva sea registrada
Permitir consultar las reservas creadas
```

---

## 3. Organización general

La actividad se desarrollará sobre un repositorio Git proporcionado por el docente.

Cada pareja deberá:

1. clonar el repositorio (enlace indicado por el docente);
2. ejecutar el proyecto;
3. localizar con el depurador el error intencional y corregirlo;
4. identificar problemas de calidad;
5. refactorizar el código;
6. completar los patrones solicitados;
7. crear pruebas;
8. documentar las mejoras;
9. realizar commits durante todo el proceso, no solo al final;
10. explicar las decisiones tomadas.

---

## 4. Ruta de trabajo

```text
Ejecutar el proyecto
      ↓
Depurar y corregir el error del horario
      ↓
Diagnóstico de calidad
      ↓
Regla de superposición
      ↓
Tercera Strategy
      ↓
Segundo Observer
      ↓
Pruebas JUnit
      ↓
README
      ↓
Microdefensa
```

---

## 5. Proyecto Maven

La estructura propuesta será:

```text
reserva-laboratorio/
│
├── pom.xml
├── README.md
│
└── src/
    ├── main/
    │   └── java/
    │       └── cr/ac/una/reservas/
    │           ├── App.java
    │           ├── model/
    │           │   ├── Reserva.java
    │           │   └── EstadoReserva.java
    │           ├── exception/
    │           │   └── ReservaInvalidaException.java
    │           ├── strategy/
    │           │   ├── ValidadorReserva.java
    │           │   ├── ValidacionBasica.java
    │           │   └── ValidacionCapacidad.java
    │           ├── observer/
    │           │   ├── ReservaObserver.java
    │           │   └── ConsolaObserver.java
    │           └── service/
    │               └── ReservaService.java
    │
    └── test/
        └── java/
            └── cr/ac/una/reservas/
                └── ReservaServiceTest.java
```

---

## 6. Archivo `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="
             http://maven.apache.org/POM/4.0.0
             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>cr.ac.una</groupId>
    <artifactId>reserva-laboratorio</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>25</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.11.4</junit.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.2</version>
            </plugin>

            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <mainClass>cr.ac.una.reservas.App</mainClass>
                </configuration>
            </plugin>

        </plugins>
    </build>

</project>
```

> **Versión de Java.** `maven.compiler.release` debe coincidir con el JDK instalado. Si su equipo tiene JDK 21, cambie `25` por `21`; de lo contrario Maven mostrará `release version 25 not supported`. El proyecto necesita como mínimo Java 17. Verifique su versión con `java -version`.

---

## 7. Comandos Maven

| Comando | Para qué sirve |
|---|---|
| `mvn clean compile` | Borra lo compilado anteriormente y compila el código. |
| `mvn test` | Compila y ejecuta las pruebas JUnit. |
| `mvn exec:java` | Ejecuta la clase `App`. Ejecute antes `mvn compile`. |

> Al inicio, `mvn test` **fallará**: la prueba `debeAceptarReservaValida` no pasa mientras exista el error intencional de la sección 11. Es lo esperado.

---

## 8. Modelo `EstadoReserva`

```java
package cr.ac.una.reservas.model;

public enum EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA
}
```

---

## 9. Clase `Reserva`

```java
package cr.ac.una.reservas.model;

import java.time.LocalDate;
import java.util.Objects;

public class Reserva {

    private String codigo;
    private String solicitante;
    private String laboratorio;
    private LocalDate fecha;
    private int horaInicio;
    private int horaFinal;
    private int cantidadPersonas;
    private EstadoReserva estado;

    public Reserva(
            String codigo,
            String solicitante,
            String laboratorio,
            LocalDate fecha,
            int horaInicio,
            int horaFinal,
            int cantidadPersonas
    ) {
        this.codigo = codigo;
        this.solicitante = solicitante;
        this.laboratorio = laboratorio;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.cantidadPersonas = cantidadPersonas;
        this.estado = EstadoReserva.PENDIENTE;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getHoraInicio() {
        return horaInicio;
    }

    public int getHoraFinal() {
        return horaFinal;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void confirmar() {
        estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar() {
        estado = EstadoReserva.CANCELADA;
    }

    @Override
    public String toString() {
        return codigo
                + " | "
                + solicitante
                + " | "
                + laboratorio
                + " | "
                + fecha
                + " | "
                + horaInicio
                + ":00-"
                + horaFinal
                + ":00 | "
                + cantidadPersonas
                + " personas | "
                + estado;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Reserva otra)) {
            return false;
        }

        return Objects.equals(codigo, otra.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
```

---

## 10. Excepción personalizada

```java
package cr.ac.una.reservas.exception;

public class ReservaInvalidaException extends Exception {

    public ReservaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
```

---

## 11. Patrón Strategy

### Problema

Si todas las reglas se escriben dentro de `registrarReserva()`, el método termina mezclando demasiadas responsabilidades. Además, cada regla nueva obligaría a modificar ese método.

Con Strategy, cada regla de validación vive en su propia clase, y el servicio solo recorre la lista de validadores.

### Interfaz

```java
package cr.ac.una.reservas.strategy;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;

public interface ValidadorReserva {

    void validar(Reserva reserva)
            throws ReservaInvalidaException;
}
```

### Strategy 1 — Validación básica

```java
package cr.ac.una.reservas.strategy;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;

public class ValidacionBasica
        implements ValidadorReserva {

    @Override
    public void validar(Reserva reserva)
            throws ReservaInvalidaException {

        if (reserva == null) {
            throw new ReservaInvalidaException(
                    "La reserva no puede ser nula"
            );
        }

        if (reserva.getCodigo() == null
                || reserva.getCodigo().isBlank()) {
            throw new ReservaInvalidaException(
                    "El código es obligatorio"
            );
        }

        if (reserva.getSolicitante() == null
                || reserva.getSolicitante().isBlank()) {
            throw new ReservaInvalidaException(
                    "El solicitante es obligatorio"
            );
        }

        if (reserva.getFecha() == null) {
            throw new ReservaInvalidaException(
                    "La fecha es obligatoria"
            );
        }

        /*
         * Existe un error intencional en esta condición.
         * Utilice el depurador para identificarlo.
         */
        if (reserva.getHoraInicio()
                < reserva.getHoraFinal()) {

            throw new ReservaInvalidaException(
                    "La hora final debe ser posterior "
                    + "a la hora inicial"
            );
        }
    }
}
```

> Este código contiene deliberadamente un error lógico que deberá localizarse utilizando el depurador.

### Strategy 2 — Capacidad

```java
package cr.ac.una.reservas.strategy;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;

public class ValidacionCapacidad
        implements ValidadorReserva {

    private final int capacidadMaxima;

    public ValidacionCapacidad(
            int capacidadMaxima
    ) {
        this.capacidadMaxima = capacidadMaxima;
    }

    @Override
    public void validar(Reserva reserva)
            throws ReservaInvalidaException {

        if (reserva.getCantidadPersonas() <= 0) {

            throw new ReservaInvalidaException(
                    "La cantidad de personas "
                    + "debe ser mayor que cero"
            );
        }

        if (reserva.getCantidadPersonas()
                > capacidadMaxima) {

            throw new ReservaInvalidaException(
                    "La cantidad de personas supera "
                    + "la capacidad del laboratorio"
            );
        }
    }
}
```

### Idea del patrón

```text
                     «interfaz»
                  ValidadorReserva
                         ▲
         ┌───────────────┼────────────────┐
         │               │                │
ValidacionBasica  ValidacionCapacidad  (su tercera Strategy)
```

---

## 12. Patrón Observer

### Problema

Si `ReservaService` imprimiera directamente cada aviso, quedaría acoplado a la consola. Para agregar otro tipo de notificación (un resumen, un registro, un correo) habría que modificar el servicio.

Con Observer, el servicio solo avisa "se registró una reserva" a todos los observadores inscritos, sin saber qué hace cada uno.

### Interfaz

```java
package cr.ac.una.reservas.observer;

import cr.ac.una.reservas.model.Reserva;

public interface ReservaObserver {

    void reservaRegistrada(Reserva reserva);
}
```

### Observer de consola

```java
package cr.ac.una.reservas.observer;

import cr.ac.una.reservas.model.Reserva;

public class ConsolaObserver
        implements ReservaObserver {

    @Override
    public void reservaRegistrada(
            Reserva reserva
    ) {

        System.out.println(
                "[NOTIFICACIÓN] Reserva creada: "
                + reserva.getCodigo()
        );
    }
}
```

### Idea del patrón

```text
ReservaService
      │
      │ notifica
      ▼
  «interfaz»
ReservaObserver
      ▲
      │
 ┌────┴──────────────┐
 │                   │
ConsolaObserver   (su segundo Observer)
```

---

## 13. Servicio `ReservaService`

```java
package cr.ac.una.reservas.service;

import java.util.ArrayList;
import java.util.List;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.observer.ReservaObserver;
import cr.ac.una.reservas.strategy.ValidadorReserva;

public class ReservaService {

    private final List<Reserva> reservas;
    private final List<ValidadorReserva> validadores;
    private final List<ReservaObserver> observers;

    public ReservaService() {
        reservas = new ArrayList<>();
        validadores = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public void agregarValidador(
            ValidadorReserva validador
    ) {
        validadores.add(validador);
    }

    public void agregarObserver(
            ReservaObserver observer
    ) {
        observers.add(observer);
    }

    public void registrarReserva(
            Reserva reserva
    ) throws ReservaInvalidaException {

        for (ValidadorReserva validador
                : validadores) {

            validador.validar(reserva);
        }

        validarCodigoDuplicado(reserva);
        validarSuperposicion(reserva);

        reservas.add(reserva);
        notificarRegistro(reserva);
    }

    private void validarCodigoDuplicado(
            Reserva nueva
    ) throws ReservaInvalidaException {

        for (Reserva existente : reservas) {

            if (existente.getCodigo()
                    .equalsIgnoreCase(
                            nueva.getCodigo()
                    )) {

                throw new ReservaInvalidaException(
                        "Ya existe una reserva "
                        + "con ese código"
                );
            }
        }
    }

    private void validarSuperposicion(
            Reserva nueva
    ) throws ReservaInvalidaException {

        /*
         * TODO:
         * Implementar la regla que impida
         * reservas superpuestas para el mismo
         * laboratorio y fecha.
         */
    }

    private void notificarRegistro(
            Reserva reserva
    ) {

        for (ReservaObserver observer
                : observers) {

            observer.reservaRegistrada(reserva);
        }
    }

    public List<Reserva> listarReservas() {
        return List.copyOf(reservas);
    }
}
```

---

## 14. Regla de superposición

La pareja deberá implementar `validarSuperposicion(...)`.

Existe conflicto cuando coinciden:

```text
mismo laboratorio
+
misma fecha
+
horarios superpuestos
```

Ejemplo:

```text
08:00 ─────────── 10:00
       09:00 ─────────── 11:00

CONFLICTO
```

En cambio:

```text
08:00 ───── 10:00
              10:00 ───── 12:00

VÁLIDO (una termina exactamente cuando empieza la otra)
```

Revise también estos casos antes de programar:

| Reserva existente | Reserva nueva | Resultado esperado |
|---|---|---|
| Lab 1, hoy, 8–10 | Lab 1, hoy, 9–11 | Conflicto |
| Lab 1, hoy, 8–12 | Lab 1, hoy, 9–10 (queda adentro) | Conflicto |
| Lab 1, hoy, 8–10 | Lab 1, hoy, 8–10 (mismo horario) | Conflicto |
| Lab 1, hoy, 8–10 | Lab 1, hoy, 10–12 | Válido |
| Lab 1, hoy, 8–10 | Lab 2, hoy, 8–10 | Válido (otro laboratorio) |
| Lab 1, hoy, 8–10 | Lab 1, mañana, 8–10 | Válido (otra fecha) |

> **Pista.** Intente escribir **una sola condición** que cubra todos los casos de conflicto. Piense en qué debe cumplirse entre el inicio de una reserva y el final de la otra.
>
> **Para discutir.** ¿Una reserva `CANCELADA` debería bloquear el horario? Justifique su decisión en el README.

---

## 15. Clase `App`

```java
package cr.ac.una.reservas;

import java.time.LocalDate;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.observer.ConsolaObserver;
import cr.ac.una.reservas.service.ReservaService;
import cr.ac.una.reservas.strategy.ValidacionBasica;
import cr.ac.una.reservas.strategy.ValidacionCapacidad;

public class App {

    public static void main(String[] args) {

        ReservaService service =
                new ReservaService();

        service.agregarValidador(
                new ValidacionBasica()
        );

        service.agregarValidador(
                new ValidacionCapacidad(30)
        );

        service.agregarObserver(
                new ConsolaObserver()
        );

        Reserva reserva =
                new Reserva(
                        "R-001",
                        "Ana",
                        "Laboratorio 1",
                        LocalDate.now(),
                        8,
                        10,
                        20
                );

        try {

            service.registrarReserva(reserva);

            System.out.println();
            System.out.println(
                    "Reservas registradas:"
            );

            for (Reserva r
                    : service.listarReservas()) {

                System.out.println(r);
            }

        } catch (ReservaInvalidaException ex) {

            System.out.println(
                    "No se pudo registrar: "
                    + ex.getMessage()
            );
        }
    }
}
```

---

## 16. Primera ejecución

Ejecute:

```bash
mvn clean compile
mvn exec:java
```

La reserva utilizada es:

```text
08:00 - 10:00
```

y el programa la rechazará con el mensaje:

```text
No se pudo registrar: La hora final debe ser posterior a la hora inicial
```

No corrija el código inmediatamente. Utilice primero el depurador.

---

## 17. Actividad de depuración

Ubique:

```java
ValidacionBasica.validar()
```

Coloque un breakpoint sobre la validación del horario y ejecute `App` en **modo depuración** desde el IDE (*Debug*, no *Run*). También puede depurar la prueba `debeAceptarReservaValida`.

Avance paso a paso y observe:

```text
horaInicio
horaFinal
resultado de la condición
```

Conteste:

```text
¿Por qué una reserva válida
de 08:00 a 10:00 está siendo rechazada?
```

Corrija la condición. Después de corregirla, piense qué debería pasar con una reserva de 10:00 a 10:00.

Haga un commit con la corrección antes de continuar.

---

## 18. Diagnóstico de calidad

Identifique al menos cinco aspectos de calidad presentes o pendientes.

Puede analizar:

```text
Responsabilidades
Nombres
Duplicación
Acoplamiento
Validaciones
Mensajes
Manejo de errores
Métodos largos
Comentarios
Colecciones
Tipos de datos
Inmutabilidad
Consistencia entre clases
```

Para cada situación, indique la clase o el método donde la encontró.

Complete en el README (sección *Problemas detectados*):

| Situación detectada | Mejora realizada |
|---|---|
| | |
| | |
| | |
| | |
| | |

---

## 19. Implementación obligatoria

La pareja deberá:

### A. Corregir el error del horario

Localícelo con el depurador antes de modificar el código (secciones 16 y 17).

### B. Implementar superposición

Completar en `ReservaService`:

```java
validarSuperposicion(...)
```

según la regla de la sección 14. Debe lanzar `ReservaInvalidaException` con un mensaje claro.

### C. Agregar una tercera Strategy

Crear, en el paquete `strategy`, otra clase que implemente:

```java
ValidadorReserva
```

Puede validar, por ejemplo:

```text
fecha no anterior a hoy
horario permitido entre 7 y 21
longitud mínima del código
laboratorio obligatorio
```

Regístrela en `App` y en el `setUp()` de las pruebas con `agregarValidador(...)`.

### D. Agregar un segundo Observer

Crear, en el paquete `observer`, una clase como:

```text
ResumenObserver
```

que muestre una notificación distinta a la de `ConsolaObserver` (por ejemplo, laboratorio, fecha y horario de la reserva, o cuántas reservas lleva registradas). Regístrela en `App` con `agregarObserver(...)`.

> Observe que para los puntos C y D **no debería ser necesario modificar el código interno de `ReservaService`**. Esa es la ventaja de ambos patrones.

---

## 20. Pruebas con JUnit

El repositorio incluye estas dos pruebas. La primera fallará hasta que se corrija el error del horario.

```java
package cr.ac.una.reservas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.service.ReservaService;
import cr.ac.una.reservas.strategy.ValidacionBasica;
import cr.ac.una.reservas.strategy.ValidacionCapacidad;

class ReservaServiceTest {

    private ReservaService service;

    @BeforeEach
    void setUp() {

        service = new ReservaService();

        service.agregarValidador(
                new ValidacionBasica()
        );

        service.agregarValidador(
                new ValidacionCapacidad(30)
        );
    }

    @Test
    void debeAceptarReservaValida()
            throws Exception {

        Reserva reserva =
                new Reserva(
                        "R-001",
                        "Ana",
                        "Lab 1",
                        LocalDate.now(),
                        8,
                        10,
                        20
                );

        service.registrarReserva(reserva);

        assertEquals(
                1,
                service.listarReservas().size()
        );
    }

    @Test
    void debeRechazarCantidadPersonasCero() {

        Reserva reserva =
                new Reserva(
                        "R-002",
                        "Luis",
                        "Lab 1",
                        LocalDate.now(),
                        10,
                        12,
                        0
                );

        assertThrows(
                ReservaInvalidaException.class,
                () -> service.registrarReserva(
                        reserva
                )
        );
    }
}
```

---

## 21. Tercera prueba obligatoria

Crear:

```java
@Test
void debeRechazarReservasSuperpuestas() {
    ...
}
```

La prueba debe:

```text
Registrar una primera reserva válida.
Intentar registrar una segunda.
Usar el mismo laboratorio.
Usar la misma fecha.
Usar horarios superpuestos.
Comprobar que se lanza ReservaInvalidaException.
Comprobar que solo quedó una reserva registrada.
```

> **Pruebas adicionales recomendadas** (tome los casos de la tabla de la sección 14):
>
> - `debeAceptarReservasConsecutivas()`: 8–10 y luego 10–12 en el mismo laboratorio.
> - `debeAceptarMismoHorarioEnOtroLaboratorio()`.
> - Una prueba para la tercera Strategy que usted creó.

---

## 22. Estructura de una prueba

```text
ARRANGE
Preparar objetos.

ACT
Ejecutar la operación.

ASSERT
Comprobar el resultado.
```

---

## 23. Ejecutar las pruebas

```bash
mvn test
```

El objetivo es finalizar con:

```text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

(El número de pruebas será mayor si agregó pruebas adicionales.)

---

## 24. Relación con otros patrones del curso

En esta actividad se implementan directamente:

```text
Strategy
Observer
```

Además, deben relacionarse conceptualmente con:

### MVC

```text
Vista
  ↓
Controller
  ↓
ReservaService
  ↓
Modelo
```

En este proyecto no hay interfaz gráfica: `App` cumple a la vez el papel de vista y de controlador, `ReservaService` contiene la lógica y `Reserva` es el modelo.

### DAO

No se utiliza en esta actividad porque no existe conexión a base de datos. Aquí la lista `reservas` dentro de `ReservaService` cumple el papel de almacenamiento en memoria.

```text
ReservaService
      ↓
ReservaDAO
      ↓
Base de datos
```

### Singleton

Podría utilizarse para administrar una instancia única de configuración o un servicio compartido. No es obligatorio implementarlo.

### Adapter

Sería útil si el sistema necesitara utilizar una clase externa cuya interfaz no coincide con la que requiere el programa. No es obligatorio implementarlo.

---

## 25. README obligatorio

El archivo `README.md` deberá seguir esta plantilla:

````markdown
# Reserva de Laboratorios

## Integrantes

- Nombre 1
- Nombre 2

## Objetivo

Descripción breve.

## Problemas detectados

| Situación detectada | Mejora realizada |
|---|---|
| | |

## Patrón Strategy

Explique dónde se utilizó y por qué. Indique qué regla valida su tercera Strategy.

## Patrón Observer

Explique dónde se utilizó y por qué.

## Excepción personalizada

Indique dónde se utiliza.

## Regla de superposición

Explique cómo se resolvió y qué decidió sobre las reservas canceladas.

## Pruebas

Indique las pruebas implementadas.

## Error detectado con depuración

Explique:
- dónde estaba;
- qué ocurría;
- cómo se corrigió.

## Ejecución

```bash
mvn clean compile
mvn test
mvn exec:java
```
````

---

## 26. Commits sugeridos

```text
Corrige validación de horario mediante depuración
Implementa regla de superposición
Agrega estrategia de validación de fecha
Agrega ResumenObserver
Agrega prueba de reservas superpuestas
Actualiza README con diagnóstico y decisiones
```

Cada commit debe describir **qué cambió**, en una sola idea. Adapte los mensajes a lo que usted realmente hizo.

Evite mensajes como:

```text
avance
cambios
final
prueba
```

---

## 27. Microdefensa

Cualquier integrante deberá poder responder:

```text
¿Por qué Strategy es útil?

¿Qué tendría que cambiar para agregar
otra validación?

¿Qué problema evita Observer?

¿Dónde se lanza ReservaInvalidaException?

¿Cómo se comprobó la superposición?

¿Qué prueba demuestra que la regla funciona?

¿Qué error se encontró con el depurador?

¿Por qué ReservaService no imprime
directamente todas las notificaciones?

¿Qué cambiaría si existiera una base de datos?
```

---

## 28. Distribución sugerida de la actividad

| Etapa | Actividad | Tiempo aproximado |
|---|---|---|
| Inicio | Clonar, ejecutar el proyecto y comprender el caso | 10 min |
| Depuración | Localizar y corregir el error intencional | 15 min |
| Diagnóstico | Identificar problemas de calidad | 15 min |
| Desarrollo | Implementar la regla de superposición | 20 min |
| Patrones | Agregar la tercera Strategy y el segundo Observer | 20 min |
| Pruebas | Completar las pruebas JUnit | 15 min |
| Documentación | Completar el README | 10 min |
| Cierre | Microdefensa | 15 min |
| **Total** | | **120 min** |

La actividad completa debe desarrollarse en un máximo de dos horas.

---

## 29. Producto final

```text
[ ] Proyecto Maven funcional

[ ] Código organizado por paquetes

[ ] Clase Reserva

[ ] Enum EstadoReserva

[ ] ReservaService

[ ] ReservaInvalidaException

[ ] Strategy implementado

[ ] Al menos tres estrategias

[ ] Observer implementado

[ ] Al menos dos observers

[ ] Regla de superposición

[ ] Error corregido con depuración

[ ] Al menos tres pruebas JUnit (incluida la de superposición)

[ ] mvn test termina con BUILD SUCCESS

[ ] README completo

[ ] Commits descriptivos durante el proceso

[ ] Proyecto compilando y ejecutando correctamente
```

---

## 30. Criterio de cierre

La meta no es agregar muchas líneas de código.

La meta es demostrar que un programa puede evolucionar desde:

```text
funciona
```

hacia:

```text
funciona
+
es comprensible
+
es validable
+
es extensible
+
es comprobable
+
puede defenderse técnicamente
```

Ese proceso resume buena parte de los aprendizajes esperados al finalizar Programación II.

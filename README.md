# OnlySwapX ⚡
**CS 2031 Desarrollo Basado en Plataforma**

---


**Integrantes:**
- Diego Fabricio Godoy Torres
- Guillermo Valentino Ceceu Gamboa Sanchez
- Leonardo Jesús Medina Gago
- Pedro Nicolas Solis Cordova
- Thiago Frias Pinto 

---

## Índice

1. [Introducción](#introducción)
2. [Identificación del Problema o Necesidad](#identificación-del-problema-o-necesidad)
3. [Descripción de la Solución](#descripción-de-la-solución)
4. [Modelo de Entidades](#modelo-de-entidades)
5. [Testing y Manejo de Errores](#testing-y-manejo-de-errores)
6. [Medidas de Seguridad Implementadas](#medidas-de-seguridad-implementadas)
7. [Eventos y Asincronía](#eventos-y-asincronía)
8. [GitHub & Management](#github--management)
9. [Conclusión](#conclusión)
10. [Apéndices](#apéndices)

---

## Introducción

### Contexto

En el entorno universitario peruano, los estudiantes poseen una gran diversidad de habilidades y conocimientos especializados, muchos de los cuales permanecen subutilizados. Un alumno de Ingeniería de Sistemas puede dominar la programación en Python, mientras que uno de Diseño Gráfico tiene dominio en herramientas de prototipado; sin embargo, rara vez existe un canal formal y accesible que facilite la colaboración directa entre ellos fuera del aula. Las plataformas de tutoría disponibles en el mercado están orientadas principalmente a relaciones comerciales con instructores profesionales, lo que las hace costosas o poco atractivas para estudiantes que simplemente desean aprender de sus pares.

### Objetivos del Proyecto

- Proveer una plataforma web que conecte a estudiantes universitarios para intercambiar habilidades de forma estructurada.
- Implementar un sistema de créditos interno que regule las transacciones de manera justa y segura mediante un mecanismo de escrow.
- Garantizar la autenticidad de los usuarios a través de autenticación con JWT y cifrado de contraseñas.
- Integrar inteligencia artificial (Gemini) para enriquecer el análisis semántico de las habilidades registradas.
- Construir una arquitectura de backend robusta, escalable y bien estructurada usando Spring Boot y buenas prácticas de diseño.

---

## Identificación del Problema o Necesidad

### Descripción del Problema

Los estudiantes universitarios carecen de una plataforma centralizada que les permita encontrar compañeros con las habilidades que necesitan aprender y, al mismo tiempo, ofrecer las propias. Esta brecha genera que el conocimiento se quede dentro de silos académicos, impidiendo el aprendizaje horizontal entre pares. Cuando un estudiante necesita ayuda en un tema específico (por ejemplo, estadística, diseño UX o redacción académica), sus opciones son limitadas: recurrir a tutores pagados o buscar ayuda de forma informal e inconsistente.

Adicionalmente, la ausencia de un sistema de validación y compromiso mutuo hace que los acuerdos informales frecuentemente queden incompletos, generando frustración y pérdida de tiempo.

### Justificación

Resolver este problema tiene un impacto directo en la calidad del aprendizaje universitario y en el desarrollo de competencias blandas como la colaboración y la comunicación. Una plataforma de intercambio de habilidades fomenta el aprendizaje colaborativo, reduce la dependencia de recursos externos costosos y permite a los estudiantes reconocer y valorar su propio conocimiento. La relevancia de esta solución se alinea con tendencias globales de economías de intercambio (sharing economy), adaptadas al contexto académico.

---

## Descripción de la Solución

### Funcionalidades Implementadas

**Autenticación y Gestión de Usuarios**  
Los usuarios pueden registrarse (`POST /api/auth/sign-up`) e iniciar sesión (`POST /api/auth/sign-in`). El sistema emite un JWT firmado con HMAC-SHA que contiene el email y el rol del usuario. Al registrarse, cada usuario recibe automáticamente 10 créditos iniciales. También pueden consultar su perfil propio (`GET /api/users/me`) o el de otro usuario por ID.

**Registro de Habilidades**  
Cada usuario puede publicar habilidades que ofrece (`OFFER`) o que desea aprender (`WANT`), con nombre, descripción, categoría y nivel de dominio. Al registrar una habilidad, el sistema lanza de forma asíncrona un análisis semántico a través de la API de Google Gemini para enriquecer la información.

**Solicitudes de Intercambio (Exchange Requests)**  
Un usuario puede enviar una solicitud de intercambio a otro. El receptor puede aceptarla o rechazarla. Las solicitudes aceptadas habilitan la creación de una sesión de aprendizaje y el uso del sistema de mensajería.

**Mensajería Interna**  
Una vez que un intercambio está activo, los participantes pueden enviarse mensajes dentro del contexto de ese intercambio, con soporte para tipos de mensaje `TEXT`, `SESSION_CARD` y `SYSTEM`.

**Sesiones de Aprendizaje**  
A partir de un intercambio aceptado, se puede agendar una sesión con tema, fecha, duración y cantidad de créditos. Al crearse, los créditos del estudiante quedan retenidos en escrow automáticamente. Ambas partes deben confirmar la sesión para que los créditos se liberen al profesor.

**Sistema de Créditos con Escrow**  
El sistema de créditos garantiza que las transacciones sean seguras: al programar una sesión los créditos se retienen (`ESCROW_HOLD`), se liberan al completarse (`ESCROW_RELEASE`) o se devuelven si se cancela (`ESCROW_REFUND`). Todas las operaciones quedan registradas en la tabla `credit_transactions`.

### Tecnologías Utilizadas

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3 |
| Seguridad | Spring Security + JWT (JJWT) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de Datos | PostgreSQL |
| Reactividad | Spring WebFlux (WebClient) |
| IA Externa | Google Gemini 1.5 Flash API |
| Build Tool | Maven |
| Utilidades | Lombok |
| Testing | JUnit 5, Spring Boot Test |

---

## Modelo de Entidades

```
┌─────────────┐       ┌──────────────────┐       ┌──────────────────┐
│    users    │1─────n│     skills       │       │ exchange_requests │
│─────────────│       │──────────────────│       │──────────────────│
│ id (PK)     │       │ id (PK)          │       │ id (PK)          │
│ email       │       │ user_id (FK)     │  ┌──n─│ requester_id(FK) │
│ password    │       │ name             │  │    │ receiver_id (FK) │
│ fullName    │       │ description      │  │    │ status           │
│ university  │       │ category         │  │    │ message          │
│ career      │       │ skill_type       │  │    │ created_at       │
│ credits     │1──────┘ level            │  │    └──────────────────┘
│ role        │       │ created_at       │  │             │1
│ created_at  │       └──────────────────┘  │             │
└─────────────┘                             │    ┌────────▼─────────┐
       │1                                   │    │     messages     │
       │                                    │    │──────────────────│
       └──────────────────────────┐         │    │ id (PK)          │
                                  │         │    │ exchange_id (FK) │
                                  │         │    │ sender_id (FK)   │
                            ┌─────▼──────── ┘    │ content          │
                            │    sessions        │ message_type     │
                            │────────────────    │ created_at       │
                            │ id (PK)            └──────────────────┘
                            │ exchange_id (FK)
                            │ teacher_id (FK)            ┌────────────────────┐
                            │ student_id (FK)            │ credit_transactions│
                            │ topic             1────────│────────────────────│
                            │ scheduled_at      │        │ id (PK)            │
                            │ duration_min      │        │ from_user_id (FK)  │
                            │ credits_amount    │        │ to_user_id (FK)    │
                            │ status            │        │ session_id (FK)    │
                            │ teacher_confirmed │        │ amount             │
                            │ student_confirmed │        │ transaction_type   │
                            │ created_at        │        │ description        │
                            └───────────────────┘        │ created_at         │
                                                         └────────────────────┘
```

### Descripción de Entidades

- **User:** Entidad central del sistema. Almacena credenciales, datos académicos y balance de créditos. Todo el sistema orbita alrededor del usuario.
- **Skill:** Representa una habilidad que un usuario ofrece o desea aprender. Tiene un tipo (`OFFER`/`WANT`) y está asociada directamente al usuario propietario.
- **ExchangeRequest:** Modela la solicitud formal de intercambio entre dos usuarios. Pasa por estados `PENDING → ACCEPTED/REJECTED`, y es el prerequisito para crear sesiones y mensajes.
- **Session:** Representa una sesión de aprendizaje concreta agendada entre dos usuarios. Tiene su propio ciclo de vida (`SCHEDULED → COMPLETED/CANCELLED`) y gestiona los créditos mediante confirmaciones mutuas.
- **Message:** Mensajes enviados dentro del contexto de un intercambio aceptado. Soporta múltiples tipos para diferentes usos (texto libre, tarjetas de sesión, mensajes del sistema).
- **CreditTransaction:** Registro inmutable de cada movimiento de créditos en el sistema, funcionando como libro contable de todas las operaciones financieras internas.

---

## Testing y Manejo de Errores

### Niveles de Testing Realizados

- **Pruebas de integración con Postman:** Se probaron manualmente todos los endpoints del sistema siguiendo el flujo completo: registro → login → publicación de habilidades → solicitud de intercambio → aceptación → creación de sesión → confirmación → liberación de créditos.
- **Pruebas de seguridad:** Se verificó que los endpoints protegidos retornan `403 Forbidden` al ser accedidos sin token, y `401 Unauthorized` con token inválido o expirado.
- **Pruebas de reglas de negocio:** Se validaron escenarios de error como saldo insuficiente de créditos, intercambios no aceptados como prerequisito de sesión, y autorización incorrecta para aceptar/rechazar solicitudes ajenas.

### Resultados

Las pruebas manuales permitieron identificar y corregir: el orden incorrecto de operaciones en la creación de sesiones (escrow debe ejecutarse después de persistir la sesión para tener un ID válido), y la necesidad de verificar la existencia del ESCROW_HOLD antes de ejecutar un reembolso, evitando duplicación de créditos.

### Manejo de Errores

El proyecto utiliza excepciones de tipo `RuntimeException` lanzadas desde la capa de servicio cuando no se cumplen las precondiciones del negocio (usuario no encontrado, credenciales inválidas, saldo insuficiente, acceso no autorizado). En un entorno de producción, esto debe complementarse con un `@ControllerAdvice` global que capture estas excepciones y las traduzca en respuestas HTTP estructuradas con códigos de estado apropiados (`404`, `400`, `403`), evitando que los stack traces lleguen al cliente y expongan detalles internos de la aplicación.

---

## Medidas de Seguridad Implementadas

### Seguridad de Datos

- **Cifrado de contraseñas:** Las contraseñas se almacenan utilizando `BCryptPasswordEncoder`, que aplica hashing con salt automático. Nunca se almacena la contraseña en texto plano.
- **Autenticación sin estado con JWT:** Cada solicitud autenticada debe incluir un token JWT firmado con HMAC-SHA en el encabezado `Authorization: Bearer`. El servidor no mantiene sesiones, lo que elimina vulnerabilidades asociadas a la gestión de sesiones del lado servidor.
- **Propagación del usuario autenticado:** El email del usuario se extrae del token y se propaga mediante `@AuthenticationPrincipal`, evitando que el cliente pueda falsificar su identidad enviando un ID diferente en el cuerpo de la solicitud.

### Prevención de Vulnerabilidades

- **CSRF deshabilitado intencionalmente:** Al usar JWT con sesiones stateless, el vector de ataque CSRF no aplica ya que no se usan cookies de sesión.
- **Protección contra inyección SQL:** Al usar Spring Data JPA con Hibernate, todas las consultas son parametrizadas por defecto, eliminando la posibilidad de inyección SQL directa.
- **Autorización a nivel de endpoint:** Spring Security bloquea cualquier ruta no incluida en la lista blanca (`/api/auth/**`) si no se presenta un token válido, rechazando las solicitudes antes de llegar a la capa de negocio.
- **Validación de entrada:** Los DTOs de entrada utilizan anotaciones de Jakarta Validation (`@NotBlank`, `@Email`, `@Size`, `@Future`, `@Min`) para rechazar datos malformados en el punto de entrada de la API.

---

## Eventos y Asincronía

### Eventos Implementados

**`SessionCompletedEvent`** es un evento de dominio publicado por `CreditService` mediante el `ApplicationEventPublisher` de Spring inmediatamente después de que ambas partes confirman una sesión y los créditos son liberados al profesor. Este evento es capturado por `NotificationEventListener`.

### Importancia en el Proyecto

La separación mediante eventos permite que el módulo de créditos (`CreditService`) no tenga dependencia directa del módulo de notificaciones. Esto respeta el principio de responsabilidad única y desacopla los módulos: `CreditService` no necesita saber qué ocurre después de completar una sesión, solo publica el hecho. Esto facilita la extensibilidad: en el futuro se pueden agregar más listeners (por ejemplo, para analytics o badges) sin modificar el código existente de créditos.

### Por qué deben ser asincrónicos

La anotación `@Async("taskExecutor")` en `NotificationEventListener` es esencial porque el envío de notificaciones externas (correos transaccionales vía Resend, push notifications, etc.) implica llamadas de red cuya latencia es impredecible. Si estas operaciones se ejecutaran de forma síncrona dentro de la transacción principal, un fallo o demora en el servicio de correo retrasaría o incluso revertiría la transacción de créditos, causando una mala experiencia al usuario. El uso de `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` garantiza además que el evento solo se procesa si la transacción de base de datos se completó exitosamente, evitando notificaciones por sesiones que finalmente no se persistieron.

De forma similar, `GeminiService.analyzeSkillAsync()` se ejecuta en el thread pool `taskExecutor` para que el endpoint de creación de habilidades responda de inmediato al usuario sin esperar la respuesta de la API externa de Google, cuyo tiempo de respuesta puede superar los 10 segundos.

---

## GitHub & Management

### Gestión de Tareas

El equipo utilizó **GitHub Projects** con un tablero Kanban estructurado en columnas: `Backlog`, `In Progress`, `In Review` y `Done`. Cada funcionalidad del sistema fue desglosada en issues individuales (por ejemplo: "Implementar JwtService", "Crear CreditService con escrow", "Integrar Gemini API"), asignados a un integrante responsable con una fecha límite estimada. El uso de labels (`feature`, `bug`, `security`, `testing`) facilitó la clasificación y priorización del trabajo.

### GitHub Actions

Se configuró un flujo de CI con **GitHub Actions** que se activa en cada `push` a las ramas `main` y `develop`, y en cada Pull Request hacia `main`. El pipeline ejecuta los siguientes pasos:

1. **Checkout del código** con `actions/checkout`.
2. **Configuración de Java 21** con `actions/setup-java`.
3. **Compilación y ejecución de pruebas** con `./mvnw test`.
4. **Validación del build** con `./mvnw package -DskipTests` para verificar que el artefacto `.jar` se genera correctamente.

Este flujo garantiza que ningún código que rompa la compilación o las pruebas pueda integrarse a la rama principal, manteniendo la estabilidad del proyecto en todo momento.

---

## Conclusión

### Logros del Proyecto

OnlySwapX logró implementar un backend funcional y completo para una plataforma de intercambio de habilidades entre estudiantes. El sistema cubre el ciclo de vida completo: desde el registro de usuarios hasta la liquidación de créditos tras una sesión completada, pasando por la gestión de solicitudes de intercambio, mensajería y sesiones agendadas. La integración con la API de Gemini añade una capa de inteligencia que enriquece el valor de los datos registrados.

### Aprendizajes Clave

- La importancia del diseño de arquitectura modular desde el inicio: la separación por paquetes por dominio (`auth`, `user`, `skill`, `exchange`, `session`, `credit`, `message`) facilitó el trabajo paralelo entre integrantes.
- El manejo de transacciones distribuidas en JPA requiere planificación cuidadosa, especialmente cuando múltiples entidades se modifican dentro de una misma operación de negocio.
- Los eventos de Spring y la asincronía con `@Async` son herramientas poderosas para desacoplar la lógica de negocio de las operaciones secundarias.

### Trabajo Futuro

- Implementar un `@ControllerAdvice` con manejo centralizado de excepciones y respuestas de error estandarizadas.
- Completar la integración con Resend para envío real de correos transaccionales al completar sesiones.
- Agregar un sistema de valoraciones (ratings) que los usuarios puedan dejar tras cada sesión completada.
- Implementar WebSockets para mensajería en tiempo real dentro de los intercambios activos.
- Añadir un motor de matching que sugiera automáticamente intercambios compatibles basándose en los perfiles semánticos generados por Gemini.

---

## Apéndices


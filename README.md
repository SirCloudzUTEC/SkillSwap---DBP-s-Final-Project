# OnlySwapX ⚡


> **Enseña lo que sabes. Aprende lo que necesitas. Sin gastar un sol.**

OnlySwapX es una plataforma de intercambio de conocimientos entre estudiantes universitarios. En lugar de pagar tutorías caras o quedarte con la duda, conectas con otro estudiante que sabe lo que tú necesitas — y le enseñas algo a cambio.

---

## ¿Por qué existe esto?

Cada facultad tiene estudiantes que dominan algo que los de otra carrera necesitan desesperadamente antes de un examen. El problema no es la falta de conocimiento — es que no existe una forma confiable de encontrar a esa persona, verificar que realmente sabe, y hacer el intercambio sin que nadie salga perdiendo.

OnlySwapX resuelve exactamente eso.

---

## Cómo funciona

```
1. Te registras con tu email universitario
      ↓
2. Marcas qué puedes enseñar y qué quieres aprender
      ↓
3. Subes tu historial académico — la IA lo lee y te hace un quiz de verificación
      ↓
4. El sistema busca tu match automáticamente en background
      ↓
5. Te llega una notificación: "Ana García enseña guitarra y quiere aprender Python"
      ↓
6. Coordinan por chat, acuerdan fecha y créditos — los créditos quedan en escrow
      ↓
7. Se da la sesión por Zoom
      ↓
8. Ambos confirman → créditos se transfieren solos → se califican mutuamente
      ↓
9. Tu rating sube y tu posición en el scoreboard mejora
```

---

## Features principales

| Feature | Descripción |
|---|---|
| 🔐 Registro académico | Autenticación restringida a dominios `.edu.pe` |
| 🧠 Verificación por IA | Extracción de historial académico + quiz generado automáticamente |
| 🤝 Matching inteligente | Embeddings semánticos con `pgvector` para encontrar la compatibilidad exacta |
| 💬 Chat con tarjetas formales | No solo texto libre — propuestas estructuradas con tema, fecha y costo en créditos |
| 🔒 Sistema de escrow | Los créditos se reservan al aceptar la sesión y se liberan solos al confirmar |
| ⭐ Ratings bidireccionales | Ambas partes se califican — afecta visibilidad y posición en el feed |
| 🏆 Scoreboard | Ranking en tiempo real basado en sesiones completadas y rating promedio |
| 🔔 Notificaciones | Matches, mensajes y confirmaciones vía app y correo (Resend API) |

---

## Stack tecnológico

### Backend
```
Java 17 + Spring Boot 3
├── Spring Security + JWT
├── Spring Data JPA + Hibernate
└── Spring Events (@Async) — para operaciones lentas
```

### Servicios externos
```
Google Gemini API → Extracción de datos del historial académico + generación de quiz
pgvector          → Búsqueda semántica de similitud para el motor de matching
Resend API        → Correos transaccionales (matches, confirmaciones, alertas)
```

---

## Arquitectura del backend

```
onlyswapx-backend/
└── src/main/java/com/onlyswapx/
    ├── config/          # Seguridad, async, clientes externos
    ├── domain/          # Entidades JPA
    ├── repository/      # Acceso a datos (JPA + queries nativas pgvector)
    ├── service/
    │   ├── auth/            # Registro, login, JWT
    │   ├── verificacion/    # Procesamiento PDF + quiz con IA
    │   ├── matching/        # Embeddings + búsqueda de similitud
    │   ├── intercambio/     # Servicios, mensajes, tarjetas de sesión
    │   ├── creditos/        # Wallet, escrow, transacciones
    │   ├── rating/          # Calificaciones post-sesión
    │   └── scoreboard/      # Ranking y puntuación
    ├── controller/      # Endpoints REST
    ├── dto/             # Request / Response objects
    ├── event/           # Eventos internos entre servicios
    └── listener/        # Handlers asíncronos de eventos
```

### Flujo de eventos (el corazón del sistema)

Las operaciones lentas no bloquean al usuario. Se procesan en background mediante eventos internos:

```
Perfil guardado
    → @Async → EmbeddingService.generarEmbedding()
    → @Async → MatchingService.buscarMatches()
    → @Async → NotificacionService.notificarNuevoMatch()

Ambos confirman sesión
    → @Async → TransaccionService.liberarEscrow()
    → @Async → NotificacionService.notificarPendienteRating()
    → @Async → EmailService.enviarResumen()

Rating creado
    → @Async → ScoreboardService.recalcularPuntos()
```

---

## Modelo de datos (entidades principales)

```
Usuario          → Wallet (1:1)
Usuario          → Scoreboard (1:1)
Usuario          → Interes (1:N)
Usuario          → ServicioIntercambio (1:N como ofertante y como solicitante)
ServicioIntercambio → Rating (1:N)
ServicioIntercambio → Mensaje (1:N)
Mensaje          → TarjetaSesion (1:1, nullable)
Rating           → Scoreboard (actualiza via evento)
Usuario          → Comunidad via Membresia (N:M)
Comunidad        → Post (1:N)
```

---

## MVP — Lo que entra en la primera versión

- [x] Registro con email universitario y perfil académico
- [x] Verificación de habilidades con IA (PDF + quiz)
- [x] Publicación de habilidades ofrecidas y buscadas
- [x] Matching automático por embeddings semánticos
- [x] Sistema básico de créditos con escrow

**Fuera del MVP (siguiente iteración):**
- [ ] Login con OAuth (Google universitario)
- [ ] Matching en cadena (A → B → C → A)
- [ ] Comunidades y posts
- [ ] App móvil

---

## Endpoints principales

```
POST   /api/auth/register
POST   /api/auth/login

GET    /api/usuarios/{id}
PUT    /api/usuarios/{id}

POST   /api/verificacion/upload
POST   /api/verificacion/quiz

GET    /api/matching/sugerencias

POST   /api/servicios
PUT    /api/servicios/{id}/confirmar

POST   /api/servicios/{id}/mensajes
GET    /api/servicios/{id}/mensajes

POST   /api/tarjetas
PUT    /api/tarjetas/{id}/aceptar

GET    /api/wallet
POST   /api/ratings
GET    /api/scoreboard
```

---

## Operaciones asíncronas

Las siguientes operaciones se procesan en background y **nunca bloquean la respuesta al usuario:**

- Procesamiento del PDF con la API de IA
- Generación de embeddings del perfil
- Búsqueda de similitud en pgvector
- Envío de correos via Resend
- Subida de archivos a Supabase Storage
- Recálculo del scoreboard tras un rating

---

## Equipo

Proyecto desarrollado como parte del curso de Desarrollo Basado en Plataformas 
- **Diego Godoy**
- **Leonardo** 
- **Valentino Gamboa**
- **Pedro Solis**
- **Thiago Frias**

*Universidad de Ingenieria y Tecnologia - UTEC.*

---

*OnlySwapX — Porque el conocimiento vale más cuando se comparte.*
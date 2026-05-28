# Soduko

Juego de Sudoku 6×6 desarrollado en Java con JavaFX como mini proyecto académico para el curso **750014C — Fundamentos de Programación Orientada a Eventos**.

---
## Descripción

Soduko es una variante del clásico Sudoku jugado en una cuadrícula de **6×6** dividida en seis bloques de **2×3**. El objetivo es completar el tablero con números del **1 al 6** de modo que cada fila, columna y bloque contenga todos los números sin repetir.

Cada partida genera un tablero distinto mediante un algoritmo de **backtracking iterativo**. Al iniciar, cada bloque muestra 2 números fijos como pistas. El jugador completa el resto usando teclado o la barra de dígitos inferior.
---

## Arquitectura

El proyecto sigue estrictamente el patrón **MVC (Modelo–Vista–Controlador)**:

```
src/main/java/com/bifasico/soduko/
├── model/          Lógica del juego, árbol de datos, generación de tablero
├── view/           Construcción de nodos JavaFX, animaciones, estilos
└── controller/     Enrutamiento de eventos FXML → handler → modelo → vista
    └── cellInputHandler/   Patrón Interface-Adapter (ICellInputListener, CellInputAdapter, CellInputHandler)
```

**Flujo de comunicación:**

```
FXML Event
    │
    ▼
GameController  ──────────────────►  CellInputHandler  ──►  BoardManager (model)
    │                                                              │
    │  ◄──────────────────  CellInputCallback ◄────────────────────┘
    │
    ▼
GameView  (actualiza nodos JavaFX)
```

El controlador solo enruta eventos. Nunca contiene lógica de juego ni manipula nodos directamente — eso es responsabilidad exclusiva de `GameView`.

---

## Estructuras de datos

El proyecto implementa **dos estructuras de datos distintas a arrays**, una de ellas en la lógica de construcción del tablero:

### 1. Árbol n-ario — lógica del tablero (modelo)

El tablero está representado como un árbol de tres niveles implementado con `ArrayList`:

```
Board (raíz)
├── Region 0  →  filas 0–1, columnas 0–2
├── Region 1  →  filas 0–1, columnas 3–5
├── Region 2  →  filas 2–3, columnas 0–2
├── Region 3  →  filas 2–3, columnas 3–5
├── Region 4  →  filas 4–5, columnas 0–2
└── Region 5  →  filas 4–5, columnas 3–5
    └── Cell × 6  (hojas — cada casilla del tablero)
```

Cada `Cell` almacena su posición absoluta `(row, column)` dentro de la cuadrícula 6×6, lo que elimina conversiones de coordenadas en la validación.

### 2. SudokuStack\<T\> — backtracking del generador

Pila genérica implementada **desde cero** (sin `java.util.Stack`) con array interno de capacidad dinámica (duplica al llenarse). Se usa en `BoardGenerator` para el algoritmo de backtracking iterativo:

- Cada entrada del stack guarda `(posición, valorElegido, candidatosRestantes)`.
- Al retroceder, se recuperan los candidatos no intentados sin recalcular ni reiniciar.
- También se usa en `BoardManager` para el historial de undo del jugador.

---

## Patrones de diseño

### Interface-Adapter (requerimiento académico)

Ubicado en `controller/cellInputHandler/`:

| Componente | Tipo | Rol |
|---|---|---|
| `ICellInputListener` | `interface` | Contrato de todos los eventos de entrada |
| `CellInputAdapter` | `abstract class` | Implementaciones vacías por defecto |
| `CellInputHandler` | `class` | Lógica concreta; solo sobreescribe lo necesario |

`CellInputHandler` también contiene la **clase interna** `CellInputCallback`, que desacopla el handler del controlador.

### Singleton
`SceneManager` gestiona el `Stage` principal y los cambios de escena con una única instancia.

### Facade
`BoardManager` actúa como fachada del modelo: expone una API mínima al controlador y coordina internamente `Board`, `BoardGenerator` y `HintProvider`.

---

## Funcionalidades

| Historia de usuario | Funcionalidad | Estado |
|---|---|---|
| HU-1 | Interfaz gráfica 6×6 con bloques, colores y controles claros | ✅ |
| HU-1 | Tablero dinámico distinto en cada partida | ✅ |
| HU-1 | Celdas fijas (given) no editables, visualmente diferenciadas | ✅ |
| HU-2 | Ingreso por teclado (teclas 1–6) y por barra de dígitos | ✅ |
| HU-2 | Borrar número con Backspace/Delete o botón eraser | ✅ |
| HU-2 | Deshacer última acción (undo) | ✅ |
| HU-3 | Validación en tiempo real: fondo rojo en colisiones | ✅ |
| HU-3 | Ambas celdas en colisión se marcan en rojo simultáneamente | ✅ |
| HU-3 | El rojo persiste hasta resolver la colisión | ✅ |
| HU-4 | Botón de ayuda: revela un número correcto en celda vacía | ✅ |
| HU-4 | Animación morada (fade) al aplicar una pista | ✅ |
| HU-4 | Ayudas ilimitadas | ✅ |
| — | Dígito completado (6 correctos) → botón se desactiva visualmente | ✅ |
| — | Overlay de victoria al completar el tablero | ✅ |
| — | Pantalla de menú integrada en la misma vista | ✅ |

---

## Heurísticas de usabilidad aplicadas

1. **Visibilidad del estado del sistema** — Las celdas en error se colorean en rojo inmediatamente; las correctas muestran el número en blanco; las pistas en morado desvanecido.
2. **Prevención de errores** — Las celdas `given` son no editables (cursor `default`, fondo diferenciado) para impedir modificaciones accidentales.
3. **Reconocimiento antes que recuerdo** — La barra inferior muestra siempre los 6 dígitos disponibles; el seleccionado se resalta en blanco para indicar el modo activo.
4. **Feedback inmediato** — Cada acción (escribir, borrar, pedir pista) produce respuesta visual instantánea sin recargar la vista completa.
5. **Control y libertad del usuario** — El botón de undo permite revertir cualquier jugada; el eraser borra celdas individualmente sin afectar el resto del tablero.
6. **Consistencia y estándares** — Paleta monocromática coherente en toda la aplicación; los estados de celda (error, hint, seleccionada, highlight) siempre usan el mismo color independientemente de la acción que los originó.
7. **Diseño estético y minimalista** — Sin elementos decorativos innecesarios; la información relevante (tablero, dígitos, overlay) ocupa el espacio sin ruido visual.

---

## Estructura del proyecto

```
soduko/
├── src/
│   └── main/
│       ├── java/com/bifasico/soduko/
│       │   ├── Main.java
│       │   ├── model/
│       │   │   ├── Board.java
│       │   │   ├── BoardGenerator.java
│       │   │   ├── BoardManager.java
│       │   │   ├── Cell.java
│       │   │   ├── HintProvider.java
│       │   │   ├── Launcher.java
│       │   │   ├── Region.java
│       │   │   └── SudokuStack.java
│       │   ├── view/
│       │   │   ├── GameView.java
│       │   │   └── SceneManager.java
│       │   ├── controller/
│       │   │   ├── GameController.java
│       │   │   └── cellInputHandler/
│       │   │       ├── ICellInputListener.java
│       │   │       ├── CellInputAdapter.java
│       │   │       └── CellInputHandler.java
│       │   └── module-info.java
│       └── resources/
│           ├── css/
│           │   └── styles.css
│           ├── fonts/
│           │   ├── BDOGrotesk-VF.ttf
│           │   └── SF-Pro.ttf
│           └── fxml/
│               └── game-view.fxml
└── pom.xml
```

---

## Requisitos previos

- **Java 17** — Amazon Corretto 17.0.9 o equivalente
- **Maven 3.8+** — incluido via wrapper (`mvnw`)
- No se requiere instalación adicional de JavaFX; las dependencias se descargan automáticamente via Maven

---

## Cómo ejecutar

```bash
# Clonar el repositorio
git clone https://github.com/<usuario>/soduko.git
cd soduko

# Compilar y ejecutar
./mvnw clean javafx:run          # macOS / Linux
mvnw.cmd clean javafx:run        # Windows
```

---

## Generar Javadoc

```bash
./mvnw javadoc:javadoc
```

La documentación HTML se genera en `target/reports/apidocs/index.html`.

---

## Autores
Yostin Ramirez 2519674 - Joseph Terreros 2521011

Desarrollado por estudiantes del curso **750014C — Fundamentos de Programación Orientada a Eventos**.

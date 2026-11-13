# Sistema de atención de reclamos

**Actividad:** Unidad 3 – Protocolo Individual: Diseño de un sistema de gestión utilizando Java Collections Framework
**Asignatura:** Estructuras de datos – Ingeniería del software – Universidad de Cartagena
**Caso asignado:** 15. Sistema de atención de reclamos

## Descripción del problema

Aplicación de consola que registra clientes y reclamos. Cada reclamo tiene un código único, un tipo,
una descripción, una prioridad (URGENTE, ALTA, NORMAL o BAJA) y un estado (PENDIENTE o ATENDIDO).
El sistema permite buscar reclamos rápidamente por código, evitar tipos de reclamo repetidos,
mostrar los reclamos ordenados y atender primero los reclamos más prioritarios.
No se implementó ninguna estructura de datos a mano: todo se resuelve con Java Collections Framework.

## Versión de Java

Java 17 o superior (desarrollado y probado con Java 21).

## Cómo compilar y ejecutar

Desde la carpeta raíz del proyecto:

```bash
# Linux / macOS
mkdir out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main

# Windows (PowerShell)
mkdir out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out Main
```

Para ejecutar las pruebas automáticas (sin librerías externas):

```bash
java -cp out PruebasSistema
```

Dentro del programa, la opción **13** carga los datos de prueba (6 clientes, 7 tipos y 16 reclamos).

## Colecciones principales

| Necesidad | Interfaz | Implementación |
|---|---|---|
| Registro general de reclamos | `List<Reclamo>` | `ArrayList` |
| Buscar reclamo por código | `Map<String, Reclamo>` | `HashMap` |
| Buscar cliente por documento | `Map<String, Cliente>` | `HashMap` |
| Tipos de reclamo sin duplicados | `Set<String>` | `LinkedHashSet` |
| Reclamos ordenados por código | `SortedSet<Reclamo>` | `TreeSet` (con `Comparable`) |
| Atención por prioridad | `Queue<Reclamo>` | `PriorityQueue` (con `Comparator`) |

## Estructura del proyecto

```
src/
├── main/java/
│   ├── Main.java
│   ├── modelo/         Cliente, Reclamo, Prioridad, Estado
│   ├── servicio/       GestorReclamos, ReclamoException
│   ├── comparadores/   ComparadorPrioridad
│   ├── ui/             MenuConsola
│   └── util/           DatosPrueba
└── test/java/
    └── PruebasSistema.java
```


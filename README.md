# OOPSlicer — Object-Oriented Java Program Slicing

A JavaFX desktop tool for exploring Java source through static program slicing and dependence-graph visualization. OOPSlicer brings project selection, source navigation, slicing criteria, and graph inspection into one interface.

## Purpose

Program slicing helps explain which statements contribute to a point of interest and which statements may be affected by it. This project applies that idea across object-oriented Java code, incorporating class relationships and interactions into its analysis representation.

## Analysis and interface

- Select a Java project, source file, and line-based slicing criterion.
- Compute **forward** and **backward** slicing maps organized by class and source line.
- Analyze class inheritance and superclass/derived-class relationships.
- Connect method calls, object construction, and return interactions across graph nodes.
- Present source and slicing operations through a JavaFX desktop workflow.
- Visualize graph relationships through the included graph-view components.

## Architecture

```text
Project files → Eclipse JDT parsing → Per-class analysis → System dependence graph
                                                                  |
                                                       Forward/backward slices
                                                                  |
                                                   JavaFX source and graph views
```

| Component | Responsibility |
| --- | --- |
| `src/sample/` | JavaFX application, interaction flow, and project selection |
| `src/othersPackage/Operation.java` | Syntax-tree analysis and graph construction |
| `src/othersPackage/SDG.java` | Inter-class relationships and slicing maps |
| `src/othersPackage/Output.java` | Slice output handling |
| `src/visGraphPackage/` | Java/JavaScript graph visualization bridge |
| `resources/` | Application imagery |

`src/sourcePackage/` contains examples with a class hierarchy for studying object-oriented interactions. `sourceFolder/` provides a bundled Java project used in the application workflow.

## Launch

Open the project in a Java IDE and run `sample.Main` from the repository root. Configure a JDK compatible with the supplied JavaFX 19 SDK, mark `src/` as the source root, and include the graph/editor libraries imported by the application together with the Eclipse JDT/runtime JARs in `lib/`. Preserve package resources on the runtime classpath.

The desktop flow supports opening a project, selecting a file and criterion, choosing the slicing operation, and inspecting the resulting source and graph views.

## Technical contribution

OOPSlicer integrates AST-based analysis, inter-class dependence handling, bidirectional slicing, source presentation, and interactive graph visualization into a single desktop analysis experience.

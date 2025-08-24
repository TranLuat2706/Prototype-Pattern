# Prototype Pattern Demo (Maven multi-module)

**Project structure (IntelliJ friendly)**:
- prototype-intellij-modular/ (parent pom)
  - prototype-core/ (core library: Prototype, Document, Resume, Report, Invoice, PrototypeRegistry)
  - prototype-app/ (application: GUI with Swing + console sample)

## Java / Maven
- Java 17
- Parent POM packs two modules. Import the parent `pom.xml` into IntelliJ as a Maven project.

## How to open in IntelliJ
1. File -> Open... -> select the project folder `prototype-intellij-modular` (or the parent pom.xml)
2. IntelliJ will import as a Maven project with two modules.
3. Set Project SDK to Java 17 if prompted.
4. To run GUI app: run `vn.eiu.prototype.PrototypeApp` from `prototype-app` module.
5. To run console demo: run `vn.eiu.prototype.PrototypeDemo` in `prototype-app` module.

## Run from command line (maven)
Build modules:
```bash
mvn -pl prototype-core,prototype-app -am compile package
```
Run GUI app via exec plugin:
```bash
cd prototype-app
mvn compile exec:java -Dexec.mainClass="vn.eiu.prototype.PrototypeApp"
```


Generated on: 2025-08-22T07:57:28.211427 UTC

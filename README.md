# JavaToUMLParser

[![Stories in 'In Progress' board](https://badge.waffle.io/gurnoors/JavaToUMLParser.svg?label=in%20progress&title=In%20Progress)](http://waffle.io/gurnoors/JavaToUMLParser)

Takes in Java Source code to generate UML Class Diagrams

## Libraries Used:
- [JavaParser](http://javaparser.org): Parses the spurce code to an abstract syntax tree. 
- [PlantUML](http://plantuml.com/class-diagram): Generates UML diagrams from text containing class relationship information.


## How to Run:
```java -jar umlparser.jar src/folder/path optional/output/path.png```
(You can find umlparser.jar in project directory or generate it again, with Main class being ```com.gurnoors.cmpe202.uml.classdia.Main``` )


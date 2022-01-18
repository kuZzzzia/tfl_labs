# tfl_lab_4

___

## Запуск проекта
Для сборки проекта должен быть установлен Maven и Java не ниже 8 версии

Перед сборкой проекта, если требуются дополнительные файлы, на которых он будет запускаться, нужно добавить их в директорию resources: `./src/main/resources`

После этого собираем проект:  
`mvn package`

Пример запуска:  
`java -jar ./target/tfl_lab_4-1.0-SNAPSHOT.jar test_1.txt`

## Структура проекта

```
.
├── pom.xml
├── README.md
└── src
    └── main
        ├── java
        │   └── bmstu
        │       └── iu9
        │           └── tfl
        │               ├── lab2
        │               │   ├── Equation.java
        │               │   └── Solver.java
        │               └── lab4
        │                   ├── AutolexApp.java
        │                   ├── Grammar.java
        │                   ├── Reader.java
        │                   └── Tokenizer.java
        └── resources
            ├── test_1.txt
            ├── test_2.txt
            └── test_3.txt


```
# tfl_lab_2
___

## Запуск проекта
Для сборки проекта должен быть установлен Maven и Java не ниже 8 версии

Перед сборкой проекта, если требуются дополнительные файлы, на которых он будет запускаться, нужно добавить их в соответствующую директорию resources:
* для системы регулярных выражений в ./system_of_regex_equations/src/main/resources
* для преобразования грамматики в регулярное выражение в ./gr_to_regex_conv/src/main/resources

После этого:
`mvn package`

Также существует возможность генерации новых тестов для модуля equiv_regexes:
`java -jar ./tests_generator/target/tests_generator-1.0-SNAPSHOT.jar ./equiv_regexes/src/main/resources`
После генерации тестов нужно обязательно пересобрать проект с помощью `mvn package`. Стоит отметить, что тесты сгенерированы заранее и уже лежат в директории `resources` модуля `equiv_regexes`

Пример запуска сравнения 3 эквивалентных регулярных выражений:
`java -jar ./equiv_regexes/target/equiv_regexes-1.0-SNAPSHOT.jar output`
Тогда по относительному пути создастся файл с `csv` расширением, в котором будет находиться таблица с временем распознавания тестов

Пример запуска решения системы регулярных выражений:
`java -jar ./system_of_regex_equations/target/system_of_regex_equations-1.0-SNAPSHOT.jar test_1.txt`

Пример запуска преобразования регулярной грамматики в регулярное выражение:
`java -jar ../gr_to_regex_conv/target/gr_to_regex_conv-1.0-SNAPSHOT.jar test_1.txt`


## Структура проекта
```
├── equiv_regexes
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── bmstu
│           │       └── iu9
│           │           └── tfl
│           │               └── lab2
│           │                   └── equalRegex
│           │                       ├── MeasureRegexPerformanceApp.java
│           │                       └── TestReader.java
│           └── resources
│               ├── test_0.txt
│               ├── test_1.txt
│               ├── test_2.txt
│               ├── test_3.txt
│               ├── test_4.txt
│               ├── test_5.txt
│               ├── test_6.txt
│               ├── test_7.txt
│               ├── test_8.txt
│               └── test_9.txt
├── gr_to_regex_conv
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── bmstu
│           │       └── iu9
│           │           └── tfl
│           │               └── lab2
│           │                   └── grammarToRegexConversion
│           │                       ├── Converter.java
│           │                       ├── FiniteAutomata.java
│           │                       ├── GrammarParser.java
│           │                       └── Rule.java
│           └── resources
│               ├── test_1.txt
│               ├── test_2.txt
│               ├── test_3.txt
│               ├── test_4.txt
│               └── test_5.txt
├── parser
│   ├── pom.xml
│   └── src
│       └── main
│           └── java
│               └── bmstu
│                   └── iu9
│                       └── tfl
│                           └── lab2
│                               └── Parser.java
├── pom.xml
├── reader
│   ├── pom.xml
│   └── src
│       └── main
│           └── java
│               └── bmstu
│                   └── iu9
│                       └── tfl
│                           └── lab2
│                               └── Reader.java
├── README.md
├── system_of_regex_equations
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── bmstu
│           │       └── iu9
│           │           └── tfl
│           │               └── lab2
│           │                   └── systemOfRegularExpressionEquations
│           │                       ├── Equation.java
│           │                       ├── RegexEquationParser.java
│           │                       └── Solver.java
│           └── resources
│               ├── test_1.txt
│               ├── test_2.txt
│               ├── test_3.txt
│               ├── test_4.txt
│               └── test_5.txt
└── tests_generator
    ├── pom.xml
    └── src
        └── main
            └── java
                └── bmstu
                    └── iu9
                        └── tfl
                            └── lab2
                                └── RegexTestsGenerator.java

```
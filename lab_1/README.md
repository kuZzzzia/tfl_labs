# tfl_lab_1
___
## Структура проекта
```
.
├── pom.xml
├── reader
│    ├── pom.xml
│    └── src
│        └── main
│            └── java
│                └── bmstu
│                    └── tfl
│                        └── lab1
│                            └── Reader.java
├── README.md
├── srs
│    ├── pom.xml
│    └── src
│        └── main
│            ├── java
│            │   └── bmstu
│            │       └── tfl
│            │           └── lab1
│            │               └── srs
│            │                   ├── Confluence.java
│            │                   └── SRSReader.java
│            └── resources
│                ├── test1.txt
│                ├── test2.txt
│                ├── test3.txt
│                ├── test4.txt
│                ├── test5.txt
│                ├── test6.txt
│                ├── test7.txt
│                └── test8.txt
└── trs
    ├── pom.xml
    └── src
        └── main
            ├── java
            │    └── bmstu
            │        └── tfl
            │            └── lab1
            │                └── trs
            │                    ├── Constructor.java
            │                    ├── Term.java
            │                    ├── TRSReader.java
            │                    └── Unification.java
            └── resources
                ├── test1.txt
                ├── test2.txt
                ├── test3.txt
                ├── test4.txt
                ├── test5.txt
                └── test6.txt

```

## Запуск проекта
Для сборки проекта должен быть установлен Maven и Java не ниже 8 версии

Перед сборкой проекта, если требуются дополнительные файлы, на которых он будет запускаться, нужно добавить их в соответствующую директорию resources:
* для TRS в ./trs/src/main/resources
* для SRS в ./srs/src/main/resources

После этого:
`mvn package`

Пример запуска TRS:
`java -jar ./trs/target/trs-1.0-SNAPSHOT.jar test1.txt`

Пример запуска SRS:
`java -jar ./srs/target/srs-1.0-SNAPSHOT.jar test1.txt`
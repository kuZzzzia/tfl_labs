# tfl_lab_3

___

## Запуск проекта
Для сборки проекта должен быть установлен Maven и Java не ниже 8 версии

Перед сборкой проекта, если требуются дополнительные файлы, на которых он будет запускаться, нужно добавить их в директорию resources: `./src/main/resources`

После этого собираем проект:  
`mvn package`

Пример запуска проверки КС-грамматики на регулярность:  
`java -jar ./target/tfl_lab_3-1.0.jar test_1.txt`  

По относительному пути создастся директория `derives`, в которой будут находиться графы (версия на языке `dot` и сам граф в `svg` формате), накачиваемых нетерминалов, достижимых из стартового.

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
        │               └── lab3
        │                   ├── CheckCFGForRegularityApp.java
        │                   ├── Grammar.java
        │                   ├── NontermLeftmostDerivationTree.java
        │                   ├── Parser.java
        │                   ├── Reader.java
        │                   ├── RuleRightSide.java
        │                   └── TreeNode.java
        └── resources
            ├── test_1.txt
            ├── test_2.txt
            ├── test_3.txt
            ├── test_4.txt
            └── test_5.txt

```
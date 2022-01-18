# tfl_lab_5
___
##Задание
Переформатирование РБНФ

## Запуск проекта
Для сборки проекта должен быть установлен Maven и Java не ниже 8 версии

Перед сборкой проекта, если требуются дополнительные файлы, на которых он будет запускаться, нужно добавить их в директорию resources: `./src/main/resources`

После этого собираем проект:  
`mvn package`

Пример запуска проверки КС-грамматики на регулярность:  
`java -jar ./target/tfl_lab_5-1.0-SNAPSHOT.jar  test_1/current_syntax.txt test_1/my_syntax.txt test_1/grammar.txt`

C помощью [лабораторной работы №4](https://github.com/kuZzzzia/tfl_lab_4) был проведен предварительный анализ лексем уточненной грамматики

Для построения деревьев вывода использовался алгоритм CYK

Нетерминалы и константы в конечном результате выделены цветом

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
        │               ├── lab4
        │               │   └── Tokenizer.java
        │               └── lab5
        │                   ├── MetaGrammar.java
        │                   ├── Reader.java
        │                   ├── ReformatEBNFApp.java
        │                   ├── TreeBuilder.java
        │                   └── TreeNode.java
        └── resources
            ├── test_1
            │   ├── current_syntax.txt
            │   ├── grammar.txt
            │   └── my_syntax.txt
            └── test_2
                ├── current_syntax.txt
                ├── grammar.txt
                └── my_syntax.txt
```

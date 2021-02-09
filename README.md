# MASTER-HIBERNATE-JPA
Hibernate, JPA and Spring Data JPA using Spring and Spring Boot

Obs: Projeto configurado com banco em mémoria por facilidade. Não tem que subir ambiente. 

### AUTO-CONFIGURATION REPORT
o uso da propriedade abaixo permite ver os logs a nível de debug e ter uma melhor idea do que está acontecendo no background da aplicação, incluindo todas informações do auto-configurtion do spring. 
```shell
logging.level.root=debug
```

### PROPRIEDADES INTERESSANTES
application.properties
```shell
# Gera estatísticas JPA/SQL com nível de informação mais detalhado (debug)
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate.stat=debug

#Exibi todas queries no console formatadas e exibe os seus parametros
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.type=trace
```

### PADRAO FORMATACAO
Utilizado padrao de estilo de formatação com o plugin fmt-maven-plugin. 
```shell
mvn com.coveo:fmt-maven-plugin:format
```

### QUERIES (REPOSITORIES)
Utilizado queries do tipo:
- Métodos Entity Manager
- JPQL
- Native Queries
- Named Queries

### RELACIONAMENTOS USADOS (ENTITIES)
Utilizados os relacionamentos abaixo, sempre com o método de busca LAZY. Apesar de mais trabalhoso, é melhor por consumo de recursos e performance. Também utilizado sempre sem o `cascade type`, o que torna os relacionamentos `transiente`. 
- @OneToOne   (Default Fetch: EAGER) 
- @ManyToOne  (Default Fetch: EAGER)
- @OneToMany  (Default Fetch: LAZY)
- @ManyYoMany (Default Fetch: LAZY)

#### PROBLEMA N+1 (REPOSITORIES)
Há um tipo de problema bastante conhecido como `Problema N+1`. Esse problema ocorre quando um *objeto A* possui um relacionamento com um *objeto B* do tipo `LAZY`. Ao instanciar o Objeto A, o objeto B não será carregado, porém uma vez que o usuário precisa daqueles dados ele pode fazer um loop para carregar todos os objetos B de uma lista, por exemplo. Aconte que será feito um select para retornar o objeto A e N selects para carregar cada um dos objetos da lista do objeto B. Isso pode resultar em problemas de performance. Neste caso o ideal é carregar a instância de A já com os objetos necessários de B. Há duas abordagens que podem ser utilizadas para isso:
- Entity Graph
- Join Fetch
- Hibernate.Initialize  
  está solucão recupera os dados através do getter. Aparentemente resolve o problema, já que ocorre um select para carregar a instância e outro para o getter para carregar o objeto lazy. No entanto, seria necessário fazer mais testes. Verificar: `StudentRepository.findCourseRetrieveReviews_XXX()`.

### HERANÇA (ENTITIES)
Utilizado classe Employee para testar algumas formas de herança e diferentes formas de persistência.
- Singles Table      (Possivelmente melhor opção)
- Joined             (Possivelmente melhor opção)
- Table per Class
- Mapped Super Class

### ISOLATION LEVEL (TRANSACTION)
Geralmente o método utilizado é o `READ COMMITED`, devido a performance. `Serializeble` resolve todo tipos de problemas como dirty read, non repeatable read e phanton read, porém pode causar problemas de performance. 
- Read Uncommited
- Read Commited   
- Repeatable Read 
- Serializeble

### SOFT DELETE (ENTITIES)
Utilizado algumas propriedades do hibernate para fazer uma implementação de deleção a qual o registro não é removido do banco de dados e, sim, atualizado seu status para deletado = true. Após implementação para conseguir retornar qualquer objeto marcado como deletado é necessário usar native queries, já que o hibernate irá sobrepor JPQL com as propriedades adicionadas. 
- @SqlDelete
- @Where
- @PreRemove

### LOMBOK BUILDER (ENTITIES)
O Lombok ajuda bastante agilizar a contrução dos builders, por isso foi utilizado para criar os factories dos testes. 
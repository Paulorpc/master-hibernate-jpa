# master-hibernate-jpa
Hibernate, JPA and Spring Data JPA using Spring and Spring Boot


Github curse project:
- https://github.com/in28minutes/jpa-with-hibernate

### AUTO-CONFIGURATION REPORT
o uso da propriedade abaixo permite ver os logs a nível de debug e ter uma melhor idea do que está acontecendo no background da aplicação, incluindo todas informações do auto-configurtion do spring. 
```shell
logging.level.root=debug
```


### PROPRIEDADES E DESCRIÇÕES
application.properties
```shell
# Configurando banco H2 e habilitando console web
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.data.jpa.repositories.bootstrap-mode=default

# nível de informação exibido de logs da apliação
#logging.level.root=info

# Gera estatísticas JPA/SQL com nível de informação mais detalhado (debug)
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate.stat=debug

#Exibi todas queries no console formatadas e exibe os seus parametros
spring.jpa.show-sql=true
spring.jpa.properties.format_sql=true
logging.level.org.hibernate.type=debug
```

### PADRAO FORMATACAO
Utilizado padrao de estilo de formatação com o plugin fmt-maven-plugin. 
```shell
mvn com.coveo:fmt-maven-plugin:format
```
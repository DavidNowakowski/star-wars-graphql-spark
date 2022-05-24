# Requirements

- We are going to use [postman](https://www.postman.com/) for test our API. You can load collection samples located in the folder `./postman`.

- This tutorial requires a version of Java 11 or higher to be completed. To change compilation version you can edit `maven.compiler` properties in [pom.xml](https://github.com/DavidNowakowski/star-wars-graphql-spark/blob/master/pom.xml)

**Dependencies in our pom.xml**

We are using [graphql-java](https://www.graphql-java.com/) for graphQL

```xml
<dependency>
	<groupId>com.graphql-java</groupId>
	<artifactId>graphql-java</artifactId>
	<version>${graphql.version}</version>
</dependency>
```

We are using spark for build HTTP server

```xml
<dependency>
	<groupId>com.sparkjava</groupId>
	<artifactId>spark-core</artifactId>
	<version>${sparkcore.version}</version>
</dependency>
```

We are using Jackson library for serialization

```xml
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>${jackson.version}</version>
</dependency>
```
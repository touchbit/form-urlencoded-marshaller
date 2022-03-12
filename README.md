# Marshaller for "x-www-form-urlencoded" data

[![Build](https://github.com/touchbit/form-urlencoded-marshaller/actions/workflows/build-deploy.yml/badge.svg?style=plastic)](https://github.com/touchbit/form-urlencoded-marshaller/actions/workflows/build-deploy.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=touchbit_form-urlencoded-marshaller&metric=alert_status&style=plastic)](https://sonarcloud.io/summary/overall?id=touchbit_form-urlencoded-marshaller)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=touchbit_form-urlencoded-marshaller&metric=security_rating&style=plastic)](https://sonarcloud.io/summary/overall?id=touchbit_form-urlencoded-marshaller) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=touchbit_form-urlencoded-marshaller&metric=reliability_rating&style=plastic)](https://sonarcloud.io/summary/overall?id=touchbit_form-urlencoded-marshaller) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=touchbit_form-urlencoded-marshaller&metric=sqale_rating&style=plastic)](https://sonarcloud.io/summary/overall?id=touchbit_form-urlencoded-marshaller) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=touchbit_form-urlencoded-marshaller&metric=coverage&style=plastic)](https://sonarcloud.io/summary/overall?id=touchbit_form-urlencoded-marshaller) [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=touchbit_form-urlencoded-marshaller&metric=ncloc&style=plastic)](https://sonarcloud.io/summary/overall?id=touchbit_form-urlencoded-marshaller)

---

![](https://img.shields.io/badge/Java-8%2B-blue?style=plastic&logo=java) [![](https://maven-badges.herokuapp.com/maven-central/org.touchbit.web/form-urlencoded-marshaller/badge.svg?style=plastic)](https://mvnrepository.com/artifact/org.touchbit.web)

```xml
<dependency>
    <groupId>org.touchbit.web</groupId>
    <artifactId>form-urlencoded-marshaller</artifactId>
    <version>1.0.0</version>
</dependency>
```
```text
org.touchbit.web:form-urlencoded-marshaller:jar:1.0.0
+- org.apache.commons:commons-lang3:jar:3.12.0:compile
```

---

## Features

* Marshal POJO/`Map<String, Object>` to URL form data string.
* Unmarshal URL form data string to POJO/`Map<String, Object>`.
* Support for nested POJO/Map `foo[bar]=100` <-> `{foo={bar=100}}`.
* Support for hidden arrays `foo=100&foo=200...&foo=100500`.
* Support for implicit arrays `foo[]=100&foo[]=200...&foo[]=100500`.
* Support for explicit arrays `foo[0]=100&foo[1]=200...&foo[n]=100500`.
* Converting string values to POJO field types.
* Rules for handling null values (ignore, null string, empty string, null marker).
* AdditionalProperties field for extra form data parameters (like Jackson2).

## Settings

**(D)** - default
```text
FormUrlMarshaller.INSTANCE
  .enableHiddenList() - foo=100&foo=200 (D)
  .enableImplicitList() - foo[]=100&foo[]=200
  .enableExplicitList() - foo[0]=100&foo[1]=200
  .setNullValueRule(RULE_IGNORE) - Ignore null value parameters (D)
  .setNullValueRule(RULE_NULL_MARKER) - /api/call?foo=%00
  .setNullValueRule(RULE_EMPTY_STRING) - /api/call?foo=
  .setNullValueRule(RULE_NULL_STRING) - /api/call?foo=null
  .prohibitAdditionalProperties(true) - error if extra fields received (D false)
  .setFormUrlCodingCharset(UTF_16); - value encoding (D utf-8)
```

## Usage

### Simple POJO (flat data)

```java
@lombok.Data
@FormUrlEncoded
public class Pagination {

    @FormUrlEncodedField("limit")
    private Integer limit;

    @FormUrlEncodedField("offset")
    private Integer offset;

}
```

**Usage**

```java
public class Example {

  public static void main(String[] args) {
    final FormUrlMarshaller marshaller = FormUrlMarshaller.INSTANCE;
    final Pagination pagination = new Pagination().limit(50).offset(10);
    
    final String form = marshaller.marshal(pagination);
    System.out.println("UrlEncoded form: " + form);
    
    final Pagination pojo = marshaller.unmarshal(Pagination.class, form);
    System.out.println("Pagination POJO: " + pojo);
  }
}
```

**Output**

```text
UrlEncoded form: offset=10&limit=50
Pagination POJO: {offset=10, limit=50}
```

### Complex POJO (nested objects)

```java
@lombok.Data
@FormUrlEncoded
public static class QueryParam {

    @FormUrlEncodedField("sorting")
    private String sorting;

    @FormUrlEncodedField("exclude")
    private String exclude;

    // POJO from the previous example
    @FormUrlEncodedField("paginate")
    private Pagination paginate;

}
```

**Usage**

```java
public class Example {

  public static void main(String[] args) {
    FormUrlMarshaller marshaller = FormUrlMarshaller.INSTANCE;
    Pagination paginate = new Pagination().limit(50).offset(10);
    QueryParam query = new QueryParam()
            .exclude("<,>").sorting("DESC").paginate(paginate);
    
    final String form = marshaller.marshal(query);
    System.out.println("UrlEncoded form: " + form);
    
    final QueryParam pojo = marshaller.unmarshal(QueryParam.class, form);
    System.out.println("QueryParam POJO: " + pojo);
  }
}
```

**Output**

```text
UrlEncoded form: paginate[offset]=10&paginate[limit]=50&sorting=DESC&exclude=%3C%2C%3E
QueryParam POJO: {exclude=<,>, sorting=DESC, paginate={offset=10, limit=50}}
```


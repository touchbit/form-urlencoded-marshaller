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

## TOC

- [Features](#features)
- [General information](#general-information)
  - [Settings](#settings)
- [Usage](#usage)
  - [Simple POJO (flat data)](#simple-pojo-flat-data)
  - [Complex POJO (nested objects)](#complex-pojo-nested-objects)
  - [Additional properties](#additional-properties)
- [Error handling](#error-handling)
- [Benchmarks](#benchmarks)
  - [Brief results](#brief-results)
  - [Detailed results](#detailed-results)
- [Licensing](#licensing)

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

[Back to top](#toc)

## General information

FormUrlMarshaller is not a utility class. You can inherit from it and change the current implementation of internal methods to suit your needs. All internal methods have the `protected` modifier. There are no finalized methods or classes.

Marshaling can be done in three ways:
- `String marshal(Object)` - converts a POJO or Map to a string in `form URL encoded` format.
- `Map<String, List<String>> marshalToMap(Object)` - converts a POJO or Map to a `form URL encoded` Map where key - URL form Key, value - list of encoded values. For example: `{foo=[1, 2], bar=car} <--> {foo=[1, 2], bar=[car]}`. Allows you to implement your own processing of `form URL encoded` lists.
- `IChain marshalToIChain(Object)`  - converts a POJO or Map to `IChain` object. `IChain` - this is a chain of encoded url form parameters. Allows you to implement your own processing of `form URL encoded` string data.

Unmarshaling can be done in two ways:
- `<M> void unmarshalTo(M, String)` - write `form URL encoded` data to a POJO or Map object. 
- `<M> M unmarshal(Class<M>, String)` - write `form URL encoded` data to a POJO or Map (independently creates class instances).

[Back to top](#toc)

### Settings

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

[Back to top](#toc)

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

[Back to top](#toc)

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

[Back to top](#toc)

## Additional properties

Used during unmarshalling to store extra fields that are not present in the POJO model.   
AP field must have the `@FormUrlEncodedAdditionalProperties` annotation.   
AP field type - strictly `Map<String, Object>`.  

```java
@lombok.Data
@FormUrlEncoded
public static class FormData {

  @FormUrlEncodedField("firstName")
  private String firstName;

  @FormUrlEncodedField("lastName")
  private String lastName;

  @FormUrlEncodedAdditionalProperties()
  public Map<String, Object> additionalProperties;

}
```

**Usage**

```java
public class Example {

  public static void main(String[] args) {
    FormUrlMarshaller marshaller = FormUrlMarshaller.INSTANCE;
    final String data =
            "lastName=Pearson&firstName=Michael&nickname=Gentlemen";
    final FormData unmarshal = marshaller.unmarshal(FormData.class, data);
    System.out.println("QueryParam POJO: " + unmarshal);
  }
}
```

**Output**

```text
UrlEncoded form: lastName=Pearson&firstName=Michael&nickname=Gentlemen
QueryParam POJO: {firstName=Michael, lastName=Pearson, additionalProperties={nickname=Gentlemen}}
```

**Prohibition of extra fields**

```java
public class Example {

  public static void main(String[] args) {
    FormUrlMarshaller marshaller = FormUrlMarshaller.INSTANCE
            .prohibitAdditionalProperties(true); // <-------- PROHIBIT
    final String data = "lastName=Pearson&" +
                        "firstName=Michael&" +
                        "nickname=Gentlemen";
    final FormData form = marshaller.unmarshal(FormData.class, data);
    System.out.println("QueryParam POJO: " + form);
  }
}
```

```text
MarshallerException: 
  URL encoded string contains unmapped additional properties.
    Actual: {nickname=Gentlemen}
    Expected: There are no additional properties.
```

[Back to top](#toc)

## Error handling

Marshalling and unmarshaling methods only throw `MarshallerException` (`RuntimeException`).   
Errors during the operation of the marshaller are as detailed as possible. For example, if the FormUrlEncoded string contains an array of objects instead of a single object.

```text
org.touchbit.www.form.urlencoded.marshaller.util.MarshallerException: 
  Incompatible types received for conversion.
    Source: {pagination=[{limit=50, offset=10}]}
    Source field: pagination
    Source value: [{limit=50, offset=10}]
    Source type: java.util.ArrayList
    Target type: qa.model.QueryParams
    Target field: private Pagination pagination;
```

[Back to top](#toc)

## Benchmarks

```text
JMH version: 1.34
VM version: JDK 15.0.4, Zulu OpenJDK 64-Bit Server VM, 15.0.4+5-MTS
Blackhole mode: full + dont-inline hint
Warmup: 5 iterations, 10 s each
Measurement: 5 iterations, 10 s each
Threads: 4 threads, will synchronize iterations
Benchmark mode: Average time, time/op
```

- Each benchmark contains a pre-prepared set of data for marshaling and unmarshaling.
- Marshalling and unmarshaling is checked on data of `[16,32,64...1024]` bytes length.
- As the data size increases, the **number of involved object fields increases**. An example for clarity:
    - 16 byte -> `offset=0&limit=5`
    - 32 byte -> `offset=2&limit=50&sort=ASC&foo=1`
- GC is performed between measurements.
- The results of marshaling and unbarshaling are dumped into the `Blackhole`.
- Used 6 load profiles:
    - FormUrlEncoded String <--> `Map<String, String>`
    - FormUrlEncoded String <--> POJO with fields of type `String`
    - FormUrlEncoded String <--> POJO with fields of type `Integer`
    - FormUrlEncoded String <--> POJO with fields of type `LIst<String>`
    - FormUrlEncoded String <--> POJO with fields of type `LIst<String>`
    - FormUrlEncoded String <--> POJO with nested POJOs fields

### Brief results

![](./.benchmarks/img/MarshalBrief.png?raw=true)
![](./.benchmarks/img/UnmarshalBrief.png?raw=true)

[Back to top](#toc)

### Detailed results

Click image for detailed JMH report

[![](./.benchmarks/img/MapStringString.png?raw=true)](https://jmh.morethan.io/source=https://github.com/touchbit/form-urlencoded-marshaller/.benchmarks/POJO_field_type_String-report.json?raw=true)
[![](./.benchmarks/img/PojoString.png?raw=true)](https://jmh.morethan.io/source=https://github.com/touchbit/form-urlencoded-marshaller/.benchmarks/POJO_field_type_String-report.json?raw=true)
[![](./.benchmarks/img/PojoInteger.png?raw=true)](https://jmh.morethan.io/source=https://github.com/touchbit/form-urlencoded-marshaller/.benchmarks/POJO_field_type_Integer-report.json?raw=true)
[![](./.benchmarks/img/PojoListString.png?raw=true)](https://jmh.morethan.io/source=https://github.com/touchbit/form-urlencoded-marshaller/.benchmarks/POJO_field_type_List_String-report.json?raw=true)
[![](./.benchmarks/img/PojoListInteger.png?raw=true)](https://jmh.morethan.io/source=https://github.com/touchbit/form-urlencoded-marshaller/.benchmarks/POJO_field_type_List_Integer-report.json?raw=true)
[![](./.benchmarks/img/PojoNestedPojo.png?raw=true)](https://jmh.morethan.io/source=https://github.com/touchbit/form-urlencoded-marshaller/.benchmarks/POJO_field_type_nested_POJO-report.json?raw=true)

[Back to top](#toc)

## Licensing

Copyright (c) 2022 Shaburov Oleg.   
Distributed under license 'Apache License Version 2.0'.   
See the [LICENSE](./LICENSE?raw=true) file for license rights and limitations.   

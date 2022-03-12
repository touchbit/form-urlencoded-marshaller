Change Log
==========

## Version 1.0.0

* **New**: marshal POJO/`Map<String, Object>` to URL form data string.
* **New**: unmarshal URL form data string to POJO/`Map<String, Object>`.
* **New**: support for nested POJO/Map `foo[bar]=100` <-> `{foo={bar=100}}`.
* **New**: support for hidden arrays `foo=100&foo=200...&foo=100500`.
* **New**: support for implicit arrays `foo[]=100&foo[]=200...&foo[]=100500`.
* **New**: support for explicit arrays `foo[0]=100&foo[1]=200...&foo[n]=100500`.
* **New**: converting string values to POJO field types.
* **New**: rules for handling null values (ignore, null string, empty string, null marker).
* **New**: AdditionalProperties field for extra form data parameters (like Jackson2).

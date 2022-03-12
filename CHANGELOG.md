Change Log
==========

## Version 1.0.0

* **New**: `FormUrlMarshaller` - marshal/unmarshal POJO.
* **New**: `FormUrlMarshaller` - marshal/unmarshal `Map<String, Object>`.
* **New**: `FormUrlMarshaller` - converting string values to POJO field types.
* **New**: `FormUrlMarshaller` - support for hidden arrays `foo=100&foo=200...&foo=100500`.
* **New**: `FormUrlMarshaller` - support for implicit arrays `foo[]=100&foo[]=200...&foo[]=100500`.
* **New**: `FormUrlMarshaller` - support for explicit arrays `foo[0]=100&foo[1]=200...&foo[n]=100500`.
* **New**: `FormUrlMarshaller` - support nested POJO/Map `foo[bar]=100` -> `{foo={bar=100}}`.
* **New**: `FormUrlMarshaller` - support custom form url coding charset other than UTF-8.

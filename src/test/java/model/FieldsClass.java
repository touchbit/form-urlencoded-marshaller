package model;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FieldsClass {

    private String foo;
    private String bar;

    public static List<Field> getFields() {
        return FieldUtils.getAllFieldsList(FieldsClass.class).stream()
                .filter(f -> !Modifier.isTransient(f.getModifiers()))
                .collect(Collectors.toList());
    }

    public static List<Field> getOneFieldList() {
        return Collections.singletonList(getFields().get(0));
    }

    public static Field foo() {
        return FieldUtils.getField(FieldsClass.class, "foo", true);
    }

}

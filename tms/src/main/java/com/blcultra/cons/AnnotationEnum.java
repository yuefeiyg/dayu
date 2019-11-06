package com.blcultra.cons;

public enum AnnotationEnum {
    annotationobject("文本","012001");

    private final String key;
    private final String value;

    private AnnotationEnum(String key, String value){
        this.key = key;
        this.value = value;
    }

    public static  String getKey(String value) {
        return AnnotationEnum.valueOf(value).key;
    }
    public static String getValue(String key) {

        return AnnotationEnum.valueOf(key).value;
    }

}

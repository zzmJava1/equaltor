package com.zzm.equaltor.basepackage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于属性的比对器
 *
 * @author dadiyang date 2018/11/22
 */
public class FieldBaseEquator extends AbstractEquator {

    private static final Map<Class<?>, Map<String, Field>> CACHE = new ConcurrentHashMap<>();

    public FieldBaseEquator() {
    }

    public FieldBaseEquator(boolean bothExistFieldOnly) {
        super(bothExistFieldOnly);
    }

    public FieldBaseEquator(List<String> includeFields, List<String> excludeFields,
        boolean bothExistFieldOnly) {
        super(includeFields, excludeFields, bothExistFieldOnly);
    }

    /**
     * 指定包含或排除某些字段
     *
     * @param includeFields 包含字段，若为 null 或空集，则不指定
     * @param excludeFields 排除字段，若为 null 或空集，则不指定
     */
    public FieldBaseEquator(List<String> includeFields, List<String> excludeFields) {
        super(includeFields, excludeFields);
    }

    /**
     * 递归计算对象属性和list对象属性 注：如果list的size不相同直接返回list不会进行深度比较
     */
    @Override
    public List<FieldInfo> getDiffFields(Object first, Object second) {

        //递归返回条件们
        if ((first==null&&second==null)||first == second) {
            return Collections.emptyList();
        }
        if(first==null||second==null){
            Object val = first==null? second:first;
            Class<?> clazz = val.getClass();
            // 不等的字段名称使用类的名称
            return Collections.singletonList(
                new FieldInfo(clazz.getPackage().getName()+clazz.getName(), clazz, first, second));
        }

        //如果是list，进行list比较
        if(first instanceof List && second instanceof List){
            List<FieldInfo> diffField = new ArrayList<>();
            int sizeF=((List<?>) first).size();
            int sizeS=((List<?>) second).size();
            if(sizeF!=sizeS){
                Object obj = first == null ? second : first;
                Class<?> clazz = obj.getClass();
                // 不等的字段名称使用类的名称
                return Collections.singletonList(new FieldInfo(clazz.getSimpleName(), clazz, first, second));
            }
            for (int i = 0; i <sizeF ; i++) {
                Object firstVal = ((List) first).get(i);
                Object secondVal = ((List) second).get(i);
                //是基本类型的list，直接比较
                if (isSimpleField(firstVal, secondVal)) {
                    if(firstVal!=secondVal&&!firstVal.equals(secondVal)){
                        diffField.addAll(Collections.singletonList(new FieldInfo(firstVal.getClass().getName(),firstVal.getClass(),firstVal,secondVal)));
                    }
                }else{
                    //不是基本类型递归diff
                    diffField.addAll(getDiffFields(firstVal, secondVal));
                }
            }
            return diffField;

        }
        //不是list反射获取属性

        Set<String> allFieldNames;
        // 获取所有字段
        Map<String, Field> firstFields = getAllFields(first);
        Map<String, Field> secondFields = getAllFields(second);
        if (first == null) {
            allFieldNames = secondFields.keySet();
        } else if (second == null) {
            allFieldNames = firstFields.keySet();
        } else {
            allFieldNames = getAllFieldNames(firstFields.keySet(), secondFields.keySet());
        }
        List<FieldInfo> diffField = new ArrayList<>();
        for (String fieldName : allFieldNames) {
            try {
                Field firstField = firstFields.getOrDefault(fieldName, null);
                Field secondField = secondFields.getOrDefault(fieldName, null);
                Object firstVal = null;
                Object secondVal = null;
                if (firstField != null) {
                    firstField.setAccessible(true);
                    firstVal = firstField.get(first);
                }
                if (secondField != null) {
                    secondField.setAccessible(true);
                    secondVal = secondField.get(second);
                }
                if(firstVal==null&&secondVal==null){
                    continue;
                }
                if((firstVal==null||secondVal==null)){

                    diffField.addAll(Collections.singletonList(new FieldInfo(fieldName,firstField.getType(),firstVal,secondVal)));

                }else {
                    if (isSimpleField(firstVal, secondVal)) {
                        if(firstVal!=secondVal&&!firstVal.equals(secondVal)){
                            diffField.addAll(Collections.singletonList(new FieldInfo(fieldName,firstField.getType(),firstVal,secondVal)));
                        }
                    }else{
                        //不是基本类型递归diff
                        diffField.addAll(getDiffFields(firstVal, secondVal));
                    }

                }

            } catch (IllegalAccessException e) {
                throw new IllegalStateException("获取属性进行比对发生异常: " + fieldName, e);
            }

        }
        return diffField;

    }

    private Map<String, Field> getAllFields(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }
        return CACHE.computeIfAbsent(obj.getClass(), k -> {
            Map<String, Field> fieldMap = new HashMap<>(8);
            Class<?> cls = k;
            while (cls != Object.class) {
                Field[] fields = cls.getDeclaredFields();
                for (Field field : fields) {
                    // 一些通过字节码注入改写类的框架会合成一些字段，如 jacoco 的 $jacocoData 字段
                    // 正常情况下这些字段都需要被排除掉
                    if (!field.isSynthetic()) {
                        fieldMap.put(field.getName(), field);
                    }
                }
                cls = cls.getSuperclass();
            }
            return fieldMap;
        });
    }
}

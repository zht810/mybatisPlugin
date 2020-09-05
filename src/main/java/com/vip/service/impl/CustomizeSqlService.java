package com.vip.service.impl;

import com.vip.service.SqlService;
import com.vip.util.ReflectUtil;

public class CustomizeSqlService implements SqlService {
    private String customizeClassName;
    private String customizeMethodName;

    public CustomizeSqlService(String customizeClassName, String customizeMethodName) {
        this.customizeClassName = customizeClassName;
        this.customizeMethodName = customizeMethodName;
    }

    public String getNewSql() {
        Class clazz = ReflectUtil.getclass(customizeClassName);
        Object result = ReflectUtil.getMethod(customizeMethodName, clazz, null);
        return (String) result;

//        return "select * from paper limit 1";
    }
}

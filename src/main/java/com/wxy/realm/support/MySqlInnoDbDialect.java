package com.wxy.realm.support;


import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StringType;

/**
 * @Author wxy
 * @Date 2020/9/25 10:58
 * @Version 1.0
 */
public class MySqlInnoDbDialect extends MySQLDialect {

    public MySqlInnoDbDialect() {
        super();
        registerFunction("convert_gbk", new SQLFunctionTemplate(StringType.INSTANCE, "convert(?1 using gbk)"));
        registerColumnType(93, "datetime(3)");
    }

    @Override
    public String getTableTypeString() {
        return " engine=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin";
    }
}

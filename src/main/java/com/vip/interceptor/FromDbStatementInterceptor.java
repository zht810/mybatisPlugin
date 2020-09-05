package com.vip.interceptor;

import com.mysql.jdbc.StringUtils;
import com.vip.po.Page;
import com.vip.service.SqlService;
import com.vip.service.impl.CustomizeSqlService;
import com.vip.service.impl.MysqlSqlService;
import com.vip.service.impl.RedisSqlService;
import com.vip.util.ReflectUtil;
import com.vip.util.StreamUtil;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class FromDbStatementInterceptor implements Interceptor {
    private String dbType;
    private String mysqlMethodName;
    private String mysqlConfigName;

    private String customizeClassName;
    private String customizeMethodName;

    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("-------》》mybatis FromDbStatementInterceptor start《《------");

        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
        BoundSql boundSql = delegate.getBoundSql();
        Object paramObj = boundSql.getParameterObject();
        String templateSql = boundSql.getSql();
        Page<?> page = null;
        if (templateSql.toLowerCase().contains("@template_sql_table@") == false || StringUtils.isNullOrEmpty(dbType)) {
            return invocation.proceed();
        }
        MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
        String selectSql = this.getRealSql();
        BoundSql realBoundsql = this.parseSql(selectSql, mappedStatement.getConfiguration(), paramObj);
        //利用反射设置当前BoundSql新的sql和新的参数mappings
        ReflectUtil.setFieldValue(boundSql, "sql", realBoundsql.getSql());
        ReflectUtil.setFieldObjectValue(boundSql, "parameterMappings", realBoundsql.getParameterMappings());
        return invocation.proceed();
    }

    private BoundSql parseSql(String realSal, Configuration configuration, Object paramObj) {
        InputStream strToStream = StreamUtil.getStrToStream(realSal); // 文本转输入流
        XPathParser xPathParser = new XPathParser(strToStream, false, null, new XMLMapperEntityResolver()); // 解析xml文件
        XNode xNode = xPathParser.evalNode("select"); // 将select语句生成XNode

        LanguageDriver langDriver = configuration.getLanguageDriver(null);
        SqlSource sqlSource = langDriver.createSqlSource(configuration, xNode, null); // 解析各个标签，并替换，创建sqlSource
        BoundSql boundSql = sqlSource.getBoundSql(paramObj);

        return boundSql;
    }

    private String getRealSql() throws IOException {

        SqlService sqlService = null;
        if (dbType.equalsIgnoreCase("mysql")) { //mysql
            sqlService = new MysqlSqlService(mysqlConfigName, mysqlMethodName);
        } else if (dbType.equalsIgnoreCase("redis")) { // redis
            sqlService = new RedisSqlService();
        } else if (dbType.equalsIgnoreCase("customize")){ // 自定义
            sqlService = new CustomizeSqlService(customizeClassName, customizeMethodName);
        }

        String realSql = sqlService.getNewSql();
        return realSql;
    }

    public Object plugin(Object target) {
        Object wrap = Plugin.wrap(target, this);
        return wrap;
    }

    public void setProperties(Properties properties) {
        this.dbType = (String) properties.get("dbType");
        this.mysqlMethodName = (String) properties.get("mysqlMethodName");
        this.mysqlConfigName = (String) properties.get("mysqlConfigName");
        this.customizeClassName = (String) properties.get("customizeClassName");
        this.customizeMethodName = (String) properties.get("customizeMethodName");
    }
}

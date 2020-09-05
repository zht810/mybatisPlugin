package com.vip.interceptor;

import com.vip.po.Page;
import com.vip.util.ReflectUtil;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PageStatementInterceptor implements Interceptor {
    private String dataBaseType;

    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("-------》》mybatis Intercept test《《------");

        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
        BoundSql boundSql = delegate.getBoundSql();
        Object paramObj = boundSql.getParameterObject();
        Page<?> page = null;
        if (paramObj instanceof Page<?>) {
            page = (Page<?>) paramObj;
        } else if (paramObj instanceof Map) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) paramObj;
            for (Object param : paramMap.values()) {
                if (param instanceof Page<?>) {
                    page = (Page<?>) param;
                    break;
                }
            }
        } else {
            return invocation.proceed();
        }
        MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
        Connection connection = (Connection) invocation.getArgs()[0];
        String sql = boundSql.getSql();
        this.setTotalRecord(page, paramObj, mappedStatement, connection);
        String pageSql = this.getPageSql(page, sql);
        //利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
        ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
        return invocation.proceed();
    }

    private String getPageSql(Page page, String sql) {
        StringBuilder sb = new StringBuilder(sql);
        if (dataBaseType.equalsIgnoreCase("mysql")) {
            return getMysqlPageSql(page, sb);
        } else if (dataBaseType.equalsIgnoreCase("oracle")) {
            return getOraclePageSql(page, sb);
        }
        return null;
    }

    private String getOraclePageSql(Page page, StringBuilder sb) {
        return "";
    }

    private String getMysqlPageSql(Page page, StringBuilder sb) {
        int startPage = (page.getPageNo() - 1) * page.getPageSize();
        sb.append(" limit ").append(startPage).append(",").append(page.getPageSize());
        return sb.toString();
    }

    private void setTotalRecord(Page<?> page, Object paramObj, MappedStatement mappedStatement, Connection connection) {
//        SqlSource sqlSource = new DynamicSqlSource();
        BoundSql boundSql = mappedStatement.getBoundSql(paramObj);
        String sql = boundSql.getSql();
        String countSql = this.getCountSql(sql);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, paramObj);
        DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, paramObj, countBoundSql);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(countSql);
            parameterHandler.setParameters(ps);
            rs = ps.executeQuery();
            if (rs.next()) {
                int totalRecord = rs.getInt(1);
                page.setTotalRecord(totalRecord);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private String getCountSql(String sql) {
        return "select count(1) from (" + sql + ") countSql";
    }

    public Object plugin(Object target) {
        Object wrap = Plugin.wrap(target, this);
        return wrap;
    }

    public void setProperties(Properties properties) {
        this.dataBaseType = (String) properties.get("dbType");
    }
}

package com.vip.service.impl;

import com.vip.service.SqlService;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MysqlSqlService implements SqlService {
    private String methodName;
    private String configName;

    public MysqlSqlService(String configName , String methodName) {
        this.methodName = methodName; //"SqlMapConfig.xml"
        this.configName = configName; // "com.vip.dao.PaperDao.queryById"
    }

    public String getNewSql() {
        try {
            InputStream inputStream = Resources.getResourceAsStream(configName);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession sqlSession = sqlSessionFactory.openSession();
            //在selectOne方法中传入namespace + id的字符串拼接
            String sql = sqlSession.selectOne(methodName, methodName);

            System.out.println(sql);
            sqlSession.commit();
            sqlSession.close();
            return sql;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}

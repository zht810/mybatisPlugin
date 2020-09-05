package com.vip.demo;

import com.vip.dao.PaperDao;
import com.vip.po.Page;
import com.vip.po.Paper;
import com.vip.util.StreamUtil;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public String testCustomize() {
        // 复杂sql测试
        String testStr = "<select resultType=\"Paper\" parameterType=\"java.lang.Integer\" id=\"queryByPage\">" +
                "select * from paper <if test=\" id != null\"> where id  != #{id} </if> <if test=\" paperName != null and paperName != '' \">" +
                "and paper_name=#{paperName} </if> \t<if test=\" list != null and list.size>0 \"> and paper_type in<foreach collection=\"list\" item=\"item\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach></if>limit 1" +
                "</select>";
        return testStr;
        // 简单sql 测试
//        return "<select resultType=\"com.vip.service.po.Paper\" parameterType=\"java.lang.Integer\" id=\"queryByPage\">select * from paper   where id  != #{id} limit 1</select>";
    }

    @org.junit.Test
    public void testQuickStart3() throws IOException {
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        SqlSession session = factory.openSession();
        PaperDao dao = session.getMapper(PaperDao.class);

        List<Integer> typeList = new ArrayList<Integer>();
        typeList.add(1);
        typeList.add(2);
        Paper paper = dao.queryVirtualSql_1(2, "test2", typeList);
        System.out.println(paper);
        session.close();
    }

    @org.junit.Test
    public void testQuickStart2() throws IOException {
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        SqlSession session = factory.openSession();
        PaperDao dao = session.getMapper(PaperDao.class);

        Page<Paper> page = new Page<Paper>();
        Paper users = dao.queryVirtualSql(1);
//        List<Paper> users = dao.queryByPage1(page);
        System.out.println(users);
        session.close();
    }

    @org.junit.Test
    public void testQuickStart4() throws IOException {
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        SqlSession session = factory.openSession();
        PaperDao dao = session.getMapper(PaperDao.class);

        Page<Paper> page = new Page<Paper>();
        List<Paper> users = dao.queryByPage(page, 1, "test1");
        System.out.println(users);
        session.close();
    }

    // 测试二级缓存
    @org.junit.Test
    public void testQuickStart1() throws IOException {
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        SqlSession session = factory.openSession();
        PaperDao dao = session.getMapper(PaperDao.class);
        Paper users = dao.queryOne();
        System.out.println(users);
        session.close();

        System.out.println("第二次查询。。。。。。。");
        SqlSession session2 = factory.openSession();
        PaperDao dao2 = session2.getMapper(PaperDao.class);
        Paper users2 = dao2.queryOne();
        System.out.println(users2);
        session2.close();
    }


    @org.junit.Test
    public void testQuickStart() throws IOException {
        //1. 读取核心配置文件SqlMapConfig.xml
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2. 创建SqlSessionFactoryBuilder构造者对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //3. 使用构造者builder，根据配置文件的信息is，构造一个SqlSessionFactory工厂对象
        SqlSessionFactory factory = builder.build(is);
        //4. 使用工厂对象factory，生产一个SqlSession对象
        SqlSession session = factory.openSession();
        //5. 使用SqlSession对象，获取映射器UserDao接口的代理对象
        PaperDao dao = session.getMapper(PaperDao.class);
        //6. 调用UserDao代理对象的方法，查询所有用户
        List<Paper> users = dao.queryAll();
        for (Paper user : users) {
            System.out.println(user);
        }
        //7. 释放资源
     /*   session.close();
        is.close();*/

        System.out.println("第二次查询。。。。。。。");
        List<Paper> users1 = dao.queryAll();
        for (Paper user : users1) {
            System.out.println(user);
        }
        session.close();
    /*    SqlSession session1 = factory.openSession();
        PaperDao dao1 = session1.getMapper(PaperDao.class);
        List<Paper> users1 = dao1.queryAll();
        for (Paper user : users1) {
            System.out.println(user);
        }*/
    }

    public static void main1(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://10.189.108.57:3309/vip_exam",
                    "vip_exam_user", "vip_exam_user");
            String sql = "select * from paper limit 10";
            preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
//                System.out.println("id:{}，paper_name:{}", resultSet.getLong("id"), resultSet.getString("paper_name"));
            }
        } catch (Exception e) {
//            log.error("error:{}", e);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                preparedStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        String testStr = "<select>select * from paper <if test=\"id != null\"> where id != #{id}</if></select>";
        String testStr = "<select resultType=\"Paper\" parameterType=\"java.lang.Integer\" id=\"queryByPage\">select * from paper where id != #{id}</select>";
        InputStream strToStream = StreamUtil.getStrToStream(testStr);
        XPathParser xPathParser = new XPathParser(strToStream, false, null, new XMLMapperEntityResolver());
        XNode xNode = xPathParser.evalNode("select");
        System.out.println(xNode);
    }
}

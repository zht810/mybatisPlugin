package com.vip.dao;

import com.vip.po.Page;
import com.vip.po.Paper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PaperDao {
    List<Paper> queryAll();
    List<Paper> queryByPage(@Param("page") Page page, @Param("id") Integer id, @Param("paperName") String paperName);
    List<Paper> queryByPage1(Page page);
    Paper queryOne();
    String queryMethod(@Param("methodName")String methodName);
    Paper queryById(@Param("id") Integer id);
    Paper queryVirtualSql(@Param("id") Integer id);
    Paper queryVirtualSql_1(@Param("id") Integer id, @Param("paperName") String paperName,
                            @Param("list") List<Integer> tyepList);
}

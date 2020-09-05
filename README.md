# mybatisPlugin
主要实现sql语句在第三方存储，无需硬编码到xml文件中
基本原理：

1、通过拦截器FromDbStatementInterceptor，对StatementHandler类的prepare方法进行处理，替换当前的模板sql，执行真正的查询。
2、模板sql的作用是为了使参数和真正sql保持相同，方便进行替换
3、替换的sql支持mybatis的各种标签，如<if>，替换后需要进行新一次的标签解析和参数绑定。
  如模板sql如下：
  
	<select id="queryVirtualSql_1" resultType="com.vip.po.Paper">
        select * from @template_sql_table@
         where
        field = #{id}
        and field=#{paperName}
        and field in
        <foreach collection="list" item="item" open="(" separator="," close=")">
            #{item}
        </foreach>
     </select>
     
   替换sql如下：
  <select resultType="Paper" parameterType="java.lang.Integer" id="queryByPage">
   select * from paper
     where
   <if test=" id != null and id ==1">
       and attention != 'attention'
   </if>
    <if test=" id != null and id ==2">
       and attention != 'attention1'
   </if>
   <if test=" paperName != null and paperName != '' ">
      and paper_name=#{paperName}
   </if> 
   <if test=" list != null and list.size>0 ">
      and paper_type in
      <foreach collection="list" item="item" open="(" separator="," close=")">
       #{item}
     </foreach>
   </if>
   limit 1
</select>
 
4、这样如果线上复杂sql有问题，只需要修改配置的sql即可，无需重新上线。

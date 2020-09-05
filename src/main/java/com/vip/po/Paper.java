package com.vip.po;


import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 试卷信息表
 * </p>
 *
 * @author hongtao05.zhang
 * @since 2020-03-10
 */
public class Paper implements Serializable {

       private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    private Long id;

    /**
     * 试卷名称
     */
    private String paperName;

    /**
     * 试卷类型（1为随机，2为固定）
     */
    private Integer paperType;


    /**
     * 考试注意事项
     */
    private String attention;


    /**
     * 备注
     */
    private String remark;


    /**
     * 测试用（为方便测试，一般不会写这里）
     */
    private String methodName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Integer getPaperType() {
        return paperType;
    }

    public void setPaperType(Integer paperType) {
        this.paperType = paperType;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}

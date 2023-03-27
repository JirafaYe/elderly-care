package org.cuit.app.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author Jirafa
 * @since 2023-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_todo_list")
public class TodoList implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String todo;

    /**
     * 老人id
     */
    private Integer elderlyId;

    /**
     * 开始时间
     */
    private Date begin;


    /**
     * 事件日期
     */
    private Date date;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private Date deleteTime;


}

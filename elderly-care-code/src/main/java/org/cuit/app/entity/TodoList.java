package org.cuit.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

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

    @TableId(value = "id", type = IdType.INPUT)
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
     * 结束时间
     */
    private Date end;

    /**
     * 事件日期
     */
    private Date date;

    private Date updateTime;

    private Date createTime;

    private Date deleteTime;


}
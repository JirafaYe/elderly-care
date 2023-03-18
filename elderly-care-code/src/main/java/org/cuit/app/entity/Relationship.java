package org.cuit.app.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author jirafa
 * @since 2023-03-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_relationship")
@NoArgsConstructor
public class Relationship implements Serializable {

    private static final long serialVersionUID = 1L;

    public Relationship(Integer guardian, Integer elderly) {
        this.guardian = guardian;
        this.elderly = elderly;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 监护人id
     */
    private Integer guardian;

    /**
     * 老人id
     */
    private Integer elderly;

    /**
     * create_time
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 不为null时表示删除
     */
    private Date deleteTime;



}

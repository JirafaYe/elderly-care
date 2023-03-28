package org.cuit.app.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class TodoListVO {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 待做事项
     */
    @NotBlank
    private String todo;

    /**
     * 老人用户名
     */
    @NotNull
    private String  elderlyName;

    /**
     * 开始时间
     */
    private Date begin;


    /**
     * 事件日期
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
}

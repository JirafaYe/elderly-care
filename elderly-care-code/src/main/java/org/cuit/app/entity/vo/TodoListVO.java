package org.cuit.app.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class TodoListVO {

    /**
     * 待做事项
     */
    @NotBlank
    private String todo;

    /**
     * 老人id
     */
    @NotNull
    private Integer elderlyId;

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

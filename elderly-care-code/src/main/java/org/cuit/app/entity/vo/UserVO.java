package org.cuit.app.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserVO {

    /**
     * 用户名
     */
    @NotBlank
    private String name;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 是否是老人
     */
    @NotBlank
    private String  isElderly;
}

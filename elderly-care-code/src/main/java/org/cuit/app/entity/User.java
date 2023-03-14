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
 * @author jirafa
 * @since 2023-03-14
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 主键
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 用户名
     */
      private String name;

      /**
     * 密码
     */
      private String password;

      /**
     * 用户角色，0是老人，1是监护人
     */
      private Integer identity;

    private Date createTime;

    private Date updateTime;

      /**
     * 不为null时表示删除
     */
      private Date deleteTime;


}

package org.cuit.app.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.cuit.app.entity.User;

@Data
@AllArgsConstructor
public class CheckBindingVO {
    private UserVO binder;

    private UserVO elderly;
}

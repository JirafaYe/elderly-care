package org.cuit.app.handler;

import lombok.extern.slf4j.Slf4j;
import org.cuit.app.utils.R;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 异常处理
 *
 * @author jirafa
 * @since 2023/3/14 22:11
 */
@RestController
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理JSR303 参数校验异常
     *
     * @param e e
     * @return {@link R}<{@link ?}>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<?> error(ConstraintViolationException e) {
        log.error(e.getMessage());
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuilder res = new StringBuilder("参数异常: ");
        constraintViolations.forEach(c -> res.append(c.getMessage()).append(" "));
        return R.fail(res.toString().trim());
    }

    /**
     * 处理JSR303 参数校验异常
     *
     * @param e e
     * @return {@link R}<{@link ?}>
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public R<?> argumentError(Exception e) {
        e.printStackTrace();
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        }
        StringBuilder msg = new StringBuilder();
        assert bindingResult != null;
        bindingResult.getFieldErrors().forEach((fieldError) ->
                msg.append(fieldError.getDefaultMessage()).append(" ")
        );
        String res = msg.toString().trim();
        log.error(res);
        return R.fail(res);
    }

    @ExceptionHandler(Exception.class)
    public R<?> unknownError(Exception e) {
        log.error(e.getMessage());
        return R.fail("发生未知异常，请一段时间后重试或联系管理员！");
    }
}
package com.wxy.realm.global;

import com.wxy.realm.support.ApiException;
import com.wxy.realm.support.IErrorCode;
import com.wxy.realm.support.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常拦截处理
 *
 * @Author wxy
 *
 * @Date 2020/9/22 13:08
 * @Version 1.0
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result<Object> handle(Exception exception) {
        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            IErrorCode errorCode = apiException.getErrorCode();
            log.error(errInfo(exception));
            if (null != errorCode) {
                return Result.failed(errorCode, errorCode.getMessage());
            } else {
                return Result.failed();
            }
        }
        throw new RuntimeException(exception);
    }

    public String errInfo(Exception e) {
        String str = "";
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            str = sw.toString();
        } catch (IOException ie) {
            log.error(ie.getMessage());
        }
        return str;
    }
}

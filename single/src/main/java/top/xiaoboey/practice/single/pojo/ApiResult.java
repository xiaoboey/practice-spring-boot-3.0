package top.xiaoboey.practice.single.pojo;

import java.io.Serializable;

/**
 *
 * @author xiaoqb
 */
public class ApiResult<T> implements Serializable {
    private Integer code;
    private String message;
    private T content;

    public ApiResult() {
    }

    public ApiResult(Integer code, String message, T content) {
        this.setCode(code);
        this.setMessage(message);
        this.setContent(content);
    }

    public ApiResult(Integer code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    public ApiResult(Integer code) {
        this.setCode(code);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}

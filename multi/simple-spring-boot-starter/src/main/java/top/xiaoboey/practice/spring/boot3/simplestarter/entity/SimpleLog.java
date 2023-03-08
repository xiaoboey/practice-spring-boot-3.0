package top.xiaoboey.practice.spring.boot3.simplestarter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author xiaoqb
 */
@Entity
@Table(name = "simple_log")
public class SimpleLog {
    @Id
    private String requestId;

    @Column(nullable = false)
    private String uri;

    @Column
    private String queryParams;

    @Column
    private String operation;

    @Column
    private String jsonResult;

    @Column(nullable = false)
    private Integer statusCode;

    @Column
    private String statusMsg;

    @Column(nullable = false)
    private Timestamp accessTime;

    @Column
    private Long durationMs;

    @Column
    private Timestamp logTime;

    @Column
    private Long userId;

    public SimpleLog(){

    }

    public SimpleLog(HttpServletRequest request) {
        this.setRequestId(request.getRequestId());
        this.setUri(request.getRequestURI());
        this.setAccessTime(Timestamp.from(Instant.now()));
        this.setDurationMs(0L);
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public Timestamp getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Timestamp accessTime) {
        this.accessTime = accessTime;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public Timestamp getLogTime() {
        return logTime;
    }

    public void setLogTime(Timestamp logTime) {
        this.logTime = logTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

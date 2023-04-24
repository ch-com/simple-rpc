package org.simple.core.domain.exchange;

/**
 * 响应状态码
 * @author ch
 * @date 2023/4/20
 */
public enum ResponseStateEnum {
    FAILED((byte)2, "failed"),
    NOT_FIND((byte)1, "No published services"),
    SUCCESS((byte)0, "success")
    ;
    // 状态码
    private byte state;
    // 状态信息
    private String message;

    ResponseStateEnum(byte state, String message) {
        this.state = state;
        this.message = message;
    }

    public byte getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }
}

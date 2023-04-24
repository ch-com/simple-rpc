package org.simple.core.domain.exchange;


import cn.hutool.core.util.StrUtil;

import java.io.Serializable;

/**
 * @author ch
 * @date 2023/4/18
 */
public class ResponseBody implements MessageBody {
    private static final long serialVersionUID = 1L;
    private String requestId;
    // 处理状态
    private byte state;
    private String stateMessage;
    private Object responseData;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getResponseData() {
        return responseData;
    }

    public void setResponseData(Object responseData) {
        this.responseData = responseData;
    }

    /**
     * 实例化一个成功的响应体
     * @param requestId
     * @param stateEnum
     * @param data
     * @return
     */
    public static ResponseBody buildSuccessResponse(String requestId, Object data) {
         ResponseBody body = new ResponseBody();
         body.setRequestId(requestId);
         body.setResponseData(data);
         body.state = ResponseStateEnum.SUCCESS.getState();
         body.stateMessage = ResponseStateEnum.SUCCESS.getMessage();
         return body;
    }
    public static ResponseBody buildFailResponse(String requestId, Object data, String errorMsg) {
        ResponseBody body = new ResponseBody();
        body.setRequestId(requestId);
        body.setResponseData(data);
        body.state = ResponseStateEnum.FAILED.getState();
        body.stateMessage = StrUtil.isEmpty(errorMsg) ? ResponseStateEnum.FAILED.getMessage() : errorMsg;
        return body;
    }

    public static ResponseBody buildResponse(String requestId, Object data, ResponseStateEnum stateEnum, String errorMsg) {
        ResponseBody body = new ResponseBody();
        body.setRequestId(requestId);
        body.setResponseData(data);
        body.state = stateEnum.getState();
        body.stateMessage = StrUtil.isEmpty(errorMsg) ? stateEnum.getMessage() : errorMsg;
        return body;
    }

    @Override
    public String toString() {
        return "ResponseBody{" +
                "requestId='" + requestId + '\'' +
                ", state=" + state +
                ", stateMessage='" + stateMessage + '\'' +
                ", responseData=" + responseData +
                '}';
    }
}

package com.imooc.VO;

import lombok.Data;

import java.util.List;

@Data
public class ResultVO<T> {

    /**错误码 */
    private Integer code;

    /**提示信息 */
    private String msg;

    /**传送的数据*/
    private T data;


}

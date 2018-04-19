package com.imooc.exception;


import com.imooc.enums.ResultEnum;
import lombok.Data;

@Data
public class SellExecption extends RuntimeException{

    private Integer code;
    //private String message;

    public SellExecption(ResultEnum resultEnum) {
        super(resultEnum.getMessage());

        this.code = resultEnum.getCode();
    }

    public SellExecption(Integer code,String message) {
        super(message);

        this.code = code;
    }
}

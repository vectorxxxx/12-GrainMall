package xyz.funnyboy.common.constant;

/**
 * 状态枚举
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-21 11:50:54
 */
public enum StatusEnum
{
    SPU_NEW(0, "新建"),
    SPU_UP(1, "上架"),
    SPU_DOWN(2, "下架");

    private final int code;

    private final String msg;

    StatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

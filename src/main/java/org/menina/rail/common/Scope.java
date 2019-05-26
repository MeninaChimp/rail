package org.menina.rail.common;

/**
 * @author zhenghao
 * @date 2019/1/15
 */
public enum Scope {

    /**
     * Mark enable on consumer side only
     */
    CONSUMER((byte) 0x01),

    /**
     * Mark enable on provider side only
     */
    PROVIDER((byte) 0x02),

    /**
     * Mark enable both side
     */
    ALL(((byte) 0x04));

    private byte value;

    Scope(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}

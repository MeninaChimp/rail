package org.menina.rail.common;

/**
 * @author zhenghao
 * @date 2019/1/12
 */
public class IdGenerator {
    private long workerId;
    private long datacenterId;
    private long sequence;
    private long twepoch;
    private long workerIdBits;
    private long datacenterIdBits;
    private long maxWorkerId;
    private long maxDatacenterId;
    private long sequenceBits;
    private long workerIdShift;
    private long datacenterIdShift;
    private long timestampLeftShift;
    private long sequenceMask;
    private long lastTimestamp;

    public IdGenerator() {
        this(0L, 0L);
    }

    public IdGenerator(long workerId, long datacenterId) {
        this.sequence = 0L;
        this.twepoch = 1288834974657L;
        this.workerIdBits = 5L;
        this.datacenterIdBits = 5L;
        this.maxWorkerId = ~(-1L << (int) this.workerIdBits);
        this.maxDatacenterId = ~(-1L << (int) this.datacenterIdBits);
        this.sequenceBits = 12L;
        this.workerIdShift = this.sequenceBits;
        this.datacenterIdShift = this.sequenceBits + this.workerIdBits;
        this.timestampLeftShift = this.sequenceBits + this.workerIdBits + this.datacenterIdBits;
        this.sequenceMask = ~(-1L << (int) this.sequenceBits);
        this.lastTimestamp = -1L;
        if (workerId <= this.maxWorkerId && workerId >= 0L) {
            if (datacenterId <= this.maxDatacenterId && datacenterId >= 0L) {
                this.workerId = workerId;
                this.datacenterId = datacenterId;
            } else {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", new Object[]{Long.valueOf(this.maxDatacenterId)}));
            }
        } else {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", new Object[]{Long.valueOf(this.maxWorkerId)}));
        }
    }

    public static IdGenerator get() {
        return IdGenHolder.INSTANCE;
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (timestamp < this.lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", new Object[]{Long.valueOf(this.lastTimestamp - timestamp)}));
        } else {
            if (this.lastTimestamp == timestamp) {
                this.sequence = this.sequence + 1L & this.sequenceMask;
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis(this.lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }

            this.lastTimestamp = timestamp;
            return timestamp - this.twepoch << (int) this.timestampLeftShift | this.datacenterId << (int) this.datacenterIdShift | this.workerId << (int) this.workerIdShift | this.sequence;
        }
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for (timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
        }

        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        System.out.println(get().nextId());
    }

    private static class IdGenHolder {
        private static final IdGenerator INSTANCE = new IdGenerator();

        private IdGenHolder() {
        }
    }
}


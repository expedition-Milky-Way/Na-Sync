package cn.deystar.Util.Beans.OSstatus;

import com.alibaba.fastjson.JSONObject;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.math.BigDecimal;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 * 操作系统资源情况
 */
public class OSstatusBean {

    private BigDecimal cpu;

    private BigDecimal memory;

    public BigDecimal getCpu() {
        return cpu;
    }

    public void setCpu(BigDecimal cpu) {
        this.cpu = cpu;
    }

    public BigDecimal getMemory() {
        return memory;
    }

    public void setMemory(BigDecimal ram) {
        this.memory = ram;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

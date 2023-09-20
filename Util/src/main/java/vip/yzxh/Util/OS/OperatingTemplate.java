package vip.yzxh.Util.OS;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.sun.management.OperatingSystemMXBean;
import vip.yzxh.Util.Beans.OSstatus.OSstatusBean;
import vip.yzxh.Util.OS.CPU.CPUMonitorCalc;
import vip.yzxh.Util.OS.Memory.MemoryMonitor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 * 系统资源情况
 */
public class OperatingTemplate {


    private static CPUMonitorCalc cpuMonitorCalc = CPUMonitorCalc.getInstance();

    public static OSstatusBean getOsStatus() {
        OSstatusBean statusBean = new OSstatusBean();
        //1. 获取CPU运行情况
        BigDecimal cpu = cpuMonitorCalc.getProcessCpu();
        if (cpu.doubleValue() > 100D) cpu= new BigDecimal("0.0");
        // 2. 获取内存运行情况
        BigDecimal ram = MemoryMonitor.getMemoryProcess();

        statusBean.setCpu(cpu);
        statusBean.setMemory(ram);
        return statusBean;
    }


}

package cn.deystar.BaiduPan.Core.Client.System.CPU;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class CPUMonitorCalc {
    private static CPUMonitorCalc instance = new CPUMonitorCalc();

    private OperatingSystemMXBean osMxBean;
    private ThreadMXBean threadBean;
    private long preTime = System.nanoTime();
    private long preUsedTime = 0;

    private CPUMonitorCalc() {
        osMxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
    }

    public static CPUMonitorCalc getInstance() {
        return instance;
    }

    public BigDecimal getProcessCpu() {
        long totalTime = 0;
        for (long id : threadBean.getAllThreadIds()) {
            totalTime += threadBean.getThreadCpuTime(id);
        }
        long curtime = System.nanoTime();
        long usedTime = totalTime - preUsedTime;
        long totalPassedTime = curtime - preTime;
        preTime = curtime;
        preUsedTime = totalTime;
        float cpuProcess = (float) ((((double) usedTime) / totalPassedTime / osMxBean.getAvailableProcessors()) * 100);
        return new BigDecimal(cpuProcess).setScale(2,BigDecimal.ROUND_HALF_DOWN);
    }

}

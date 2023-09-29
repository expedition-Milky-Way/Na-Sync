package cn.deystar.BaiduPan.Core.OS.OsService;

import cn.deystar.BaiduPan.Core.OS.OsService.CPU.CPUMonitorCalc;
import cn.deystar.Util.Beans.OSstatus.OSstatusBean;
import cn.deystar.BaiduPan.Core.OS.OsService.Memory.MemoryMonitor;

import java.math.BigDecimal;


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

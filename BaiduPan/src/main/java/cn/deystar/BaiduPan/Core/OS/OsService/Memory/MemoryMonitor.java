package cn.deystar.BaiduPan.Core.OS.OsService.Memory;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class MemoryMonitor {

    private static OperatingSystemMXBean osmxb = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    public static BigDecimal getMemoryProcess(){
        BigDecimal totalvirtualMemory = new BigDecimal(osmxb.getTotalPhysicalMemorySize())
                .divide(new BigDecimal(Math.pow(1024D, 3D))).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal freePhysicalMemorySize = new BigDecimal(osmxb.getFreePhysicalMemorySize())
                .divide(new BigDecimal(Math.pow(1024D, 3D))).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal usedMemory = totalvirtualMemory.subtract(freePhysicalMemorySize);
        BigDecimal ram = usedMemory.divide(totalvirtualMemory, RoundingMode.CEILING).multiply(new BigDecimal("100"));
        return ram;
    }
}

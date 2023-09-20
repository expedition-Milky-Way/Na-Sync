package vip.yzxh.Util.Beans.WsResponse;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 * 网站首页需要的response
 */
public class IndexResponse {

    private Integer cpu;

    private Integer ram;

    private Integer error;

//    private List<> zipTasks;
//
//    private List<> uploadTasks;


    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }
}

package vip.yzxh.BaiduPan.BaiduPanResponse;

import com.alibaba.fastjson.annotation.JSONField;
import vip.yzxh.Util.Const.VipTypeEnums;
import vip.yzxh.Util.HttpServerlet.Response.ResponseData;

/**
 * @Author YeungLuhyun
 * 百度网盘用户回调
 **/
public class UserMsg extends ResponseData {


    /**
     * 百度账号
     */
    @JSONField(name = "baidu_name")
    private String baiduName;

    /**
     * 网盘账号
     */
    @JSONField(name = "netdisk_name")
    private String netDiskName;

    /**
     * 头像
     */
    @JSONField(name = "avatar_url")
    private String avatarUrl;

    @JSONField(name = "vip_type")
    private Integer vipType;

    private VipTypeEnums vipTypeEnums;

    /**
     * 用户 id
     */
    private Integer uk;


    public UserMsg(String baiduName, String netDiskName, String avatarUrl, Integer vipType, VipTypeEnums vipTypeEnums, Integer uk) {
        this.code = DEFAULT_SUCCESS_CODE;
        this.message = DEFAULT_SUCCESS_MESSAGE;
        this.success = true;
        this.baiduName = baiduName;
        this.netDiskName = netDiskName;
        this.avatarUrl = avatarUrl;
        this.vipType = vipType;
        VipTypeEnums[] enums = VipTypeEnums.values();
        for (VipTypeEnums e : enums) {
            if (e.type.equals(vipType)) {
                this.vipTypeEnums = e;
            }
        }
        this.uk = uk;
    }

    public UserMsg(Boolean success, Integer code, String msg) {
        super(success, code, msg, null);
    }

    public String getBaiduName() {
        return baiduName;
    }

    public void setBaiduName(String baiduName) {
        this.baiduName = baiduName;
    }

    public String getNetDiskName() {
        return netDiskName;
    }

    public void setNetDiskName(String netDiskName) {
        this.netDiskName = netDiskName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getVipType() {
        return vipType;
    }

    public void setVipType(Integer vipType) {
        this.vipType = vipType;
    }

    public VipTypeEnums getVipTypeEnums() {
        return vipTypeEnums;
    }

    public void setVipTypeEnums(VipTypeEnums vipTypeEnums) {
        this.vipTypeEnums = vipTypeEnums;
    }

    public Integer getUk() {
        return uk;
    }

    public void setUk(Integer uk) {
        this.uk = uk;
    }
}

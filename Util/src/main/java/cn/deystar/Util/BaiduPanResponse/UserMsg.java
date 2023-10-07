package cn.deystar.Util.BaiduPanResponse;

import com.alibaba.fastjson.annotation.JSONField;
import cn.deystar.Util.Const.VipTypeEnums;

/**
 * @Author YeungLuhyun
 * 百度网盘用户回调
 **/
public class UserMsg {


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


    public Boolean isAllNotNull() {
        return this.baiduName != null && !this.baiduName.trim().isEmpty() &&
                this.netDiskName != null && !this.netDiskName.trim().isEmpty() &&
                this.avatarUrl != null && !this.avatarUrl.trim().isEmpty() &&
                this.vipType != null;
    }

    public UserMsg(String baiduName, String netDiskName, String avatarUrl, Integer vipType, VipTypeEnums vipTypeEnums) {

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
    }

    public UserMsg() {
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
        if (this.vipTypeEnums == null){
            VipTypeEnums[] enums = VipTypeEnums.values();
            for (VipTypeEnums e : enums) {
                if (e.type.equals(vipType)) {
                    this.vipTypeEnums = e;
                }
            }
        }
        return vipTypeEnums;
    }

    public void setVipTypeEnums(VipTypeEnums vipTypeEnums) {
        this.vipTypeEnums = vipTypeEnums;
    }


}

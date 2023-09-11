package vip.yzxh.Util.Const;

/**
 * @Author YeungLuhyun
 * 百度网盘的会员类型
 **/
public enum VipTypeEnums {


    NORMAL(4294967296L, 4194304L, 0),
    VIP(10737418240L, 16777216L, 1),
    SVIP(21474836480L, 33554432L, 2);

    public Long fileSize;

    public Long tempSize;

    public Integer type;

    VipTypeEnums(Long fileSize, Long tempSize, Integer type) {
        this.fileSize = fileSize;
        this.tempSize = tempSize;
        this.type = type;
    }
}

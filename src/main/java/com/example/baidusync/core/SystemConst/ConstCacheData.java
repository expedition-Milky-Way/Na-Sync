package com.example.baidusync.core.SystemConst;

import lombok.Data;

/**
 * @author 杨 名 (字 露煊)
 */
@Data
public class ConstCacheData {

    static final Long[] ONE_TEMP_FILE_SIZES = {4194304L, 16777216L, 33554432L};
    private static Long[] ONE_FILE_SIZES ={4294967296L,10737418240L,21474836480L};
    public String token;

    public Long oneFileMaxSize;

    public Long tempFileMaxSize;

    public String deviceCode;

    public Long expires;

    public Integer vipType;

    public String refreshToken;

    public ConstCacheData(String token,  String deviceCode,
                          Long expires, Integer vipType, String refreshToken) {
        this.token = token;
        this.deviceCode = deviceCode;
        this.expires = expires;
        this.vipType = vipType;
        this.refreshToken = refreshToken;
        this.oneFileMaxSize = ONE_FILE_SIZES[vipType];
        this.tempFileMaxSize = ONE_TEMP_FILE_SIZES[vipType];
    }

    public ConstCacheData() {
    }
}

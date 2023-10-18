package cn.deystar.BaiduPan;

import java.net.URLEncoder;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class URLEncodeTest {

    public static void main(String[] args) {
        String path = "/apps/nasSYNC";
        System.out.println(URLEncoder.encode(path));
    }
}

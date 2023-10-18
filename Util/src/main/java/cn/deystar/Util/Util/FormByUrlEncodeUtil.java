package cn.deystar.Util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class FormByUrlEncodeUtil {


    /**
     * www-form-urlencode formatter
     * @param param
     * @return
     */
    public static String wwwFormUrlEncode(Map<String, String > param) {
        if (param == null || param.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        Integer index = 0;
        for (Map.Entry<String, String> entry : param.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            if (index != param.entrySet().size() - 1) {
                builder.append("&");
            }
            index++;
        }
        return builder.toString();
    }
}

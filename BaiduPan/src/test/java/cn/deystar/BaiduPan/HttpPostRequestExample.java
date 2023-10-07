package cn.deystar.BaiduPan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPostRequestExample {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=126.74ec617646fb382b1d8a6f6ff6c0c322.YmQeZKpnrwvg7yIl7SgymaKIhKdJ1BnJCKiIBJT.DvLMDQ");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
            writer.write("path=/apps/test/mydir&isdir=1&rtype=1");
            writer.flush();
            writer.close();
            httpConn.getOutputStream().close();

            int responseCode = httpConn.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
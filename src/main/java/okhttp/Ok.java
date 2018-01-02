package okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Ok {
    public static void main(String[] args) throws IOException {
        t2();
    }

    public static void t1() {
        String url = "http://sch.cd120.info/hyt/wechat/departmentList?hisCode=HID0101";
        Request.Builder builder = new Request.Builder().url(url);
        builder.addHeader("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Mobile/9B176 micromessenger/4.3.2");  //将请求头以键值对形式添加，可添加多个请求头
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build(); //设置各种超时时间
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println(response.body().string());
            } else {
                System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void t2() throws IOException {
        String url = "http://sch.cd120.info/hyt/wechat/departmentList?hisCode=HID0101";
        Document d = Jsoup
                .connect(url)
                .userAgent("Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; 2014011 Build/HM2014011) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 micromessenger/6.0.0.50_r844973.501 NetType/WIFI")
                .cookie("JSESSIONID", "8D46B0A8B972A60BD95C1604436BB143")
                .get();
        System.out.println(d.html());
    }
}

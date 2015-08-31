package club.eureca.stocks;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sven on 2015/8/5.
 */
public class Network {


    static String httpUrl = "http://apis.baidu.com/apistore/stockservice/stock";
    static String httpArg = "stockid=sz002230";

    public static String request() {return request(httpArg);}

    public static String request(String httpArg) {
        return request(httpUrl,httpArg);
    }

    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  "d3f926dd59ac7f0049c22440b5ac5569");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean validate(String code) {
        boolean validated = false;
        try {
            JSONObject result = new JSONObject(Network.request("stockid=" + code));
            if (result.getInt("errNum") == 0) {
                validated = true;
            }
            else
                Log.v("fuck",result.getString("errMsg"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return validated;
    }
    public static String getTime() {
        String s = null;
        try {
            JSONObject result = new JSONObject(Network.request()).getJSONObject("retData").getJSONObject("stockinfo");
            String date = result.getString("date");
            String time = result.getString("time");
            s = date+" "+time;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }
    //get a stock object by its code or get sh\sz by "sh"\"sz"
    public static Stock getStockBycode(String code) {
        Stock s = null;
        if (code.equals("sh")) {
            try {
                JSONObject result = new JSONObject(Network.request()).getJSONObject("retData");
                JSONObject jb = result.getJSONObject("stockinfo");
                JSONObject jbsh = result.getJSONObject("market").getJSONObject("shanghai");
                s = new Stock("上证指数", jbsh.getDouble("curdot"), jbsh.getDouble("rate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (code.equals("sz")) {
            try {
                JSONObject result = new JSONObject(Network.request()).getJSONObject("retData");
                JSONObject jb = result.getJSONObject("stockinfo");
                JSONObject jbsz = result.getJSONObject("market").getJSONObject("shenzhen");
                s = new Stock("深证成指", jbsz.getDouble("curdot"), jbsz.getDouble("rate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject result = (new JSONObject(request("stockid=" + code))).getJSONObject("retData").getJSONObject("stockinfo");
                double currentprice = result.getDouble("currentPrice");
                double closing = result.getDouble("closingPrice");
                double percent = 100 * (currentprice - closing) / closing;
                s = new Stock(result.getString("name"), currentprice, Double.parseDouble(String.format("%.2f", percent)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        s.setCode(code);
        return s;
    }

}

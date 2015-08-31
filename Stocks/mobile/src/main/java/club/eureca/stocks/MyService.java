package club.eureca.stocks;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.android.wearable.WearableListenerService;

import java.io.IOException;
import java.util.ArrayList;

public class MyService extends WearableListenerService implements MobvoiApiClient.ConnectionCallbacks {
    public final static String GET_STOCK_PATH = "/get/stock";
    public final static String GET_TIME_PATH = "/get/time";
    public final static String GET_STOCKLIST_PATH = "/get/stocklist";
    private MobvoiApiClient mb;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mb = new MobvoiApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).build();
        mb.connect();
        Log.v("23333333", "create");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mb.disconnect();
        Log.v("233333333333", "destroy");
    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v("233333333333333", "i got something");

        if (messageEvent.getPath().equals(GET_STOCK_PATH)) {
            Log.v("233333333", "stock");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendStocks();
                }
            }).start();
        }
        if (messageEvent.getPath().equals(GET_TIME_PATH)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v("2333333", "time");
                    String time = Network.getTime();
                    Wearable.MessageApi.sendMessage(mb, null, GET_TIME_PATH, time.getBytes());
                }
            }).start();
        }
        if (messageEvent.getPath().equals(GET_STOCKLIST_PATH)) {
            Log.v("233333333", "stocklist");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Stock> stocks = new ArrayList<Stock>();
                    SQLiteDatabase db = MyService.this.openOrCreateDatabase("stock.db", Context.MODE_PRIVATE, null);
                    db.execSQL("Create table if not exists stocks (code string)");
                    Cursor c = db.rawQuery("select * from stocks",null);
                    while (c.moveToNext()) {
                        String code = c.getString(c.getColumnIndex("code"));
                        stocks.add(Network.getStockBycode(code));
                    }
                    c.close();
                    db.close();
                    try {
                        Wearable.MessageApi.sendMessage(mb,null,GET_STOCKLIST_PATH,Util.stock2Byte(stocks));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    public void sendStocks() {
        Stock sh = Network.getStockBycode("sh");
        Stock sz = Network.getStockBycode("sz");
        Stock[] stocks = new Stock[]{sh,sz};
        try {
            Wearable.MessageApi.sendMessage(mb, null, GET_STOCK_PATH, Util.stock2Byte(stocks));
            Log.v("2333","sendstocks");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("233333333","connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}

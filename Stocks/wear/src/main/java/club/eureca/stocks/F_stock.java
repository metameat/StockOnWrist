package club.eureca.stocks;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class F_stock extends Fragment implements MobvoiApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    public final static String GET_STOCK_PATH = "/get/stock";
    public final static String GET_TIME_PATH = "/get/time";
    private View root;
    private TextView updatetime;
    private TextView shnumber;
    private TextView sznumber;
    private TextView shgrowth;
    private TextView szgrowth;
    private MobvoiApiClient mb;

    public F_stock() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_f_stock, container, false);
        initView(root);
        return root;
    }
    private void initView(View root) {
        updatetime = (TextView) root.findViewById(R.id.updatetime);
        shnumber = (TextView) root.findViewById(R.id.shnumber);
        sznumber = (TextView) root.findViewById(R.id.sznumber);
        shgrowth = (TextView) root.findViewById(R.id.shgrowth);
        szgrowth = (TextView) root.findViewById(R.id.szgrowth);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mb = new MobvoiApiClient.Builder(getActivity()).addApi(Wearable.API).addConnectionCallbacks(this).build();
        mb.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mb,this);
        Wearable.MessageApi.sendMessage(mb, null, GET_STOCK_PATH, null);
        Wearable.MessageApi.sendMessage(mb,null,GET_TIME_PATH,null);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(GET_STOCK_PATH)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Stock[] stocks = null;
                    try {
                        stocks = (Stock[]) Util.byte2Stock(messageEvent.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Stock sh = stocks[0];
                    Stock sz = stocks[1];
                    shnumber.setText(String.valueOf(sh.getPrice()));
                    shgrowth.setText(String.valueOf(sh.getRate()) + "%");
                    sznumber.setText(String.valueOf(sz.getPrice()));
                    szgrowth.setText(String.valueOf(sz.getRate()) + "%");
                    if (sh.getRate() < 0.0) {
                        shnumber.setTextColor(Color.GREEN);
                        shgrowth.setTextColor(Color.GREEN);
                    }else {
                        shnumber.setTextColor(Color.RED);
                        shgrowth.setTextColor(Color.RED);
                    }
                    if (sz.getRate() < 0.0) {
                        sznumber.setTextColor(Color.GREEN);
                        szgrowth.setTextColor(Color.GREEN);
                    }else {
                        sznumber.setTextColor(Color.RED);
                        szgrowth.setTextColor(Color.RED);
                    }
                }
            });
        }
        if (messageEvent.getPath().equals(GET_TIME_PATH)) {
             getActivity().runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     updatetime.setText(new String(messageEvent.getData()));
                 }
             });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Wearable.MessageApi.removeListener(mb,this);
        mb.disconnect();
    }
}

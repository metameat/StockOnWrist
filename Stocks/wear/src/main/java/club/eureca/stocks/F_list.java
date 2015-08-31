package club.eureca.stocks;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class F_list extends Fragment implements MobvoiApiClient.ConnectionCallbacks,MessageApi.MessageListener {

    private final static String GET_STOCKLIST_PATH = "/get/stocklist";
    private MobvoiApiClient mb;
    private View root;
    private WearableListView listView;
    private WearableListView.Adapter myAdapter;
    private ArrayList<Stock> stocks = new ArrayList<Stock>();
    public F_list() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_f_list, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        listView = (WearableListView) root.findViewById(R.id.stocklist);
        myAdapter = new WearableListView.Adapter() {
            @Override
            public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item,null));
            }

            @Override
            public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
                Stock thisStock = stocks.get(position);
                ((MyHolder) holder).item_name.setText(thisStock.getName());
                ((MyHolder) holder).item_price.setText(String.valueOf(thisStock.getPrice()));
                ((MyHolder) holder).item_rate.setText(thisStock.getRate() + "%");
                if (thisStock.getRate() >= 0)
                    ((MyHolder) holder).item_name.setTextColor(Color.RED);
                else
                    ((MyHolder) holder).item_name.setTextColor(Color.GREEN);
            }

            @Override
            public int getItemCount() {
                return stocks.size();
            }
        };
        listView.setGreedyTouchMode(true);
        listView.setAdapter(myAdapter);
        mb = new MobvoiApiClient.Builder(getActivity()).addApi(Wearable.API).addConnectionCallbacks(this).build();
        mb.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mb,this);
        Wearable.MessageApi.sendMessage(mb,null,GET_STOCKLIST_PATH,null);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mb.disconnect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        try {
            stocks = (ArrayList<Stock>) Util.byte2Stock(messageEvent.getData());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.notifyDataSetChanged();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
class MyHolder extends WearableListView.ViewHolder {

    public TextView item_name;
    public TextView item_price;
    public TextView item_rate;

    public MyHolder(View itemView) {
        super(itemView);
        item_name = (TextView) itemView.findViewById(R.id.item_name);
        item_price = (TextView) itemView.findViewById(R.id.item_price);
        item_rate = (TextView) itemView.findViewById(R.id.item_rate);
    }
}


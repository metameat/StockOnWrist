package club.eureca.stocks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class F_test extends Fragment {

    private View root;
    private ListView listView;
    private TextView updatetime;
    private ArrayList<Stock> stocks;
    private BaseAdapter myAdapter;
    public F_test() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_f_test, container, false);
        initView(root);
        stocks = new ArrayList<Stock>();
        getStocks();
        listView.setAdapter(myAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return stocks.size();
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(F_test.this.getActivity()).inflate(R.layout.item_layout, parent, false);
                Stock stock = stocks.get(position);
                TextView name = (TextView) view.findViewById(R.id.item_name);
                TextView price = (TextView) view.findViewById(R.id.item_price);
                TextView growth = (TextView) view.findViewById(R.id.item_growth);
                name.setText(stock.getName());
                price.setText(String.valueOf(stock.getPrice()));
                growth.setText(String.valueOf(stock.getRate()) + "%");
                if (stock.getRate() > 0)
                    growth.setBackgroundColor(Color.RED);
                return view;
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(F_test.this.getActivity());
                builder.setTitle("是否删除该自选股？");
                builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = getActivity().openOrCreateDatabase("stock.db",Context.MODE_PRIVATE,null);
                        String code = stocks.get(position).getCode();
                        db.execSQL("delete from stocks where code = '" + code + "'");
                        db.close();
                        Toast.makeText(getActivity(),"已删除该自选股",Toast.LENGTH_SHORT).show();
                        F_test.this.getActivity().getFragmentManager().beginTransaction().replace(R.id.holder,new F_test()).commit();
                    }
                });
                builder.show();
                return true;
            }
        });
        return root;
    }

    private void initView(View root) {
        listView = (ListView) root.findViewById(R.id.listview);
        updatetime = (TextView) root.findViewById(R.id.updatetime);
    }

    private void prepare() {
                stocks.add(Network.getStockBycode("sh"));
                stocks.add(Network.getStockBycode("sz"));
                final String time = Network.getTime();
                F_test.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updatetime.setText("上次更新: "+ time);
                    }
                });
    }

    private void getStocks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepare();
                SQLiteDatabase db = F_test.this.getActivity().openOrCreateDatabase("stock.db", Context.MODE_PRIVATE,null);
                db.execSQL("Create table if not exists stocks (code string)");
                Cursor c = db.rawQuery("select * from stocks",null);
                while (c.moveToNext()) {
                    String code = c.getString(c.getColumnIndex("code"));
                    stocks.add(Network.getStockBycode(code));
                }
                c.close();
                db.close();
                F_test.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
        }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}


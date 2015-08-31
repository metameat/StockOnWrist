package club.eureca.stocks;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    public FragmentManager fm = getFragmentManager();
    private F_test f_test = new F_test();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.holder,f_test).commit();
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_add:
                add();
                break;
            case R.id.bt_setting:
                startActivity(new Intent(MainActivity.this,Setting.class));
                break;
        }
    }

    private void add() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入股票代码");
        final View view = getLayoutInflater().from(this).inflate(R.layout.add,null);
        builder.setView(view);
        final EditText editText = (EditText)view;
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = editText.getText().toString();
                final String finalCode = regularize(code);
                final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,"稍等...","检查该自选股是否有效");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(Network.validate(finalCode)) {
                            SQLiteDatabase database = openOrCreateDatabase("stock.db",MODE_PRIVATE,null);
                            database.execSQL("Create table if not exists stocks (code string)");
                            database.execSQL("insert into stocks values ('" + finalCode + "')");
                            database.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                    fm.beginTransaction().replace(R.id.holder,new F_test()).commit();
                                }
                            });
                            }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "不存在该股票", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }).start();

            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
    private String regularize(String code) {
        if (code.startsWith("sz") || code.startsWith("sh"))
            return code;
        else if (code.startsWith("60") || code.startsWith("900")){
            return "sh"+code;
        }
        else return "sz"+code;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

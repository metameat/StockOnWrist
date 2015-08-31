package club.eureca.stocks;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class MainActivity extends Activity {

    private GridViewPager viewPager;
    private DotsPageIndicator indicator;
    private Fragment[] fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }
    private void initView() {
        viewPager = (GridViewPager) findViewById(R.id.viewpager);
        indicator = (DotsPageIndicator) findViewById(R.id.indicator);
        fragments = new Fragment[]{new F_stock(),new F_list()};
        viewPager.setAdapter(new FragmentGridPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getFragment(int i, int i2) {
                return fragments[i2];
            }

            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount(int i) {
                return 2;
            }
        });
        indicator.setPager(viewPager);
    }
}

package com.gloiot.hygounionmerchant.ui.adapter;

import android.support.v4.app.Fragment;

import com.gloiot.hygounionmerchant.ui.fragment.MessageFragment;
import com.gloiot.hygounionmerchant.ui.fragment.MyFragment;
import com.gloiot.hygounionmerchant.ui.fragment.NewHomeFragment;
import com.gloiot.hygounionmerchant.widget.fragmentnavigator.FragmentNavigatorAdapter;


/**
 * Created by Dlt on 2017/3/18 12:53
 */
public class MainFragmentAdapter implements FragmentNavigatorAdapter {

    private static final String TABS[] = {"Home", "Message", "My"};

    @Override
    public Fragment onCreateFragment(int position) {

        switch (position) {
            case 0:
//                return HomeFragment.newInstance(position);
                return NewHomeFragment.newInstance(position);
            case 1:
                return MessageFragment.newInstance(position);
//            case 2:
//                return IncomeFragment.newInstance(position);
            case 2:
                return MyFragment.newInstance(position);
        }
        return null;
    }

    @Override
    public String getTag(int position) {

        switch (position) {
            case 0:
//                return HomeFragment.TAG;
                return NewHomeFragment.TAG;
            case 1:
                return MessageFragment.TAG;
//            case 2:
//                return IncomeFragment.TAG;
            case 2:
                return MyFragment.TAG;
        }
        return null;
    }

    @Override
    public int getCount() {
        return TABS.length;
    }
}

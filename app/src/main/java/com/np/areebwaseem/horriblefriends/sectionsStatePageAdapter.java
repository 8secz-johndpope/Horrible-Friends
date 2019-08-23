package com.np.areebwaseem.horriblefriends;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by areebwaseem on 10/28/17.
 */

public class sectionsStatePageAdapter extends FragmentStatePagerAdapter {
    private long baseId = 0;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    public final List<String> mFragmentTitleList= new ArrayList<>();
    public sectionsStatePageAdapter(FragmentManager fm) {
        super(fm);
    }
    public void addFragment(Fragment fragment, String title)
    {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);

    }
    @Override
    public int getItemPosition(Object object) {
        int index = mFragmentList.indexOf (object);

        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }




    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void deletePage(String fragmentType)
    {
        int position=0;
        boolean checked=false;
        /*
        // Remove the corresponding item in the data set
        for (int i=0; i<mFragmentTitleList.size();i++){
            if (mFragmentTitleList.get(i).equals(fragmentType))
            {
                position=i;
                checked=true;
            }
        }
        if (checked==true) {
            mFragmentTitleList.remove(position);
            mFragmentList.remove(position);
        }
        // Notify the adapter that the data set is changed
       // notifyDataSetChanged();
       */
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

}

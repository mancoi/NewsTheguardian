package com.example.mancoi.news;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by mancoi on 26/08/2017.
 */

class Headline_Adapter extends FragmentPagerAdapter {

    private Context mContext;

    Headline_Adapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new Newest_Fragment();
        } else {
            return new International_Fragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.newest_headline);
        } else {
            return mContext.getString(R.string.international_headline);
        }
    }
}

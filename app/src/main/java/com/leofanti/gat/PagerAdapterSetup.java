package com.leofanti.gat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapterSetup extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapterSetup(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: //print setup
                tabFragmentExIn tab1 = new tabFragmentExIn();
                return tab1;
            case 1: //reciper
                tabFragmentMpIn tab2 = new tabFragmentMpIn();
                return tab2;
            case 2: //usuarios
                tabFragmentPtProd tab3 = new tabFragmentPtProd();
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
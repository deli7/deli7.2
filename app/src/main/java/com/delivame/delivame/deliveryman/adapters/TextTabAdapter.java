package com.delivame.delivame.deliveryman.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class TextTabAdapter extends FragmentPagerAdapter {

   private final List<Fragment> fragmentList;
   private final List<String> titlesList;

   public TextTabAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titlesList) {
      super(fm);
      this.fragmentList = fragmentList;
      this.titlesList = titlesList;
   }

   @Override
   public Fragment getItem(int position) {
      return fragmentList.get(position);
   }

   @Override
   public int getCount() {
      return fragmentList.size();
   }

   @Nullable
   @Override
   public CharSequence getPageTitle(int position) {
      return titlesList.get(position);
   }
}

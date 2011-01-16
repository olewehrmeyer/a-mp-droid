package com.mediaportal.ampdroid.activities.media;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mediaportal.ampdroid.api.DataHandler;
import com.mediaportal.ampdroid.data.VideoShare;
import com.mediaportal.ampdroid.utils.Util;
import com.mediaportal.ampdroid.R;
public class TabSharesActivity extends Activity {
   private ListView mListView;
   private ArrayAdapter<VideoShare> mListItems;
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle _savedInstanceState) {
      super.onCreate(_savedInstanceState);
      setContentView(R.layout.tabsharesactivity);

      mListView = (ListView) findViewById(R.id.ListViewShares);
      mListView.setOnItemClickListener(new OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            VideoShare selected = (VideoShare)mListView.getItemAtPosition(position);
            Util.showToast(v.getContext(), selected.Path);
            //handleListClick(v, position, selected);
         }
      });
      
      mListItems = new ArrayAdapter<VideoShare>(this, android.R.layout.simple_list_item_1);
      mListView.setAdapter(mListItems);
      
      
      DataHandler service = DataHandler.getCurrentRemoteInstance();
      List<VideoShare> shares = service.getAllVideoShares();
      
      for(VideoShare s : shares){
         mListItems.add(s);
      }
   }
}
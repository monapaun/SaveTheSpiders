package com.acidnom.savethespiders;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HighScoreChart extends Activity {
	
		private HighScoreTable hsTable;
		private HighScoreEntry[] hsEntries;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.high_score);
			
			hsTable = new HighScoreTable(this);
			hsEntries = hsTable.getHighScores();
			hsTable.closeDB();
			
			TableLayout table = (TableLayout) findViewById(R.id.HighScoreTableLayout01);
			
			for (int i=0; i < HighScoreTable.HIGHSCORE_NUM_ENTRIES; i++)
			{
				TableRow row = new TableRow(this);
				
				TextView t = new TextView(this);
				t.setText(String.valueOf(i+1));
				t.setPadding(10, 10, 10, 10);
				
				row.addView(t);
				
				t = new TextView(this);
				t.setText(hsEntries[i].mName);
				t.setPadding(10, 10, 10, 10);
				t.setWidth(130);
				
				row.addView(t);
				
				t = new TextView(this);
				t.setText(String.valueOf(hsEntries[i].mScore));
				t.setPadding(10, 10, 10, 10);
				
				row.addView(t);
				
				table.addView(row);
			}
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			finish();
			return true;
		}
}
package com.acidnom.savethespiders;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class Help extends Activity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.help);
		}
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			finish();
			return true;
		}

}


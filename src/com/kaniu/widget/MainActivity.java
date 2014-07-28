package com.kaniu.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private ImageWithTextView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new ImageWithTextView(MainActivity.this);
		view.setPadding(10, 10, 10, 10);
		view.setText("项目中遇到当TextView显示的数据不超过3行的时候，不显示下面的展开按钮，这时候就必须要获取到此时TextView的行数，查看api发现了getLineCount()方");
		view.addDrawableIntoViewById(R.drawable.ic_launcher);
		setContentView(view);
	}
}

package jp.januaraid.android.memo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListView extends ListView {
	
	private int mOverscrollDistance = 0;
	
	public MyListView(Context context) {  
		super(context);
	}
	
	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY,
			int scrollX,int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX,
				mOverscrollDistance, isTouchEvent);
	}
	
	@Override
	protected void onOverScrolled(int scrollX, int scrollY,
			boolean clampedX, boolean clampedY) {
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

}

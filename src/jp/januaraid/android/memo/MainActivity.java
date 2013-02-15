package jp.januaraid.android.memo;

import java.util.ArrayList;

import jp.co.omronsoft.android.emoji.EmojiAssist;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	//private ListView mListView;
	//private MyListView mListView;
	private DragSortListView mListView;
	private DragSortController mController;
	//private DragSortListView mListView;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<Integer> mArrayList = new ArrayList<Integer>();;
	public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = true;
    public int removeMode = DragSortController.FLING_RIGHT_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;
    
    public int point;
    
    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
    	@Override
    	public void drop(int from, int to) {
    		if (from != to) {
    			String item = mAdapter.getItem(from);
    			mAdapter.remove(item);
    			mAdapter.insert(item, to);
    			int n = mArrayList.get(from);
        		mArrayList.remove(from);
        		mArrayList.add(to, n);
    		}
    	}
    };
            
    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
    	
    	@Override
    	public void remove(int which) {
    		mAdapter.remove(mAdapter.getItem(which));
    		DataBaseRemove(which);
    	}
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //データベースの処理
        SQLiteDatabase sdb;
        SubOpenHelper helper = new SubOpenHelper(this, "memo.db", 1);
        try {
        	//読み書き可能でオープン
        	sdb = helper.getWritableDatabase();
        } catch(SQLiteException e) {
        	//異常終了
        	return;
        }
        Cursor c = sdb.query("T_USER", new String[] {"_ID", "NAME", "SORT"},
        		null, null, null, null, "SORT", null);
        
        //ListViewの処理
        //mListView = (ListView)findViewById(R.id.listView1);
        //mListView = (MyListView)findViewById(R.id.listView1);
        mListView = (DragSortListView)findViewById(R.id.listView1);
        mController = new DragSortController(mListView);
        mController.setBackgroundColor(getResources().getColor(R.color.lightgrey));
        mController.setDragHandleId(R.id.drag_handle);
        mController.setRemoveEnabled(removeEnabled);
        mController.setSortEnabled(sortEnabled);
        mController.setDragInitMode(dragStartMode);
        mController.setRemoveMode(removeMode);
        mListView.setFloatViewManager(mController);
        mListView.setOnTouchListener(mController);
        mListView.setDragEnabled(dragEnabled);
        mListView.setDropListener(onDrop);
        mListView.setRemoveListener(onRemove);
        mAdapter = new ArrayAdapter<String>(this, R.layout.list_item_handle_left, R.id.text);
        mListView.setAdapter(mAdapter);
        boolean isEof = c.moveToFirst();
        
        mArrayList.clear();
        while(isEof) {
        	mAdapter.add(c.getString(1));
        	mArrayList.add(c.getInt(0));
        	isEof = c.moveToNext();
        }
        sdb.close();
        c.close();
        
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				DragSortListView list = (DragSortListView)parent;
    			String item = (String)list.getItemAtPosition(position);
    			showDialog(item, position);
				
				return false;
			}
		});
        
        /*SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                		mListView,
                		new SwipeDismissListViewTouchListener.OnDismissCallback() {

							@Override
							public void onDismiss(ListView listView,
									int[] reverseSortedPositions) {
								for (int position : reverseSortedPositions) {
                                    mAdapter.remove(mAdapter.getItem(position));
                                    DataBaseRemove(position);
                                }
                                mAdapter.notifyDataSetChanged();								
							}
                			
                		});
        mListView.setOnTouchListener(touchListener);
        mListView.setOnScrollListener(touchListener.makeScrollListener());*/
        
        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        			// TODO Auto-generated method stub
        			ListView list = (ListView)parent;
        			String item = (String)list.getItemAtPosition(position);
        			@SuppressWarnings("unchecked")
					ArrayAdapter<String> adapter = (ArrayAdapter<String>)list.getAdapter();
        			// 項目を削除
        			adapter.remove(item);
        			DataBaseRemove(position);
        	}
        });*/
        //ボタンの処理
        /*Button btn = (Button) this.findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				button1_onClick();
			}

        });*/
    }
    
    /*private void button1_onClick() {
		showDialog();
	}*/
    
    private void showDialog() {
        final EditText editText = new EditText(this);
        //editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);	//1行に制限
        

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle("メモ")
            .setView(editText)
            .setPositiveButton("OK", null)
            		/*new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (editText != null && editText.length() != 0) {
						String editStr = editText.getText().toString();
						mAdapter.add(editStr);
	                    DataBaseAdd(editStr);
					}
				}
			})*/
            .create();
        
        
        
        EmojiAssist ea = EmojiAssist.getInstance();
        ea.addView(editText);
        ea.startAnimation();
        
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        
     // キーハンドリング
        editText.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                 // Enterキーハンドリング
                if (KeyEvent.KEYCODE_ENTER == keyCode) {
                    // 押したときに改行を挿入防止処理
                    if (KeyEvent.ACTION_DOWN == event.getAction()) {
                        return true;
                    }
                     // 離したときにダイアログ上の[OK]処理を実行
                    else if (KeyEvent.ACTION_UP == event.getAction()) {
                        if (editText != null && editText.length() != 0) {
                            // ここで[OK]が押されたときと同じ処理をさせます
                            String editStr = editText.getText().toString();
                            mAdapter.add(editStr);
                            DataBaseAdd(editStr);
                            // AlertDialogを閉じます
                            alertDialog.dismiss();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        
        alertDialog.show();
        //ボタンの設定
        Button buttonOK = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        //if(buttonOK != null) {
        	//buttonOK.setBackgroundColor(getResources().getColor(R.color.black));
        	//buttonOK.setTextColor(getResources().getColor(R.color.white));
        //}
        buttonOK.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (editText != null && editText.length() != 0) {
					String editStr = editText.getText().toString();
					mAdapter.add(editStr);
                    DataBaseAdd(editStr);
                    alertDialog.dismiss();
				} 
			}
		});
    }
    
    private void showDialog(String item, int position) {
    	final EditText editText = new EditText(this);
        //editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);	//1行に制限
        editText.setText(item);
        point = position;

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle("メモ")
            .setView(editText)
            /*.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String editStr = editText.getText().toString();
				}
			})*/
            .create();
        editText.selectAll();
        EmojiAssist ea = EmojiAssist.getInstance();
        ea.addView(editText);
        ea.startAnimation();

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        
     // キーハンドリング
        editText.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                 // Enterキーハンドリング
                if (KeyEvent.KEYCODE_ENTER == keyCode) {
                    // 押したときに改行を挿入防止処理
                    if (KeyEvent.ACTION_DOWN == event.getAction()) {
                        return true;
                    }
                     // 離したときにダイアログ上の[OK]処理を実行
                    else if (KeyEvent.ACTION_UP == event.getAction()) {
                        if (editText != null && editText.length() != 0) {
                            // ここで[OK]が押されたときと同じ処理をさせます
                            String editStr = editText.getText().toString();
                            mAdapter.remove(mAdapter.getItem(point));
                            mAdapter.insert(editStr, point);
                            DataBaseUpdate(editStr, point);
                            // AlertDialogを閉じます
                            alertDialog.dismiss();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        
        alertDialog.show();
	}
    
    public void DataBaseUpdate(String str, int point) {
    	SQLiteDatabase sdb;
        SubOpenHelper helper = new SubOpenHelper(this, "memo.db", 1);
        try {
        	//読み書き可能でオープン
        	sdb = helper.getWritableDatabase();
        } catch(SQLiteException e) {
        	//異常終了
        	return;
        }
        
        sdb.execSQL("update T_USER set NAME = '"+str+"' WHERE (_ID = '"+mArrayList.get(point)+"');");
    }
    
    public void DataBaseAdd(String str) {
    	SQLiteDatabase sdb;
        SubOpenHelper helper = new SubOpenHelper(this, "memo.db", 1);
        try {
        	//読み書き可能でオープン
        	sdb = helper.getWritableDatabase();
        } catch(SQLiteException e) {
        	//異常終了
        	return;
        }
    	sdb.execSQL("INSERT INTO T_USER (NAME) VALUES ('"+str+"')");
    	Cursor c = sdb.query("T_USER", new String[] {"_ID", "NAME", "SORT"},
        		null, null, null, null, null, null);
    	c.moveToLast();
    	mArrayList.add(c.getInt(0));
    	sdb.close();
    	c.close();
    }
    
    private void DataBaseRemove(int position) {
    	SQLiteDatabase sdb;
        SubOpenHelper helper = new SubOpenHelper(this, "memo.db", 1);
        try {
        	//読み書き可能でオープン
        	sdb = helper.getWritableDatabase();
        } catch(SQLiteException e) {
        	//異常終了
        	return;
        }
        
    	sdb.execSQL("delete from T_USER where (_ID = '"+mArrayList.get(position)+"');");
    	mArrayList.remove(position);
    	sdb.close();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	SQLiteDatabase sdb;
        SubOpenHelper helper = new SubOpenHelper(this, "memo.db", 1);
        try {
        	//読み書き可能でオープン
        	sdb = helper.getWritableDatabase();
        } catch(SQLiteException e) {
        	//異常終了
        	return;
        }
        
    	for(int i = 0; i < mArrayList.size(); i++) {
    		sdb.execSQL("update T_USER set SORT = '"+i+"' WHERE (_ID = '"+mArrayList.get(i)+"');");
    	}
    	sdb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	showDialog();
    	
		return true;
    }
    
}

class SubOpenHelper extends SQLiteOpenHelper {

	public SubOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sql;
		// テーブルを作成する
		sql = new StringBuffer();
		sql.append("CREATE TABLE T_USER (");
		sql.append(" _ID integer primary key autoincrement,");
		sql.append(" NAME text NOT NULL,");
		sql.append(" SORT integer");
		sql.append(")");
		db.execSQL(sql.toString());
		
		db.execSQL("INSERT INTO T_USER (NAME) VALUES ('テスト太郎')");
		db.execSQL("INSERT INTO T_USER (NAME) VALUES ('テスト花子')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
}

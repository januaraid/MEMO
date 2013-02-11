package jp.januaraid.android.memo;

import jp.januaraid.android.memo.SwipeDismissListViewTouchListener;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
//import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
//import android.widget.Toast;

public class MainActivity extends Activity {
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private int cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //�f�[�^�x�[�X�̏���
        SQLiteDatabase sdb;
        SubOpenHelper helper = new SubOpenHelper(this, "memo.db", 1);
        try {
        	//�ǂݏ����\�ŃI�[�v��
        	sdb = helper.getWritableDatabase();
        } catch(SQLiteException e) {
        	//�ُ�I��
        	return;
        }
        Cursor c = sdb.query("T_USER", new String[] {"USER_ID", "NAME"},
        		null, null, null, null, null, null);
        
        //ListView�̏���
        //mListView = (ListView)findViewById(R.id.listView1);
        mListView = (ListView)findViewById(R.id.listView1);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mListView.setAdapter(mAdapter);
        boolean isEof = c.moveToFirst();
        cnt = 0;
        while(isEof) {
        	mAdapter.add(c.getString(1));
        	isEof = c.moveToNext();
        	cnt++;
        }
        sdb.close();
        c.close();
        
        SwipeDismissListViewTouchListener touchListener =
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
        mListView.setOnScrollListener(touchListener.makeScrollListener());
        
        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        			// TODO Auto-generated method stub
        			ListView list = (ListView)parent;
        			String item = (String)list.getItemAtPosition(position);
        			@SuppressWarnings("unchecked")
					ArrayAdapter<String> adapter = (ArrayAdapter<String>)list.getAdapter();
        			// ���ڂ��폜
        			adapter.remove(item);
        			DataBaseRemove(position);
        	}
        });*/
        //�{�^���̏���
        Button btn = (Button) this.findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				button1_onClick();
			}

        });
    }
    
    private void button1_onClick() {
		showDialog();
	}
    
    private void showDialog() {
        final EditText editText = new EditText(this);
        //editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);	//1�s�ɐ���

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle("����")
            .setView(editText)
            /*.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String editStr = editText.getText().toString();
				}
			})*/
            .create();

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        
     // �L�[�n���h�����O
        editText.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                 // Enter�L�[�n���h�����O
                if (KeyEvent.KEYCODE_ENTER == keyCode) {
                    // �������Ƃ��ɉ��s��}���h�~����
                    if (KeyEvent.ACTION_DOWN == event.getAction()) {
                        return true;
                    }
                     // �������Ƃ��Ƀ_�C�A���O���[OK]���������s
                    else if (KeyEvent.ACTION_UP == event.getAction()) {
                        if (editText != null && editText.length() != 0) {
                            // ������[OK]�������ꂽ�Ƃ��Ɠ��������������܂�
                            String editStr = editText.getText().toString();
                            mAdapter.add(editStr);
                            DataBaseAdd(editStr);
                            // AlertDialog����܂�
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
    
    public void DataBaseAdd(String str) {
    	SQLiteDatabase sdb;
        SubOpenHelper helper = new SubOpenHelper(this, "memo.db", 1);
        try {
        	//�ǂݏ����\�ŃI�[�v��
        	sdb = helper.getWritableDatabase();
        } catch(SQLiteException e) {
        	//�ُ�I��
        	return;
        }
    	sdb.execSQL("INSERT INTO T_USER (USER_ID, NAME) VALUES ("+cnt+", '"+str+"')");
    	cnt++;
    	sdb.close();
    }
    
    private void DataBaseRemove(int position) {
    	//String item = String.valueOf(position);
    	//Toast toast = Toast.makeText(this, item + cnt, Toast.LENGTH_LONG);
    	//toast.show();
    	
    	SQLiteDatabase sdb;
        SubOpenHelper helper = new SubOpenHelper(this, "memo.db", 1);
        try {
        	//�ǂݏ����\�ŃI�[�v��
        	sdb = helper.getWritableDatabase();
        } catch(SQLiteException e) {
        	//�ُ�I��
        	return;
        }
        
    	sdb.execSQL("delete from T_USER where (USER_ID = '"+position+"');");
    	for(int i = position; i < cnt; i++) {
    		sdb.execSQL("update T_USER set USER_ID = '"+i+"' WHERE (USER_ID = '"+(i + 1)+"');");
    	}
    	cnt--;
    	sdb.close();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/
    
}

class SubOpenHelper extends SQLiteOpenHelper {

	public SubOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sql;
		// �e�[�u�����쐬����
		sql = new StringBuffer();
		sql.append("CREATE TABLE T_USER (");
		sql.append(" USER_ID integer primary key,");
		sql.append(" NAME text NOT NULL");
		sql.append(")");
		db.execSQL(sql.toString());
		
		db.execSQL("INSERT INTO T_USER (USER_ID, NAME) VALUES (0, '�e�X�g���Y')");
		db.execSQL("INSERT INTO T_USER (USER_ID, NAME) VALUES (1, '�e�X�g�Ԏq')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}
	
}

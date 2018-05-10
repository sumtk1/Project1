package com.gloiot.hygounionmerchant.ui.activity.home;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.DialogUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 搜索认证记录(用环游购的实现方法)
 * Created by Dlt on 2017/11/18 15:45
 */
public class NewSearchAuthenticationRecordActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_search_back)
    ImageView mIvSearchBack;
    @Bind(R.id.tv_search_right)
    TextView mTvSearchRight;
    @Bind(R.id.et_search_message)
    EditText mEtSearchMessage;
    @Bind(R.id.list_view)
    ListView mListView;

    private List<String> listDatas = new ArrayList<>(5);
    private CommonAdapter<String> commonAdapter;
    private View viewHead, viewFooter;
    private TextView mTvClearHistory;

    //SQLite
    private SQLiteDatabase sqLiteDatabase;

    @OnClick({R.id.iv_search_back, R.id.tv_search_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_back:
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();
                break;
            case R.id.tv_search_right:

                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_new_search_authentication_record;
    }

    @Override
    public void initData() {

        mEtSearchMessage.addTextChangedListener(textWatcher);
        mEtSearchMessage.requestFocus();

        commonAdapter = new CommonAdapter<String>(this, R.layout.item_search_authentication_record, listDatas) {
            @Override
            public void convert(ViewHolder holder, String str) {
                holder.setText(R.id.tv_search_history, str);
            }
        };

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getAdapter().getItemId(position) > -1) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("jilu", listDatas.get((int) parent.getAdapter().getItemId(position)));
                    sqLiteDatabase.replace("sousuo_jilu", null, contentValues);

//                    Intent intent = new Intent(mContext, FenLeiSonActivity.class);
//                    intent.putExtra("关键字", listDatas.get((int) parent.getAdapter().getItemId(position)));
//                    intent.putExtra("类型", "搜索");
//                    startActivity(intent);

                    finish();
                }
            }
        });

        /**
         * 软键盘搜索监听
         * */
        mEtSearchMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (mEtSearchMessage.getText().toString().trim().length() != 0) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("jilu", mEtSearchMessage.getText().toString().replace(" ", ""));

                        sqLiteDatabase.replace("sousuo_jilu", null, contentValues);

//                        Intent intent = new Intent(mContext, FenLeiSonActivity.class);
//                        intent.putExtra("关键字", mEtSearchMessage.getText().toString().replace(" ", ""));
//                        intent.putExtra("类型", "搜索");
//                        startActivity(intent);
                        finish();
                    } else {
                        mEtSearchMessage.setText("");
                        DialogUtils.oneBtnNormal(mContext, "请输入关键字!");
                    }
                    return true;
                }
                return false;
            }
        });

        //头尾（历史搜索、清空历史记录）
        viewHead = View.inflate(this, R.layout.layout_search_authentication_record_head, null);
        viewFooter = View.inflate(this, R.layout.layout_search_authentication_record_footer, null);
        mTvClearHistory = (TextView) viewFooter.findViewById(R.id.search_history_footer);
        mListView.removeHeaderView(viewHead);
        mListView.removeFooterView(viewFooter);
        mListView.addHeaderView(viewHead);
        mListView.addFooterView(viewFooter);

        mListView.setAdapter(commonAdapter);

        //清空事件监听
        mTvClearHistory.setOnClickListener(this);

        //SQLite
        sqLiteDatabase = openOrCreateDatabase("shang_cheng.db", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE if not exists sousuo_jilu (jilu VARCHAR PRIMARY KEY)");



        //查询获得游标
        Cursor cursor = sqLiteDatabase.rawQuery("select * from sousuo_jilu", null);

        for (int i = cursor.getCount(); i > 0; i--) {
            cursor.moveToPosition(i - 1);
            //获得记录
            String str = cursor.getString(0);
            L.e("-----", str + "----");
            listDatas.add(str);
        }

        commonAdapter.notifyDataSetChanged();
        if (listDatas.size() == 0)
            mListView.removeFooterView(viewFooter);


    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                changeType(false);
//                getShangPinInfo(s.toString());
            } else {
                changeType(true);

                //查询获得游标
                Cursor cursor = sqLiteDatabase.rawQuery("select * from sousuo_jilu", null);
                listDatas.clear();
                while (cursor.moveToNext()) {
                    //获得记录
                    String str = cursor.getString(0);
                    listDatas.add(str);
                }
                commonAdapter.notifyDataSetChanged();

                if (listDatas.size() == 0)
                    mListView.removeFooterView(viewFooter);

            }
        }
    };

    //控制历史搜索、清空历史记录是否显示
    private void changeType(boolean flag) {
        if (flag) {
            mListView.removeHeaderView(viewHead);
            mListView.removeFooterView(viewFooter);
            mListView.addHeaderView(viewHead);
            mListView.addFooterView(viewFooter);
        } else {
            mListView.removeHeaderView(viewHead);
            mListView.removeFooterView(viewFooter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭数据库
        sqLiteDatabase.close();
    }

}

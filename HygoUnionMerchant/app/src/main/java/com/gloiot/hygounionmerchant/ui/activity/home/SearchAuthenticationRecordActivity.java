package com.gloiot.hygounionmerchant.ui.activity.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.db.AuthenticationRecordSearchHistorySQLiteOpenHelper;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.widget.ListViewForScrollView;
import com.zyd.wlwsdk.utils.L;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 搜索认证记录
 * Created by Dlt on 2017/11/18 14:27
 */
public class SearchAuthenticationRecordActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_search_back)
    ImageView mIvSearchBack;
    @Bind(R.id.tv_search_right)
    TextView mTvSearchRight;
    @Bind(R.id.et_search_message)
    EditText mEtSearchMessage;
    @Bind(R.id.tv_search_tip)
    TextView mTvSearchTip;
    @Bind(R.id.list_view)
    ListViewForScrollView mListView;
    @Bind(R.id.tv_clear_search_history)
    TextView mTvClearSearchHistory;

    private AuthenticationRecordSearchHistorySQLiteOpenHelper helper = new AuthenticationRecordSearchHistorySQLiteOpenHelper(this);
    private SQLiteDatabase db;
    private BaseAdapter adapter;

    public static final int RESULT_SEARCH_MESSAGE = 8;

    @Override
    public int initResource() {
        return R.layout.activity_search_authentication_record;
    }

    @Override
    public void initData() {

        // 搜索框的键盘搜索键点击回调
        mEtSearchMessage.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
                    boolean hasData = hasData(mEtSearchMessage.getText().toString().trim());
                    if (!hasData) {
                        insertData(mEtSearchMessage.getText().toString().trim());
                        queryData("");
                    }
                    // TODO 根据输入的内容模糊查询商品，并跳转到另一个界面，由你自己去实现
//                    Toast.makeText(SearchAuthenticationRecordActivity.this, "clicked!", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("searchContent", mEtSearchMessage.getText().toString().trim());
                    SearchAuthenticationRecordActivity.this.setResult(RESULT_SEARCH_MESSAGE, resultIntent);//结果码用于标识返回自哪个Activity
                    SearchAuthenticationRecordActivity.this.finish();

                }
                return false;
            }
        });

        // 搜索框的文本变化实时监听
        mEtSearchMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    mTvSearchTip.setText("搜索历史");
                } else {
                    mTvSearchTip.setText("搜索结果");
                }
                String tempName = mEtSearchMessage.getText().toString().trim();
                // 根据tempName去模糊查询数据库中有没有数据
                queryData(tempName);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                TextView textView = (TextView) view.findViewById(R.id.tv_search_history);
                String name = textView.getText().toString();
//                mEtSearchMessage.setText(name);
//                Toast.makeText(SearchAuthenticationRecordActivity.this, name, Toast.LENGTH_SHORT).show();
                // TODO 获取到item上面的文字，根据该关键字跳转到另一个页面查询，由你自己去实现

                Intent resultIntent = new Intent();
                resultIntent.putExtra("searchContent", name);
                SearchAuthenticationRecordActivity.this.setResult(RESULT_SEARCH_MESSAGE, resultIntent);//结果码用于标识返回自哪个Activity
                SearchAuthenticationRecordActivity.this.finish();

            }
        });

        // 第一次进入查询所有的历史记录
        queryData("");
    }

    @OnClick({R.id.iv_search_back, R.id.tv_search_right, R.id.tv_clear_search_history})
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
                String searchContent = mEtSearchMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(searchContent)) {
                    insertData(searchContent);
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("searchContent", searchContent);
                SearchAuthenticationRecordActivity.this.setResult(RESULT_SEARCH_MESSAGE, resultIntent);//结果码用于标识返回自哪个Activity
                SearchAuthenticationRecordActivity.this.finish();
                break;
            case R.id.tv_clear_search_history:
                deleteData();
                queryData("");
                break;
        }
    }

    /**
     * 插入数据
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 模糊查询数据
     */
    private void queryData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);
        // 创建adapter适配器对象
//        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[]{"name"},
//                new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        adapter = new SimpleCursorAdapter(this, R.layout.item_search_authentication_record, cursor, new String[]{"name"},
                new int[]{R.id.tv_search_history}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 设置适配器
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //判断数据库若为空，不显示搜索历史及清除搜索历史的文字(搜索历史的文字最好保留吧，要不太单调了)
        if (adapter.getCount() == 0) {
//            mTvSearchTip.setVisibility(View.GONE);
            mTvClearSearchHistory.setVisibility(View.GONE);
        } else {
//            mTvSearchTip.setVisibility(View.VISIBLE);
            mTvClearSearchHistory.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 检查数据库中是否已经有该条记录
     */
    private boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name = ? ", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //貌似是多余的
        if (db != null) {
            db.close();
            L.e("onDestroy", "搜索记录数据库关闭");
        }
    }

}

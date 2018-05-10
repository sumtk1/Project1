package com.gloiot.hygounionmerchant.ui.activity.income.shop;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.widget.SelectTimeQuantumPopupWindow;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.utils.StatusBarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;

import static com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.OUTSIDE_CHART;

/**
 * 小铺--统计
 * Created by Dlt on 2017/8/23 10:56
 */
public class StatisticsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_month)
    TextView mTvMonth;
    @Bind(R.id.tv_total_earning)
    TextView mTvTotalEarning;
    @Bind(R.id.tv_shop_income)
    TextView mTvShopIncome;
    @Bind(R.id.tv_amount)
    TextView mTvAmount;
    @Bind(R.id.tl_tabs)
    TabLayout mTlTabs;
    @Bind(R.id.line_chart)
    LineChart mLineChart;
    @Bind(R.id.tv_switcher)
    TextView mTvSwitcher;
    @Bind(R.id.tv_switcher_total)
    TextView mTvSwitcherTotal;
    @Bind(R.id.tv_switcher_daozhang)
    TextView mTvSwitcherDaozhang;
    @Bind(R.id.tv_switcher_jiaoyi)
    TextView mTvSwitcherJiaoyi;
    @Bind(R.id.tv_switcher_total_fuwu)
    TextView mTvSwitcherTotalFuwu;

    private String accountType, shopType;
    private String monthDaozhangIncome, monthJiaoyiIncome, totalIncome, totalAmount;
    private String currentYear, currentMonth, currentDay;

    private SelectTimeQuantumPopupWindow selectWindow;
    private String accountStartYear, accountStartMonth;//账号创建的年月
    private String startDate = "", endDate = "";

    private ArrayList<Entry> yValsDaozhang = new ArrayList<Entry>();
    private ArrayList<Entry> yValsJiaoyi = new ArrayList<Entry>();

    private String currentQueryType = "昨天", currentQueryCondition = "1";//当前查询类别，当前查询条件
    private String currentLineChart = "";//当前显示的折线图（取值：到账/交易）

    private int[] mColors = new int[]{
            Color.parseColor("#21d1c1"),    //绿色
    };

    private String[] mMonths;

    @Override
    public void onResume() {
        super.onResume();

        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR) + "";
        currentMonth = (calendar.get(Calendar.MONTH) + 1) + "";
        currentDay = calendar.get(Calendar.DAY_OF_MONTH) + "";
        L.e("当前日期", currentYear + "-" + currentMonth + "-" + currentDay);

        mTvMonth.setText(currentMonth);

        mTlTabs.getTabAt(0).select();

        requestHandleArrayList.add(requestAction.getAccountTotalIncome(this, "昨天", "1", accountType));
        requestHandleArrayList.add(requestAction.getMonthIncomeLineChart(this, accountType));//月入趋势折线图
        requestHandleArrayList.add(requestAction.getAccountMonthIncome(this, accountType, currentYear, currentMonth));
    }

    @OnClick({R.id.rl_select_month, R.id.tv_detail, R.id.tv_switcher_total, R.id.tv_switcher_income,
            R.id.tv_period, R.id.rl_shop_earning, R.id.tv_switcher, R.id.tv_switcher_daozhang, R.id.tv_switcher_jiaoyi})
    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick()) return;
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_select_month:

                requestHandleArrayList.add(requestAction.getAccountExistYearAndMonth(this, accountType));
                break;
            case R.id.tv_detail:
                intent = new Intent(StatisticsActivity.this, ShopIncomeDetailActivity.class);
//                intent.putExtra("year", currentYear);
//                intent.putExtra("month", currentMonth);

                intent.putExtra("currentQueryType", "月份");
                intent.putExtra("currentQueryCondition", currentYear + "-" + currentMonth);

                startActivity(intent);
                break;
            case R.id.tv_switcher_total://切换选中月份的到账/交易金额
                if (mTvSwitcherTotal.getText().toString().contains("到账")) {
                    mTvSwitcherTotal.setText("本月交易金额（元）");
                    mTvTotalEarning.setText(monthJiaoyiIncome);
                } else if ((mTvSwitcherTotal.getText().toString().contains("交易"))) {
                    mTvSwitcherTotal.setText("本月到账金额（元）");
                    mTvTotalEarning.setText(monthDaozhangIncome);
                }
                break;
            case R.id.tv_switcher_income://小铺收益部分的一个按钮

                break;
            case R.id.tv_period://不用了

                break;
            case R.id.rl_shop_earning://小铺收益
                intent = new Intent(StatisticsActivity.this, ShopIncomeDetailActivity.class);
                intent.putExtra("currentQueryType", currentQueryType);
                intent.putExtra("currentQueryCondition", currentQueryCondition);
                startActivity(intent);
                break;
            case R.id.tv_switcher:
//                initChartView();
//                addDataSet(yValsJiaoyi, "图例");
//                //图标的下边的指示块  图例
//                Legend l = mLineChart.getLegend();
//                l.setEnabled(false);
//                mTvSwitcher.setText("切换到交易金额");
                break;
            case R.id.tv_switcher_daozhang://折线图到账金额

                if (currentLineChart.equals("交易")) {

                    mTvSwitcherDaozhang.setTextColor(getResources().getColor(R.color.blue_449ffb));
                    mTvSwitcherJiaoyi.setTextColor(getResources().getColor(R.color.gray_b4b4b4));

                    initChartView();
                    addDataSet(yValsDaozhang, "图例");
                    //图标的下边的指示块  图例
                    Legend l = mLineChart.getLegend();
                    l.setEnabled(false);

                    currentLineChart = "到账";
                }

                break;
            case R.id.tv_switcher_jiaoyi://折线图交易金额

                if (currentLineChart.equals("到账")) {

                    mTvSwitcherJiaoyi.setTextColor(getResources().getColor(R.color.blue_449ffb));
                    mTvSwitcherDaozhang.setTextColor(getResources().getColor(R.color.gray_b4b4b4));

                    initChartView();
                    addDataSet(yValsJiaoyi, "图例");
                    //图标的下边的指示块  图例
                    Legend l = mLineChart.getLegend();
                    l.setEnabled(false);

                    currentLineChart = "交易";

                }

                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_statistics;
    }

    @Override
    public void initData() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.green_21d1c1), 0);
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        shopType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_SHOPTYPE, "");
        CommonUtils.setTitleBar(this, true, "统计", "");

        if (shopType.equals("商超")) {
            mTvSwitcherTotal.setVisibility(View.VISIBLE);
            mTvSwitcherTotalFuwu.setVisibility(View.GONE);
            mTvSwitcherDaozhang.setVisibility(View.VISIBLE);
            mTvSwitcherJiaoyi.setVisibility(View.VISIBLE);
        } else {
            mTvSwitcherTotal.setVisibility(View.GONE);
            mTvSwitcherTotalFuwu.setVisibility(View.VISIBLE);
            mTvSwitcherDaozhang.setVisibility(View.VISIBLE);
            mTvSwitcherJiaoyi.setVisibility(View.GONE);
        }

        mTlTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                L.e("onTabSelected", "tab:" + tab.toString() + ",tab.position" + tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        requestHandleArrayList.add(requestAction.getAccountTotalIncome(StatisticsActivity.this, "昨天", "1", accountType));
                        currentQueryType = "昨天";
                        currentQueryCondition = "1";
                        break;
                    case 1:
                        requestHandleArrayList.add(requestAction.getAccountTotalIncome(StatisticsActivity.this, "7天", "7", accountType));
                        currentQueryType = "7天";
                        currentQueryCondition = "7";
                        break;
                    case 2:
                        requestHandleArrayList.add(requestAction.getAccountTotalIncome(StatisticsActivity.this, "30天", "30", accountType));
                        currentQueryType = "30天";
                        currentQueryCondition = "30";
                        break;
                    case 3://时间段
                        Point position = CommonUtils.getNavigationBarSize(mContext);//虚拟键适配PopupWindow显示位置
                        selectWindow = new SelectTimeQuantumPopupWindow(mContext, popupWindowOnClick);
                        selectWindow.showAtLocation(mTlTabs, Gravity.BOTTOM, 0, position.y);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void initChartView() {
        mLineChart.setDrawGridBackground(false);
        mLineChart.setDescription(null);    //右下角说明文字
//        mLineChart.setDrawBorders(true);    //四周是不是有边框
        mLineChart.setDrawBorders(false);    //四周是不是有边框
        mLineChart.setBorderWidth(0.5f);
        mLineChart.setBorderColor(Color.parseColor("#b3b3b3"));    //边框颜色，默认黑色？

//        mChart.setVisibleXRangeMaximum(4);

        // enable touch gestures
        mLineChart.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        //禁止x轴y轴同时进行缩放
        mLineChart.setPinchZoom(false);
        // enable scaling and dragging
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(false);//20170927 禁止缩放

        //控制轴上的坐标绘制在什么地方 上边下边左边右边
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setDrawGridLines(false);//不绘制网格线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);    //x轴是在上边显示还是显示在下边
//        xAxis.enableGridDashedLine(10f, 10f, 0f);    //背景用虚线表格来绘制  给整成虚线
//        xAxis.setAxisMinimum(0f);//设置轴的最小值。这样设置将不会根据提供的数据自动计算。          //20170927 注释掉
        xAxis.setGranularityEnabled(true);    //粒度
//        xAxis.setGranularity(1f);    //缩放的时候有用，比如放大的时候，我不想把横轴的月份再细分      //20170927 注释掉

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths[(int) value % mMonths.length];
            }

//            @Override
//            public int getDecimalDigits() {
//                return 0;
//            }
        });
//        xAxis.setAxisLineWidth(0f);    //设置坐标轴那条线的宽度
        xAxis.setDrawAxisLine(false);    //是否显示坐标轴那条轴
        xAxis.setDrawLabels(true);    //是不是显示轴上的刻度
        if (mMonths.length > 1) {
            xAxis.setLabelCount(mMonths.length);    //强制有多少个刻度
        } else {
            xAxis.setLabelCount(1);
        }

        xAxis.setTextColor(Color.parseColor("#b3b3b3"));

        mLineChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setEnabled(false);//不绘制坐标轴
//        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawAxisLine(false);
        //坐标轴绘制在图表的内侧
//        leftAxis.setPosition(INSIDE_CHART);
        leftAxis.setPosition(OUTSIDE_CHART);
        leftAxis.setTextColor(Color.parseColor("#b3b3b3"));
        //确实没看懂这个是干嘛用的，默认是10f
        //这个玩意好像有坐标轴enable的时候是不可用的
        leftAxis.setSpaceBottom(10f);

        //一个chart中包含一个Data对象，一个Data对象包含多个DataSet对象，
        // 每个DataSet是对应一条线上的所有点(相对于折线图来说)
        mLineChart.setData(new LineData());

    }

    private void addDataSet(ArrayList<Entry> entryList, String dataSetName) {

        LineData data = mLineChart.getData();

        if (data != null) {
            int count = data.getDataSetCount();

            LineDataSet set = new LineDataSet(entryList, dataSetName);
//            set.setLineWidth(1.5f);
            set.setLineWidth(2.0f);
            set.setCircleRadius(3.5f);
//            set.setCircleColorHole(Color.parseColor("#3192f6"));
            set.setCircleColorHole(Color.parseColor("#21c1d1"));

            int color = mColors[count % mColors.length];

            set.setColor(color);
            set.setCircleColor(color);
            set.setHighLightColor(color);
            set.setValueTextSize(10f);

            set.setDrawValues(true);    //节点显示具体数值
            set.setValueTextColor(Color.parseColor("#3192f6"));
            set.enableDashedHighlightLine(10f, 5f, 0f);    //选中某个点的时候高亮显示指示线
//            set.setDrawFilled(true);     //填充折线图折线和坐标轴之间
            set.setDrawFilled(false);     //不填充折线图折线和坐标轴之间
            set.setFillColor(color);    //填充可以设置渐变填充一个Drawable，或者仅仅填充颜色
//            set.setAxisDependency(YAxis.AxisDependency.RIGHT);    //如果使用的是右坐标轴必须设置这行

//            set.setDrawVerticalHighlightIndicator(true);//取消纵向辅助线
//            set.setDrawHorizontalHighlightIndicator(true);//取消横向辅助线

            data.addDataSet(set);
            data.notifyDataChanged();
            mLineChart.notifyDataSetChanged();
            //这行代码必须放到这里，这里设置的是图表这个视窗能显示，x坐标轴，从最大值到最小值之间
            //多少段，好像这个库没有办法设置x轴中的间隔的大小
            mLineChart.setVisibleXRangeMaximum(6);
            mLineChart.invalidate();
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener popupWindowOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            selectWindow.dismiss();
            switch (v.getId()) {
                case R.id.iv_close:// 取消

                    selectWindow.dismiss();
                    break;
                case R.id.tv_start_time://开始时间

//                    DialogUtils.towBtnDate(mContext, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            android.widget.DatePicker datePicker = (android.widget.DatePicker) DialogUtils.myDialogBuilder.getCustomView().findViewById(R.id.datePicker);
//                            int month = datePicker.getMonth() + 1;
//                            String date = datePicker.getYear() + "-" + month + "-" + datePicker.getDayOfMonth();
//                            DialogUtils.myDialogBuilder.dismiss();
//                            startDate = date;
//                            selectWindow.setStartTime(date);
//
//                        }
//                    });

                    Calendar calendar = Calendar.getInstance();
                    String calendarYear = calendar.get(Calendar.YEAR) + "";
                    String calendarMonth = (calendar.get(Calendar.MONTH) + 1) + "";
                    String calendarDay = calendar.get(Calendar.DAY_OF_MONTH) + "";

                    DatePicker picker = new DatePicker(StatisticsActivity.this, DatePicker.YEAR_MONTH);
                    picker.setGravity(Gravity.CENTER);//弹框居中
                    picker.setWidth((int) (picker.getScreenWidthPixels() * 0.6));
                    picker.setRangeStart(Integer.parseInt(accountStartYear), Integer.parseInt(accountStartMonth), 1);
                    picker.setRangeEnd(Integer.parseInt(calendarYear), Integer.parseInt(calendarMonth), Integer.parseInt(calendarDay));

                    picker.setSelectedItem(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));

//                    picker.setSelectedItem(2017, 8);
                    picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
                        @Override
                        public void onDatePicked(String year, String month) {
//                            MToast.showToast(mContext, year + "-" + month);

                            startDate = year + "-" + month;
                            selectWindow.setStartTime(startDate);

                        }
                    });
                    picker.show();

                    break;
                case R.id.tv_end_time://结束时间

                    Calendar calendar1 = Calendar.getInstance();
                    String calendarYear1 = calendar1.get(Calendar.YEAR) + "";
                    String calendarMonth1 = (calendar1.get(Calendar.MONTH) + 1) + "";
                    String calendarDay1 = calendar1.get(Calendar.DAY_OF_MONTH) + "";

                    DatePicker picker1 = new DatePicker(StatisticsActivity.this, DatePicker.YEAR_MONTH);
                    picker1.setGravity(Gravity.CENTER);//弹框居中
                    picker1.setWidth((int) (picker1.getScreenWidthPixels() * 0.6));
                    picker1.setRangeStart(Integer.parseInt(accountStartYear), Integer.parseInt(accountStartMonth), 1);
                    picker1.setRangeEnd(Integer.parseInt(calendarYear1), Integer.parseInt(calendarMonth1), Integer.parseInt(calendarDay1));
                    picker1.setSelectedItem(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
//                    picker.setSelectedItem(2017, 8);
                    picker1.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
                        @Override
                        public void onDatePicked(String year, String month) {
//                            MToast.showToast(mContext, year + "-" + month);

                            endDate = year + "-" + month;
                            selectWindow.setEndTime(endDate);
                        }
                    });
                    picker1.show();

                    break;
                case R.id.tv_reset://重置
                    selectWindow.setStartTime("");
                    selectWindow.setEndTime("");
                    break;
                case R.id.tv_complete://完成
                    L.e("比较时间", "startDate" + startDate + ",endDate" + endDate);
                    if (TextUtils.isEmpty(selectWindow.getStartTime())) {
                        MToast.showToast(mContext, "开始时间不能为空");
                    } else if (TextUtils.isEmpty(selectWindow.getEndTime())) {
                        MToast.showToast(mContext, "结束时间不能为空");
                    } else if (CommonUtils.dateCompare(startDate, endDate)) {
                        MToast.showToast(mContext, "结束时间应晚于开始时间");
                    } else {
                        selectWindow.dismiss();
//                        mTabItemTimeQuantum.setTextDirection(accountStartYear+",");
                        //发起请求
                        requestHandleArrayList.add(requestAction.getAccountTotalIncome(StatisticsActivity.this, "时间段", startDate + "," + endDate, accountType));

                        currentQueryType = "时间段";
                        currentQueryCondition = startDate + "," + endDate;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_INCOMETOTALMONEY://收益总金额
                L.e("收益总金额", response.toString());
                totalIncome = response.getString("总月收益到账金额");
                totalAmount = response.getString("总收益到账笔数");
                mTvShopIncome.setText(totalIncome);
                mTvAmount.setText(totalAmount);
                break;
            case RequestAction.TAG_INCOMEMONTHMONEY://收益月份金额
                L.e("收益月份金额", response.toString());
                accountStartYear = response.getString("最早年份");
                accountStartMonth = response.getString("最早月份");
                monthDaozhangIncome = response.getString("本月收益到账总金额");
                monthJiaoyiIncome = response.getString("本月收益交易总金额");
                currentYear = response.getString("年份");
                currentMonth = response.getString("月份");
                if (shopType.equals("商超")) {
                    mTvSwitcherTotal.setVisibility(View.VISIBLE);
                    mTvSwitcherTotalFuwu.setVisibility(View.GONE);
                    mTvSwitcherTotal.setText("本月到账金额(元)");
                }
                mTvTotalEarning.setText(monthDaozhangIncome);
                break;
            case RequestAction.TAG_MONTHINCOMELINECHART://月收益折线图
                L.e("月份收益折线图数据", response.toString());
                yValsDaozhang.clear();
                yValsJiaoyi.clear();
                JSONArray jsonArray = response.getJSONArray("数据");
                int num = jsonArray.length();
                if (num > 0) {
                    mMonths = new String[num];
                    for (int i = 0; i < num; i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        mMonths[i] = jsonObject.getString("递增月份");
                        yValsDaozhang.add(new Entry(i, Float.parseFloat(jsonObject.getString("到账金额"))));
                        yValsJiaoyi.add(new Entry(i, Float.parseFloat(jsonObject.getString("交易金额"))));
                    }
                    initChartView();
                    addDataSet(yValsDaozhang, "图例");
                    //图标的下边的指示块  图例
                    Legend l = mLineChart.getLegend();
                    l.setEnabled(false);

                    currentLineChart = "到账";
                } else {

                }
                break;
            case RequestAction.TAG_QUERYYEARANDMONTH://账号注册以来的年月
                L.e("账号注册以来的年月", response.toString());

                int startYear = Integer.parseInt(response.getString("最早年份"));
                int startMonth = Integer.parseInt(response.getString("最早月份"));

                Calendar calendar = Calendar.getInstance();
                String calendarYear = calendar.get(Calendar.YEAR) + "";
                String calendarMonth = (calendar.get(Calendar.MONTH) + 1) + "";
                String calendarDay = calendar.get(Calendar.DAY_OF_MONTH) + "";

                DatePicker picker = new DatePicker(StatisticsActivity.this, DatePicker.YEAR_MONTH);
//                    picker.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                picker.setGravity(Gravity.CENTER);//弹框居中
                picker.setWidth((int) (picker.getScreenWidthPixels() * 0.6));
                picker.setRangeStart(startYear, startMonth, 1);
                picker.setRangeEnd(Integer.parseInt(calendarYear), Integer.parseInt(calendarMonth), Integer.parseInt(calendarDay));
                picker.setSelectedItem(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
//                    picker.setSelectedItem(2017, 8);
                picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
                    @Override
                    public void onDatePicked(String year, String month) {
//                            MToast.showToast(mContext, year + "-" + month);

                        mTvMonth.setText(Integer.parseInt(month) + "");
                        requestHandleArrayList.add(requestAction.getAccountMonthIncome(StatisticsActivity.this, accountType, year, month));

                        currentYear = year;
                        currentMonth = month;

                        currentQueryType = "月份";
                        currentQueryCondition = year + "-" + month;
                    }
                });
                picker.show();

                break;

        }
    }

}

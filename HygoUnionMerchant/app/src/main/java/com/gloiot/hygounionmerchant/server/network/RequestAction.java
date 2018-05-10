package com.gloiot.hygounionmerchant.server.network;

import android.content.SharedPreferences;

import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zyd.wlwsdk.server.network.HttpManager;
import com.zyd.wlwsdk.server.network.OnDataListener;
import com.zyd.wlwsdk.server.network.utlis.EnDecryptUtlis;
import com.zyd.wlwsdk.server.network.utlis.JsonUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MD5Utlis;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.pulltorefresh.PullToRefreshLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dlt on 2017/8/12 16:18
 */
public class RequestAction {

    public final static int TAG_LOGINTYPE = 0;
    public static String FUNC_LOGINTYPE = "Mlogin_selectType";

    public final static int TAG_USERLOGIN = 1;
    public static String FUNC_USERLOGIN = "Mlogin_login";

    public final static int TAG_SENDYZM = 2;
    public static String FUNC_SENDYZM = "Mlogin_sendMessage";

    public final static int TAG_PREPARERESETLOGINPWD = 3;
    public final String FUNC_PREPARERESETLOGINPWD = "Mlogin_resetPassword";

    public final static int TAG_RESETLOGINPWD = 4;
    public final String FUNC_RESETLOGINPWD = "Mlogin_setNewPassword";

    public final static int TAG_SHOPUPLOADPRODUCT = 5;
    public final String FUNC_SHOPUPLOADPRODUCT = "Mshop_addGoods";

    public final static int TAG_SHOPMYPRODUCTLIST = 6;
    public final String FUNC_SHOPMYPRODUCTLIST = "Mshop_selServiceGoods";

    public final static int TAG_SHOPEDITPRODUCT = 7;
    public final String FUNC_SHOPEDITPRODUCT = "Mshop_editGoods";

    public final static int TAG_SHOPDELETEPRODUCT = 8;
    public final String FUNC_SHOPDELETEPRODUCT = "Mshop_delGoods";

    public final static int TAG_SHOPCOMMODITYCATEGORY = 9;
    public final String FUNC_SHOPCOMMODITYCATEGORY = "Mshop_selGoodsType";

    public final static int TAG_SHOPADDBARCODE = 10;
    public final String FUNC_SHOPADDBARCODE = "Mshop_addBarcode";

    public final static int TAG_SHOPBARCODELIST = 11;
    public final String FUNC_SHOPBARCODELIST = "Mshop_selBarcode";

    public final static int TAG_SHOPEDITBARCODE = 12;
    public final String FUNC_SHOPEDITBARCODE = "Mshop_editBarcode";

    public final static int TAG_SHOPDELETEBARCODE = 13;
    public final String FUNC_SHOPDELETEBARCODE = "Mshop_delBarcode";

    public final static int TAG_SHOPFINDBARCODEMESSAGE = 14;
    public final String FUNC_SHOPFINDBARCODEMESSAGE = "Mshop_barcodeDetails";

    public final static int TAG_SHOPCOMMODITYWAREHOUSINGLIST = 15;
    public final String FUNC_SHOPCOMMODITYWAREHOUSINGLIST = "Mshop_selGoods";

    public final static int TAG_SHOPCOMMODITYWAREHOUSING = 16;
    public final String FUNC_SHOPCOMMODITYWAREHOUSING = "Mshop_addStorage";

    public final static int TAG_SHOPCOMMODITYINFOLIST = 17;
    public final String FUNC_SHOPCOMMODITYINFOLIST = "Mshop_selStock";

    public final static int TAG_SHOPCOMMODITYSOLDOUT = 18;
    public final String FUNC_SHOPCOMMODITYSOLDOUT = "Mshop_offlineStock";

    public final static int TAG_SHOPCOMMODITYSOLDOUTLIST = 19;
    public final String FUNC_SHOPCOMMODITYSOLDOUTLIST = "Mshop_offlineGoods";

    public final static int TAG_SHOPTODAYEARNING = 20;
    public final String FUNC_SHOPTODAYEARNING = "Mincome_todayEarnings";

    public final static int TAG_SHOPJIESUANLIST = 21;
    public final String FUNC_SHOPJIESUANLIST = "Mincome_settleAnnal";

    public final static int TAG_SHOPJIESUANDETAIL = 22;
    public final String FUNC_SHOPJIESUANDETAIL = "Mincome_settleDetails";

    public final static int TAG_SHOPGOSETTLE = 23;
    public final String FUNC_SHOPGOSETTLE = "Mincome_orderSettle";

    public final static int TAG_SHOPSETTLEMENTDETAIL = 24;
    public final String FUNC_SHOPSETTLEMENTDETAIL = "Mincome_settleList";

    public final static int TAG_QUERYYEARANDMONTH = 25;
    public final String FUNC_QUERYYEARANDMONTH = "Mincome_selYearMonth";

    public final static int TAG_INCOMETOTALMONEY = 26;
    public final String FUNC_INCOMETOTALMONEY = "Mincome_selTotleMoney";

    public final static int TAG_INCOMEMONTHMONEY = 27;
    public final String FUNC_INCOMEMONTHMONEY = "Mincome_selMonthMoney";

    public final static int TAG_HOMEDATA = 28;
    public final String FUNC_HOMEDATA = "Mindex_selBanner";

    public final static int TAG_TRAVELANDHOTELMONTHINCOMEDETAIL = 29;
    public final String FUNC_TRAVELANDHOTELMONTHINCOMEDETAIL = "Mincome_selIncomeDetails";

    public final static int TAG_MYACCOUNTINFO = 30;
    public final String FUNC_MYACCOUNTINFO = "Mmy_selMarInfo";

    public final static int TAG_SETACCOUNTPAYPWD = 31;
    public final String FUNC_SETACCOUNTPAYPWD = "Mmy_setPayPassword";

    public final static int TAG_BINDHYGOACCOUNT = 32;
    public final String FUNC_BINDHYGOACCOUNT = "Mmy_bindHYGAccount";

    public final static int TAG_UPDATELOGINORPAYPWD = 33;
    public final String FUNC_UPDATELOGINORPAYPWD = "Mmy_changePassword";

    public final static int TAG_MYACCOUNTINCOME = 34;
    public final String FUNC_MYACCOUNTINCOME = "Mmy_selMoney";

    public final static int TAG_QUERYHYGOACCOUNTINFO = 35;
    public final String FUNC_QUERYHYGOACCOUNTINFO = "Mmy_selHYGAccount";

    public final static int TAG_APPVERSIONINFO = 36;
    public final String FUNC_APPVERSIONINFO = "Mmy_selAppInfo";

    public final static int TAG_EXTRACTBONUSTOHYGO = 37;
    public final String FUNC_EXTRACTBONUSTOHYGO = "Mmy_extracHLAccount";

    public final static int TAG_EXTRACTDETAIL = 38;
    public final String FUNC_EXTRACTDETAIL = "Mmy_extractDetails";

    public final static int TAG_SENDYZMWITHACCOUNT = 39;
    public final String FUNC_SENDYZMWITHACCOUNT = "Mmy_forgetPPGetCode";

    public final static int TAG_PREPARERESETPAYPWD = 40;
    public final String FUNC_PREPARERESETPAYPWD = "Mmy_forgetPayPassword";

    public final static int TAG_RESETPAYPWD = 41;
    public final String FUNC_RESETPAYPWD = "Mmy_setNewPassPwd";

    public final static int TAG_SHOPORDERDETAIL = 42;
    public final String FUNC_SHOPORDERDETAIL = "Mshop_serOrderGoodsList";

    public final static int TAG_SHOPADDPRINTER = 43;
    public final String FUNC_SHOPADDPRINTER = "Mincome_addPrint";

    public final static int TAG_SHOPGETPRINTERINFO = 44;
    public final String FUNC_SHOPGETPRINTERINFO = "Mincome_PrintMessage";

    public final static int TAG_SHOPEDITPRINTER = 45;
    public final String FUNC_SHOPEDITPRINTER = "Mincome_updatePrint";

    public final static int TAG_SUGGESTIONFEEDBACK = 46;
    public final String FUNC_SUGGESTIONFEEDBACK = "Mmy_viewFeedback";

    public final static int TAG_RESETLOGINPWDSTEPONE = 47;
    public final String FUNC_RESETLOGINPWDSTEPONE = "Mlogin_resetPassword";

    public final static int TAG_RESETLOGINPWDSTEPTWO = 48;
    public final String FUNC_RESETLOGINPWDSTEPTWO = "Mlogin_setNewPassword";

    public final static int TAG_UPDATEACCOUNTINFO = 49;
    public final String FUNC_UPDATEACCOUNTINFO = "Mmy_editMarInfo";

    public final static int TAG_MONTHINCOMELINECHART = 50;
    public final String FUNC_MONTHINCOMELINECHART = "Mincome_monthLineChart";

    public final static int TAG_TRAVELANDHOTELORDERDETAIL = 51;
    public final String FUNC_TRAVELANDHOTELORDERDETAIL = "Mincome_orderDetails";

    public final static int TAG_CHECKFORUPDATES = 52;
    public final String FUNC_CHECKFORUPDATES = "Mmy_versionUpdate";

    public final static int TAG_GETQUALIFICATIONINFO = 53;
    public final String FUNC_GETQUALIFICATIONINFO = "Mmy_selQualification";

    public final static int TAG_ZIZHIUPLOADINGPHOTO = 54;
    public final String FUNC_ZIZHIUPLOADINGPHOTO = "Mmy_uploadQualificationImg";

    public final static int TAG_GOCERTIFICATE = 55;
    public final String FUNC_GOCERTIFICATE = "Mmy_addQualification";

    public final static int TAG_ZIZHIWARMPROMPT = 56;
    public final String FUNC_ZIZHIWARMPROMPT = "Mmy_warmPrompt";

    public final static int TAG_BANKLIST = 57;
    public final String FUNC_BANKLIST = "Mmy_bankTypelist";

    public final static int TAG_ADDBANKCARD = 58;
    public final String FUNC_ADDBANKCARD = "Mmy_addbankCard";

    public final static int TAG_MYBANKCARDLIST = 59;
    public final String FUNC_MYBANKCARDLIST = "Mmy_bankCard_list";

    public final static int TAG_REMOVEBANKCARD = 60;
    public final String FUNC_REMOVEBANKCARD = "Mmy_RemovebankCard";

    public final static int TAG_EXTRACTTOBANKCARD = 61;
    //    public final String FUNC_EXTRACTTOBANKCARD = "Mmy_extracHLAccount_tobank";
    public final String FUNC_EXTRACTTOBANKCARD = "Mmy_extracHLAccount";

    public final static int TAG_EXTRACTEXPLAIN = 62;
    public final String FUNC_EXTRACTEXPLAIN = "Mmy_TixianExplain";

    public final static int TAG_OFFLINEGATHERINGRECORD = 63;
    public final String FUNC_OFFLINEGATHERINGRECORD = "Mshop_selOfflineRecord";

    public final static int TAG_OFFLINEGATHERINGRECORDDETAIL = 64;
    public final String FUNC_OFFLINEGATHERINGRECORDDETAIL = "Mshop_OfflineRecordDetail";

    public final static int TAG_AUTHENTICATIONRECORDS = 65;
    public final String FUNC_AUTHENTICATIONRECORDS = "Msth_CertificationRecord";

    public final static int TAG_QUERYPRINCIPALNAME = 66;
    public final String FUNC_QUERYPRINCIPALNAME = "Mmy_selPrincipalName";

    public final static int TAG_BINDINGALIPAYACCOUNT = 67;
    public final String FUNC_BINDINGALIPAYACCOUNT = "Mmy_addZFBAccount";

    public final static int TAG_ALIPAYACCOUNTLIST = 68;
    public final String FUNC_ALIPAYACCOUNTLIST = "Mmy_selZFBAccount";

    public final static int TAG_DELETEALIPAYACCOUNT = 69;
    public final String FUNC_DELETEALIPAYACCOUNT = "Mmy_delZFBAccount";

    public final static int TAG_EXTRACTTOALIPAYACCOUNT = 70;
    public final String FUNC_EXTRACTTOALIPAYACCOUNT = "Mmy_extracZFBAccount";

    public final static int TAG_DEPOSITDETAIL = 71;
    public final String FUNC_DEPOSITDETAIL = "Mmy_selCashDeposit";

    public final static int TAG_PAYTHEDEPOSIT = 72;
    public final String FUNC_PAYTHEDEPOSIT = "Mmy_payCashDeposit";

    public final static int TAG_PAYTHEDEPOSITRECORDS = 73;
//    public final String FUNC_PAYTHEDEPOSITRECORDS = "Mmy_selCashDepositList";
    public final String FUNC_PAYTHEDEPOSITRECORDS = "Mmy_extractDetails";

    public final static int TAG_PAYTHEDEPOSITDETAIL = 74;
    public final String FUNC_PAYTHEDEPOSITDETAIL = "Mmy_cashDepositDetail";

    public final static int TAG_MYEARNINGSLIST = 75;
    public final String FUNC_MYEARNINGSLIST = "Mmy_extractDetailsList";

    public final static int TAG_MYEARNINGSLISTDETAIL = 76;
    public final String FUNC_MYEARNINGSLISTDETAIL = "Mmy_extractListDetails";

    private static HttpManager httpManager = HttpManager.getInstance();
    private static SharedPreferences sp = SharedPreferencesUtils.getInstance().getSharedPreferences();
    // 登录后随机码
    private static String randomCode = sp.getString(ConstantUtils.SP_RANDOMCODE, "");
    //当前版本号（版本名）
    private static String versionName = sp.getString(ConstantUtils.SP_VERSIONNAME, "");
    //商户类别
    private static String accountType = sp.getString(ConstantUtils.SP_ACCOUNTTYPE, "");

    private static RequestParams getParams(String func, HashMap<String, Object> hashMap) {
        randomCode = randomCode.equals("") ? MD5Utlis.Md5(func) : randomCode;
        RequestParams params = new RequestParams();
        params.add("func", func);
        params.add("words", randomCode + EnDecryptUtlis.aesEncrypt(JsonUtils.createJSON(hashMap).toString(), randomCode));
        return params;
    }

    //打印请求信息
    private void showRequstInfo(String tag, HashMap<String, Object> hashMap) {
        Set<Map.Entry<String, Object>> entry = hashMap.entrySet();
        for (Map.Entry<String, Object> e : entry) {
            L.e(tag, e.getKey() + "--" + e.getValue());
        }
    }

    //获取登录类型
    public RequestHandle getLoginType(OnDataListener onDataListener) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_PHONEINFO_JSON));
        showRequstInfo("获取登录类型", hashMap);

        return HttpManager.doPost(TAG_LOGINTYPE, getParams(FUNC_LOGINTYPE, hashMap), onDataListener, 0);
    }

    //登录
    public RequestHandle userLogin(OnDataListener onDataListener, String account, String pwd, String loginType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("随机码", MD5Utlis.Md5(FUNC_USERLOGIN));
        hashMap.put("账号", account);
        hashMap.put("密码", pwd);
        hashMap.put("商户类别", loginType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_PHONEINFO_JSON));
        showRequstInfo("登录", hashMap);

        return HttpManager.doPost(TAG_USERLOGIN, getParams(FUNC_USERLOGIN, hashMap), onDataListener, 0);
    }

    // 发送验证码
    public RequestHandle sendYZM(OnDataListener onDataListener, String account, String phoneNum, String loginType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("随机码", MD5Utlis.Md5(FUNC_SENDYZM));
        hashMap.put("账号", account);
        hashMap.put("手机号", phoneNum);
        hashMap.put("商户类别", loginType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_PHONEINFO_JSON));

        showRequstInfo("发送验证码", hashMap);

        return HttpManager.doPost(TAG_SENDYZM, getParams(FUNC_SENDYZM, hashMap), onDataListener, 0);
    }

    // 准备重设登录密码（忘记登录密码第一步）
    public RequestHandle prepareResetLoginPwd(OnDataListener onDataListener, String phoneNum, String yzm, String loginType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("随机码", MD5Utlis.Md5(FUNC_PREPARERESETLOGINPWD));
        hashMap.put("手机号", phoneNum);
        hashMap.put("验证码", yzm);
        hashMap.put("商户类别", loginType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_PHONEINFO_JSON));

        showRequstInfo("重设登录密码第一步", hashMap);

        return HttpManager.doPost(TAG_PREPARERESETLOGINPWD, getParams(FUNC_PREPARERESETLOGINPWD, hashMap), onDataListener, 0);
    }

    // 重设登录密码（忘记登录密码第二步）
    public RequestHandle resetLoginPwd(OnDataListener onDataListener, String phoneNum, String newPwd, String flag, String loginType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("随机码", MD5Utlis.Md5(FUNC_RESETLOGINPWD));
        hashMap.put("手机号", phoneNum);
        hashMap.put("新密码", newPwd);
        hashMap.put("flag", flag);
        hashMap.put("商户类别", loginType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_PHONEINFO_JSON));

        showRequstInfo("重设登录密码第二步", hashMap);

        return HttpManager.doPost(TAG_RESETLOGINPWD, getParams(FUNC_RESETLOGINPWD, hashMap), onDataListener, 0);
    }

    // 小铺--上传商品（服务类小铺）
    public RequestHandle shopUploadProduct(OnDataListener onDataListener, String picUrl, String goodsName, String marketPrice,
                                           String supplyPrice, String versionCode, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("图片路径", picUrl);
        hashMap.put("商品名称", goodsName);
        hashMap.put("市场价", marketPrice);
        hashMap.put("供货价", supplyPrice);
        hashMap.put("版本号", versionCode);
        hashMap.put("商户类别", accountType);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("服务类小铺上传商品", hashMap);

        return HttpManager.doPost(TAG_SHOPUPLOADPRODUCT, getParams(FUNC_SHOPUPLOADPRODUCT, hashMap), onDataListener, 0);
    }

    //小铺（服务类型）--我的商品列表------------------有分页刷新加载的请求方式(不使用pullToRefreshLayout)
    public RequestHandle shopGetMyGoodsList(OnDataListener onDataListener, int requestType, int page, int requesTag, int showLoad, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("服务类小铺我的商品列表", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_SHOPMYPRODUCTLIST, hashMap), onDataListener, showLoad, requestType);
    }

    //小铺删除商品（服务类小铺）
    public RequestHandle shopDeleteGoods(OnDataListener onDataListener, String goodsId, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商品id", goodsId);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("服务类小铺删除商品", hashMap);

        return HttpManager.doPost(TAG_SHOPDELETEPRODUCT, getParams(FUNC_SHOPDELETEPRODUCT, hashMap), onDataListener, 0);
    }

    //小铺编辑（修改）商品（服务类小铺）
    public RequestHandle shopEditGoods(OnDataListener onDataListener, String picUrl, String goodsTitle, String marketPrice,
                                       String supplyPrice, String goodsId, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("图片路径", picUrl);
        hashMap.put("商品名称", goodsTitle);
        hashMap.put("市场价", marketPrice);
        hashMap.put("供货价", supplyPrice);
        hashMap.put("商品id", goodsId);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("服务类小铺编辑商品", hashMap);

        return HttpManager.doPost(TAG_SHOPEDITPRODUCT, getParams(FUNC_SHOPEDITPRODUCT, hashMap), onDataListener, 0);
    }

    //获取小铺商品类别（查询大类不需要传入id，查询小类传入大类的id）
    public RequestHandle getShopCommodityCategory(OnDataListener onDataListener, String categoryId, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("类别id", categoryId);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取商品类别", hashMap);

        return HttpManager.doPost(TAG_SHOPCOMMODITYCATEGORY, getParams(FUNC_SHOPCOMMODITYCATEGORY, hashMap), onDataListener, 0);
    }

    // 添加条形码(商超类小铺)
    public RequestHandle shopUploadBarCode(OnDataListener onDataListener, String barCode, String bigTypeId, String smallTypeId,
                                           String picUrl, String goodsName, String marketPrice, String supplyPrice, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("条形码", barCode);
        hashMap.put("商品类别", bigTypeId);
        hashMap.put("商品种类", smallTypeId);
        hashMap.put("图片路径", picUrl);
        hashMap.put("商品名称", goodsName);
        hashMap.put("市场价", marketPrice);
        hashMap.put("供货价", supplyPrice);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("添加条码", hashMap);

        return HttpManager.doPost(TAG_SHOPADDBARCODE, getParams(FUNC_SHOPADDBARCODE, hashMap), onDataListener, 0);
    }

    //条码管理------------------改为有分页刷新加载的请求方式(使用pullToRefreshLayout)
    public RequestHandle getBarCodeManagementList(OnDataListener onDataListener, String accountType, PullToRefreshLayout pullToRefreshLayout, int requestType,
                                                  int page, int requesTag, int showLoad) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("商超类小铺条码管理列表", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_SHOPBARCODELIST, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //小铺编辑（修改）商品条形码（商超类小铺）
    public RequestHandle shopEditBarCode(OnDataListener onDataListener, String barCodeId, String bigType, String smallType,
                                         String picUrl, String goodsTitle, String marketPrice, String supplyPrice, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("条形码id", barCodeId);
        hashMap.put("商品类别", bigType);
        hashMap.put("商品种类", smallType);
        hashMap.put("图片路径", picUrl);
        hashMap.put("商品名称", goodsTitle);
        hashMap.put("市场价", marketPrice);
        hashMap.put("供货价", supplyPrice);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("编辑条形码", hashMap);

        return HttpManager.doPost(TAG_SHOPEDITBARCODE, getParams(FUNC_SHOPEDITBARCODE, hashMap), onDataListener, 0);
    }

    //小铺删除条码(商超类小铺)
    public RequestHandle shopDeleteBarCode(OnDataListener onDataListener, String barCodeId, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("条形码id", barCodeId);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("删除条码", hashMap);

        return HttpManager.doPost(TAG_SHOPDELETEBARCODE, getParams(FUNC_SHOPDELETEBARCODE, hashMap), onDataListener, 0);
    }

    // 获取条形码信息
    public RequestHandle getShopBarCodeMessage(OnDataListener onDataListener, String barCode, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("条形码", barCode);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取条码信息", hashMap);

        return HttpManager.doPost(TAG_SHOPFINDBARCODEMESSAGE, getParams(FUNC_SHOPFINDBARCODEMESSAGE, hashMap), onDataListener, 0);
    }

    // 获取小铺商品入库列表
    public RequestHandle getShopCommodityWarehousingList(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("商品入库列表", hashMap);

        return HttpManager.doPost(TAG_SHOPCOMMODITYWAREHOUSINGLIST, getParams(FUNC_SHOPCOMMODITYWAREHOUSINGLIST, hashMap), onDataListener, 0);
    }

    // 小铺商品入库
    public RequestHandle shopCommodityWarehousing(OnDataListener onDataListener, String barCodeId, String bigCategory, String smallCategory,
                                                  String picUrl, String commodityName, String marketPrice, String supplyPrice,
                                                  String purchasePrice, String amount, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("条形码id", barCodeId);
        hashMap.put("商品类别", bigCategory);
        hashMap.put("商品种类", smallCategory);
        hashMap.put("图片路径", picUrl);
        hashMap.put("商品名称", commodityName);
        hashMap.put("市场价", marketPrice);
        hashMap.put("供货价", supplyPrice);
        hashMap.put("进货价", purchasePrice);
        hashMap.put("数量", amount);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("商品入库", hashMap);

        return HttpManager.doPost(TAG_SHOPCOMMODITYWAREHOUSING, getParams(FUNC_SHOPCOMMODITYWAREHOUSING, hashMap), onDataListener, 0);
    }

    //获取小铺商品信息(入库信息)列表--------改为有分页刷新加载的请求方式(使用pullToRefreshLayout)
    public RequestHandle getShopCommodityInfoList(OnDataListener onDataListener, String commodityId, String accountType,
                                                  PullToRefreshLayout pullToRefreshLayout, int requestType, int page, int requesTag, int showLoad) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商品id", commodityId);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("商超类小铺商品信息列表", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_SHOPCOMMODITYINFOLIST, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //小铺商品下架
    public RequestHandle shopSoldOutGoods(OnDataListener onDataListener, String goodsId, String batchNum, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商品id", goodsId);
        hashMap.put("批次号", batchNum);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("商品下架", hashMap);

        return HttpManager.doPost(TAG_SHOPCOMMODITYSOLDOUT, getParams(FUNC_SHOPCOMMODITYSOLDOUT, hashMap), onDataListener, 0);
    }

    // 获取小铺商品下架列表(使用pullToRefreshLayout)
    public RequestHandle getShopCommoditySoldOutList(OnDataListener onDataListener, String commodityId, String accountType,
                                                     PullToRefreshLayout pullToRefreshLayout, int requestType, int page, int requesTag, int showLoad) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商品id", commodityId);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("商品下架列表", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_SHOPCOMMODITYSOLDOUTLIST, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //小铺--今日收益 ------------------有分页刷新加载的请求方式(不使用pullToRefreshLayout)
    public RequestHandle shopTodayEarning(OnDataListener onDataListener, int requestType, int page, int requesTag, int showLoad, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("小铺-今日收益", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_SHOPTODAYEARNING, hashMap), onDataListener, showLoad, requestType);
    }

    // 获取小铺结算列表数据(使用pullToRefreshLayout)
    public RequestHandle getShopJiesuanList(OnDataListener onDataListener, String accountType, PullToRefreshLayout pullToRefreshLayout,
                                            int requestType, int page, int requesTag, int showLoad) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("小铺结算首页", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_SHOPJIESUANLIST, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //获取当前结算数据
    public RequestHandle shopJiesuanDetail(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取当前结算数据", hashMap);

        return HttpManager.doPost(TAG_SHOPJIESUANDETAIL, getParams(FUNC_SHOPJIESUANDETAIL, hashMap), onDataListener, 0);
    }

    //小铺去结算
    public RequestHandle shopGotoSettle(OnDataListener onDataListener, String lasttime, String thisTime, String jiaoyi, String daozhang, String accountType) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("上次结算时间", lasttime);
        hashMap.put("本次结算时间", thisTime);
        hashMap.put("应结金额", jiaoyi);
        hashMap.put("到账金额", daozhang);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取当前结算数据", hashMap);

        return HttpManager.doPost(TAG_SHOPGOSETTLE, getParams(FUNC_SHOPGOSETTLE, hashMap), onDataListener, 0);
    }

    // 获取小铺结算列表中条目明细(使用pullToRefreshLayout)
    public RequestHandle getShopSettlementItemDetail(OnDataListener onDataListener, PullToRefreshLayout pullToRefreshLayout,
                                                     int requestType, int page, int requesTag, int showLoad, String oddNum, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("结算单号", oddNum);
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("小铺列表条目明细", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_SHOPSETTLEMENTDETAIL, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //获取账号注册以来的年月
    public RequestHandle getAccountExistYearAndMonth(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取账号注册以来的年月", hashMap);

        return HttpManager.doPost(TAG_QUERYYEARANDMONTH, getParams(FUNC_QUERYYEARANDMONTH, hashMap), onDataListener, 0);
    }

    //获取账号总收益
    public RequestHandle getAccountTotalIncome(OnDataListener onDataListener, String queryType, String queryCondition, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("查询类别", queryType);//取值：昨天|7天|30天|时间段
        hashMap.put("查询条件", queryCondition);//取值：1|7|30|2017-08,2017-09

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取账号总收益", hashMap);

        return HttpManager.doPost(TAG_INCOMETOTALMONEY, getParams(FUNC_INCOMETOTALMONEY, hashMap), onDataListener, 0);
    }

    //获取账号选中月份收益
    public RequestHandle getAccountMonthIncome(OnDataListener onDataListener, String accountType, String year, String month) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("年份", year);
        hashMap.put("月份", month);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取账号选中月份收益", hashMap);

        return HttpManager.doPost(TAG_INCOMEMONTHMONEY, getParams(FUNC_INCOMEMONTHMONEY, hashMap), onDataListener, 0);
    }

    //获取首页数据
    public RequestHandle getHomeData(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取首页数据", hashMap);

        return HttpManager.doPost(TAG_HOMEDATA, getParams(FUNC_HOMEDATA, hashMap), onDataListener, 0);
    }

    // 获取旅游/酒店月份收益明细(使用pullToRefreshLayout) ----小铺用的也是这个，命名有误
    public RequestHandle getTravelAndHotelMonthIncomeDetail(OnDataListener onDataListener, PullToRefreshLayout pullToRefreshLayout,
                                                            int requestType, int page, int requesTag, int showLoad, String queryType, String queryCondition, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("查询类别", queryType);//取值：昨天|7天|30天|时间段|月份
        hashMap.put("查询条件", queryCondition);//取值：'1|7|30|2017-08,2017-09|2017-09'
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("小铺/旅游/酒店月份收益明细", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_TRAVELANDHOTELMONTHINCOMEDETAIL, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //我的--我的小铺/酒店/旅行社
    public RequestHandle getMyAccountInfo(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("我的小铺/酒店/旅行社", hashMap);

        return HttpManager.doPost(TAG_MYACCOUNTINFO, getParams(FUNC_MYACCOUNTINFO, hashMap), onDataListener, 0);
    }

    //设置账号支付密码
    public RequestHandle setAccountPayPwd(OnDataListener onDataListener, String payPwd, String confirmPwd, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("支付密码", payPwd);
        hashMap.put("确认支付密码", confirmPwd);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("设置账号支付密码", hashMap);

        return HttpManager.doPost(TAG_SETACCOUNTPAYPWD, getParams(FUNC_SETACCOUNTPAYPWD, hashMap), onDataListener, 0);
    }

    //绑定环游购账号
    public RequestHandle bindHygoAccount(OnDataListener onDataListener, String hygoAccount, String phoneNum, String name, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("环游购账户", hygoAccount);
        hashMap.put("手机号", phoneNum);
        hashMap.put("姓名", name);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("绑定环游购账号", hashMap);

        return HttpManager.doPost(TAG_BINDHYGOACCOUNT, getParams(FUNC_BINDHYGOACCOUNT, hashMap), onDataListener, 0);
    }

    //修改登录/支付密码
    public RequestHandle updateLoginOrPayPwd(OnDataListener onDataListener, String pwdType, String originalPwd,
                                             String newPwd, String confirmPwd, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("类别", pwdType);//取值：登录/支付
        hashMap.put("旧密码", originalPwd);
        hashMap.put("新密码", newPwd);
        hashMap.put("确认新密码", confirmPwd);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("修改登录/支付密码", hashMap);

        return HttpManager.doPost(TAG_UPDATELOGINORPAYPWD, getParams(FUNC_UPDATELOGINORPAYPWD, hashMap), onDataListener, 0);
    }

    //我的--我的收益
    public RequestHandle getMyAccountIncome(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("我的收益", hashMap);

        return HttpManager.doPost(TAG_MYACCOUNTINCOME, getParams(FUNC_MYACCOUNTINCOME, hashMap), onDataListener, 0);
    }

    //我的--查询环游购账号信息
    public RequestHandle queryHygoAccountInfo(OnDataListener onDataListener, String hygoAccount, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("环游购账户", hygoAccount);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("我的收益", hashMap);

        return HttpManager.doPost(TAG_QUERYHYGOACCOUNTINFO, getParams(FUNC_QUERYHYGOACCOUNTINFO, hashMap), onDataListener, 0);
    }

    //我的--关于--获取App版本信息
    public RequestHandle getAppVersionInfo(OnDataListener onDataListener, String versionName, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("类别", "商家版");
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取App版本信息", hashMap);

        return HttpManager.doPost(TAG_APPVERSIONINFO, getParams(FUNC_APPVERSIONINFO, hashMap), onDataListener, 0);
    }

    //我的--我的收益--提取积分到环游购
    public RequestHandle extractBonusToHygo(OnDataListener onDataListener, String extractBonus, String payPwd, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("提取积分", extractBonus);
        hashMap.put("支付密码", payPwd);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("提取积分到环游购", hashMap);

        return HttpManager.doPost(TAG_EXTRACTBONUSTOHYGO, getParams(FUNC_EXTRACTBONUSTOHYGO, hashMap), onDataListener, 0);
    }

    // 获取积分提取明细(使用pullToRefreshLayout)
    public RequestHandle getExtractDetail(OnDataListener onDataListener, PullToRefreshLayout pullToRefreshLayout,
                                          int requestType, int page, int requesTag, int showLoad, String accountType, String type) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("类型", type);//取值：银行卡/红利/支付宝
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取积分提取明细", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_EXTRACTDETAIL, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //发送验证码（需要传入账号）
    public RequestHandle sendYzmWithAccount(OnDataListener onDataListener, String phoneNum, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("手机号", phoneNum);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("发送验证码（需要传入账号）", hashMap);

        return HttpManager.doPost(TAG_SENDYZMWITHACCOUNT, getParams(FUNC_SENDYZMWITHACCOUNT, hashMap), onDataListener, 0);
    }

    // 准备重设支付密码（忘记支付密码第一步）
    public RequestHandle prepareResetPayPwd(OnDataListener onDataListener, String phoneNum, String yzm, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("手机号", phoneNum);
        hashMap.put("验证码", yzm);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("准备重设支付密码（忘记支付密码第一步）", hashMap);

        return HttpManager.doPost(TAG_PREPARERESETPAYPWD, getParams(FUNC_PREPARERESETPAYPWD, hashMap), onDataListener, 0);
    }

    // 重设支付密码（忘记支付密码第二步）
    public RequestHandle resetPayPwd(OnDataListener onDataListener, String payPwd, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("支付密码", payPwd);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("重设支付密码（忘记支付密码第二步）", hashMap);

        return HttpManager.doPost(TAG_RESETPAYPWD, getParams(FUNC_RESETPAYPWD, hashMap), onDataListener, 0);
    }

    // 获取小铺订单详情
    public RequestHandle getShopOrderDetail(OnDataListener onDataListener, String orderId, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("订单id", orderId);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取小铺订单详情", hashMap);

        return HttpManager.doPost(TAG_SHOPORDERDETAIL, getParams(FUNC_SHOPORDERDETAIL, hashMap), onDataListener, 0);
    }

    //小铺添加打印机
    public RequestHandle shopAddPrinter(OnDataListener onDataListener, String terminalNum, String secretKey, String gpsCardNum, String gpsSerialNum,
                                        String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("终端号", terminalNum);
        hashMap.put("密钥", secretKey);
        hashMap.put("gps卡号", gpsCardNum);
        hashMap.put("gps序列号", gpsSerialNum);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("小铺添加打印机", hashMap);

        return HttpManager.doPost(TAG_SHOPADDPRINTER, getParams(FUNC_SHOPADDPRINTER, hashMap), onDataListener, 0);
    }

    //获取小铺打印机
    public RequestHandle shopGetPrinter(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        return HttpManager.doPost(TAG_SHOPGETPRINTERINFO, getParams(FUNC_SHOPGETPRINTERINFO, hashMap), onDataListener, 0);
    }

    //小铺更改打印机
    public RequestHandle shopUpdatePrinter(OnDataListener onDataListener, String OriginalTerminalNum, String OriginalSecretKey, String newTerminalNum,
                                           String newSecretKey, String gpsCardNum, String gpsSerialNum, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("原终端号", OriginalTerminalNum);
        hashMap.put("原密匙", OriginalSecretKey);
        hashMap.put("新终端号", newTerminalNum);
        hashMap.put("新密匙", newSecretKey);
        hashMap.put("新GPS卡号", gpsCardNum);
        hashMap.put("新GPS序列号", gpsSerialNum);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("小铺更改打印机", hashMap);

        return HttpManager.doPost(TAG_SHOPEDITPRINTER, getParams(FUNC_SHOPEDITPRINTER, hashMap), onDataListener, 0);
    }

    //意见反馈
    public RequestHandle suggestionFeedback(OnDataListener onDataListener, String suggestionType, String suggestionDetail, String pics,
                                            String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("意见分类", suggestionType);//取值：功能异常/其他问题
        hashMap.put("意见描述", suggestionDetail);
        hashMap.put("反馈图片", pics);//只能一张图片
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("意见反馈", hashMap);

        return HttpManager.doPost(TAG_SUGGESTIONFEEDBACK, getParams(FUNC_SUGGESTIONFEEDBACK, hashMap), onDataListener, 0);
    }

    //忘记登录密码（重置登录密码）第一步 （20170921修改）
    public RequestHandle resetLoginPwdStepOne(OnDataListener onDataListener, String account, String phoneNum, String yzm,
                                              String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("随机码", MD5Utlis.Md5(FUNC_RESETLOGINPWDSTEPONE));
        hashMap.put("账号", account);
        hashMap.put("手机号", phoneNum);
        hashMap.put("验证码", yzm);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_PHONEINFO_JSON));

        showRequstInfo("忘记登录密码（重置登录密码）第一步", hashMap);

        return HttpManager.doPost(TAG_RESETLOGINPWDSTEPONE, getParams(FUNC_RESETLOGINPWDSTEPONE, hashMap), onDataListener, 0);
    }

    //忘记登录密码（重置登录密码）第二步 （20170921修改）
    public RequestHandle resetLoginPwdStepTwo(OnDataListener onDataListener, String account, String phoneNum, String newPwd, String flag,
                                              String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("随机码", MD5Utlis.Md5(FUNC_RESETLOGINPWDSTEPTWO));
        hashMap.put("账号", account);
        hashMap.put("手机号", phoneNum);
        hashMap.put("新密码", newPwd);
        hashMap.put("flag", flag);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_PHONEINFO_JSON));

        showRequstInfo("忘记登录密码（重置登录密码）第一步", hashMap);

        return HttpManager.doPost(TAG_RESETLOGINPWDSTEPTWO, getParams(FUNC_RESETLOGINPWDSTEPTWO, hashMap), onDataListener, 0);
    }

    //编辑账号信息
    public RequestHandle updateAccountInfo(OnDataListener onDataListener, String tupian, String zuoji, String quyu, String dizhi, String jianjie,
                                           String jingdu, String weidu, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户图片", tupian);
        hashMap.put("商户座机", zuoji);
        hashMap.put("商户区域", quyu);
        hashMap.put("商户地址", dizhi);
        hashMap.put("商户简介", jianjie);
        hashMap.put("商户经度", jingdu);
        hashMap.put("商户纬度", weidu);
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("编辑账号信息", hashMap);

        return HttpManager.doPost(TAG_UPDATEACCOUNTINFO, getParams(FUNC_UPDATEACCOUNTINFO, hashMap), onDataListener, 0);
    }

    //获取月份收益折线图数据
    public RequestHandle getMonthIncomeLineChart(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取月份收益折线图数据", hashMap);

        return HttpManager.doPost(TAG_MONTHINCOMELINECHART, getParams(FUNC_MONTHINCOMELINECHART, hashMap), onDataListener, 0);
    }

    //旅行社/酒店的订单详情
    public RequestHandle getTravelAndHotelOrderDetail(OnDataListener onDataListener, String tupian, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("订单id", tupian);
        hashMap.put("商户类别", accountType);//取值：旅行社/酒店
        hashMap.put("版本号", versionName);
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("旅行社/酒店的订单详情", hashMap);

        return HttpManager.doPost(TAG_TRAVELANDHOTELORDERDETAIL, getParams(FUNC_TRAVELANDHOTELORDERDETAIL, hashMap), onDataListener, 0);
    }

    //检查更新
    public RequestHandle checkForUpdates(OnDataListener onDataListener, String versionName, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("类别", "商家版");
        hashMap.put("版本号", versionName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("检查更新", hashMap);

        return HttpManager.doPost(TAG_CHECKFORUPDATES, getParams(FUNC_CHECKFORUPDATES, hashMap), onDataListener, 0);
    }

    //获取资质认证信息
    public RequestHandle getQualificationInfo(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取资质认证信息", hashMap);

        return HttpManager.doPost(TAG_GETQUALIFICATIONINFO, getParams(FUNC_GETQUALIFICATIONINFO, hashMap), onDataListener, 0);
    }

    //资质认证---上传照片
    public RequestHandle uploadingZizhiPhoto(OnDataListener onDataListener, String accountType, HashMap<String, Object> hashMap) {

        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("资质认证---上传照片", hashMap);

        return HttpManager.doPost(TAG_ZIZHIUPLOADINGPHOTO, getParams(FUNC_ZIZHIUPLOADINGPHOTO, hashMap), onDataListener, 0);
    }

    //去资质认证
    public RequestHandle goCertificate(OnDataListener onDataListener, String accountType, String name, String IDNum, String front, String back, String shouchi,
                                       String farenFront, String farenBack, String farenShouchi, String zhizhaoPic, String xukezhengPics) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("姓名", name);
        hashMap.put("身份证号码", IDNum);
        hashMap.put("商户负责人身份证正面", front);
        hashMap.put("商户负责人身份证反面", back);
        hashMap.put("商户负责人手持身份证照片", shouchi);
        hashMap.put("商户法人身份证正面", farenFront);
        hashMap.put("商户法人身份证反面", farenBack);
        hashMap.put("商户法人手持身份证照片", farenShouchi);
        hashMap.put("商户营业执照照片", zhizhaoPic);
        hashMap.put("商户行业许可证照片", xukezhengPics);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("去资质认证", hashMap);

        return HttpManager.doPost(TAG_GOCERTIFICATE, getParams(FUNC_GOCERTIFICATE, hashMap), onDataListener, 0);
    }

    //资质认证---温馨提示
    public RequestHandle getZizhiWarmPrompt(OnDataListener onDataListener, String accountType, HashMap<String, Object> hashMap) {

        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("资质认证---温馨提示", hashMap);

        return HttpManager.doPost(TAG_ZIZHIWARMPROMPT, getParams(FUNC_ZIZHIWARMPROMPT, hashMap), onDataListener, 0);
    }

    //获取银行名称数据
    public RequestHandle getBankList(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取银行名称数据", hashMap);

        return HttpManager.doPost(TAG_BANKLIST, getParams(FUNC_BANKLIST, hashMap), onDataListener, 0);
    }

    //添加银行卡
    public RequestHandle addBankcard(OnDataListener onDataListener, String accountType, String bankName, String bankId, String branchName, String cardNum, String cardType,
                                     String personName, String personIDNum, String phoneNum) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("银行名称", bankName);
        hashMap.put("银行id", bankId);
        hashMap.put("开户行名称", branchName);
        hashMap.put("银行卡号", cardNum);
        hashMap.put("卡类型", cardType);
        hashMap.put("持卡人姓名", personName);
        hashMap.put("身份证号码", personIDNum);
        hashMap.put("手机号", phoneNum);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("添加银行卡", hashMap);

        return HttpManager.doPost(TAG_ADDBANKCARD, getParams(FUNC_ADDBANKCARD, hashMap), onDataListener, 0);
    }

    //获取已绑定的银行卡列表
    public RequestHandle getMyBankcardList(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取已绑定的银行卡列表", hashMap);

        return HttpManager.doPost(TAG_MYBANKCARDLIST, getParams(FUNC_MYBANKCARDLIST, hashMap), onDataListener, 0);
    }

    //解绑银行卡
    public RequestHandle removeBankcard(OnDataListener onDataListener, String accountType, String bankName, String cardId, String cardNum, String payPwd) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("银行名称", bankName);
        hashMap.put("银行卡id", cardId);
        hashMap.put("银行卡号", cardNum);
        hashMap.put("支付密码", payPwd);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("解绑银行卡", hashMap);

        return HttpManager.doPost(TAG_REMOVEBANKCARD, getParams(FUNC_REMOVEBANKCARD, hashMap), onDataListener, 0);
    }

    //提现到银行卡
    public RequestHandle extractToBankcard(OnDataListener onDataListener, String accountType, String tiquMoney, String payPwd, String cardNum,
                                           String bankName, String personName) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("提取积分", tiquMoney);
        hashMap.put("支付密码", payPwd);
        hashMap.put("银行卡号", cardNum);
        hashMap.put("银行名", bankName);
        hashMap.put("银行卡姓名", personName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("提现到银行卡", hashMap);

        return HttpManager.doPost(TAG_EXTRACTTOBANKCARD, getParams(FUNC_EXTRACTTOBANKCARD, hashMap), onDataListener, 0);
    }

    //提现说明
    public RequestHandle getExtractExplain(OnDataListener onDataListener, String type, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("通道", type);//取值：支付宝/银行卡

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("提现说明", hashMap);

        return HttpManager.doPost(TAG_EXTRACTEXPLAIN, getParams(FUNC_EXTRACTEXPLAIN, hashMap), onDataListener, 0);
    }

    // 获取线下收款记录(使用pullToRefreshLayout)
    public RequestHandle getOfflineGatheringRecords(OnDataListener onDataListener, PullToRefreshLayout pullToRefreshLayout, int requestType, int page,
                                                    int requesTag, int showLoad, String startTime, String endTime, String orderType, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("开始时间", startTime);//取值eg--：2017-11-12
        hashMap.put("结束时间", endTime);//取值eg--：2017-11-13
        hashMap.put("订单类别", orderType);//取值：扫码支付/套餐购买(线下收款记录（非商超） 传套餐购买,收款记录（商超） 传扫码支付)
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取线下收款记录", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_OFFLINEGATHERINGRECORD, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //（某个订单）线下收款详情
    public RequestHandle getOfflineGatheringRecordDetail(OnDataListener onDataListener, String dingdanId, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("订单id", dingdanId);
//        hashMap.put("订单类别", orderType);//取值：扫码支付/套餐购买(线下收款记录 传扫码支付,收款记录 传套餐购买)

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("线下收款详情", hashMap);

        return HttpManager.doPost(TAG_OFFLINEGATHERINGRECORDDETAIL, getParams(FUNC_OFFLINEGATHERINGRECORDDETAIL, hashMap), onDataListener, 0);
    }

    // 获取认证记录(使用pullToRefreshLayout)
    public RequestHandle getAuthenticationRecords(OnDataListener onDataListener, PullToRefreshLayout pullToRefreshLayout,
                                                  int requestType, int page, int requesTag, int showLoad, String searchContent, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("搜索框内容", searchContent);
        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取认证记录", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_AUTHENTICATIONRECORDS, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //获取负责人姓名
    public RequestHandle getPrincipalName(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取负责人姓名", hashMap);

        return HttpManager.doPost(TAG_QUERYPRINCIPALNAME, getParams(FUNC_QUERYPRINCIPALNAME, hashMap), onDataListener, 0);
    }

    //绑定支付宝账号
    public RequestHandle bindingAlipayAccount(OnDataListener onDataListener, String alipayAccount, String alipayName, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("支付宝账号", alipayAccount);
        hashMap.put("支付宝姓名", alipayName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("绑定支付宝账号", hashMap);

        return HttpManager.doPost(TAG_BINDINGALIPAYACCOUNT, getParams(FUNC_BINDINGALIPAYACCOUNT, hashMap), onDataListener, 0);
    }

    //获取已绑定的支付宝账号列表
    public RequestHandle getAlipayAccountList(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取已绑定的支付宝账号列表", hashMap);

        return HttpManager.doPost(TAG_ALIPAYACCOUNTLIST, getParams(FUNC_ALIPAYACCOUNTLIST, hashMap), onDataListener, 0);
    }

    //解绑支付宝账号
    public RequestHandle removeAlipayAccount(OnDataListener onDataListener, String alipayAccount, String payPwd, String id, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("支付宝账号", alipayAccount);
        hashMap.put("支付密码", payPwd);
        hashMap.put("id", id);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("解绑支付宝账号", hashMap);

        return HttpManager.doPost(TAG_DELETEALIPAYACCOUNT, getParams(FUNC_DELETEALIPAYACCOUNT, hashMap), onDataListener, 0);
    }

    //提取到支付宝账号
    public RequestHandle extractToAlipayAccount(OnDataListener onDataListener, String tiquJifen, String alipayAccount, String alipayName, String payPwd, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("提取积分", tiquJifen);
        hashMap.put("支付宝账号", alipayAccount);
        hashMap.put("支付宝姓名", alipayName);
        hashMap.put("支付密码", payPwd);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("提取到支付宝账号", hashMap);

        return HttpManager.doPost(TAG_EXTRACTTOALIPAYACCOUNT, getParams(FUNC_EXTRACTTOALIPAYACCOUNT, hashMap), onDataListener, 0);
    }

    //获取保证金细节/详情
    public RequestHandle getDepositDetail(OnDataListener onDataListener, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取保证金细节/详情", hashMap);

        return HttpManager.doPost(TAG_DEPOSITDETAIL, getParams(FUNC_DEPOSITDETAIL, hashMap), onDataListener, 0);
    }

    //去缴纳保证金
    public RequestHandle payTheDeposit(OnDataListener onDataListener, String payMoney, String payPwd, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("缴纳金额", payMoney);
        hashMap.put("支付密码", payPwd);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("去缴纳保证金", hashMap);

        return HttpManager.doPost(TAG_PAYTHEDEPOSIT, getParams(FUNC_PAYTHEDEPOSIT, hashMap), onDataListener, 0);
    }

    // 获取保证金缴纳记录(使用pullToRefreshLayout)
    public RequestHandle getPayTheDepositRecords(OnDataListener onDataListener, PullToRefreshLayout pullToRefreshLayout,
                                                 int requestType, int page, int requesTag, int showLoad, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("类型", "保证金");

        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取保证金缴纳记录", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_PAYTHEDEPOSITRECORDS, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //缴纳保证金详情
    public RequestHandle getPayTheDepositDetail(OnDataListener onDataListener, String id, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("id", id);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("缴纳保证金详情", hashMap);

        return HttpManager.doPost(TAG_PAYTHEDEPOSITDETAIL, getParams(FUNC_PAYTHEDEPOSITDETAIL, hashMap), onDataListener, 0);
    }

    // 获取我的收益列表数据(使用pullToRefreshLayout)
    public RequestHandle getMyEarningsList(OnDataListener onDataListener, PullToRefreshLayout pullToRefreshLayout,
                                           int requestType, int page, int requesTag, int showLoad, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);

        if (page > 0) {
            hashMap.put("页数", page + "");
        } else {
            hashMap.put("页数", 1 + "");
        }
        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("获取我的收益列表数据", hashMap);

        return HttpManager.doPost(requesTag, getParams(FUNC_MYEARNINGSLIST, hashMap), onDataListener, showLoad, pullToRefreshLayout, requestType);
    }

    //我的收益列表条目详情
    public RequestHandle getMyEarningsListDetail(OnDataListener onDataListener, String type, String id, String accountType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("商户类别", accountType);
        hashMap.put("版本号", versionName);
        hashMap.put("类别", type);//保证金 银行卡 支付宝
        hashMap.put("id", id);

        hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_REQUESTINFO_JSON));

        showRequstInfo("我的收益列表条目详情", hashMap);

        return HttpManager.doPost(TAG_MYEARNINGSLISTDETAIL, getParams(FUNC_MYEARNINGSLISTDETAIL, hashMap), onDataListener, 0);
    }

}

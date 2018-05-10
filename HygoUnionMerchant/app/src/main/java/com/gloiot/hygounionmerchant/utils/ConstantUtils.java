package com.gloiot.hygounionmerchant.utils;

/**
 * Created by Dlt on 2017/8/12 16:36
 */
public class ConstantUtils {
    public static String BASEURL = "http://121.201.67.222:12110";//测试

//    public static String BASEURL = "https://other.zhenxuanzhuangyuan.com:8030";//正式

//    public static String BASEURL = "https://other.zhenxuanzhuangyuan.com:8050";//正式地址---内部测试用

    public static String URL = BASEURL + "/api.post";
    public static final String MESSAGE_SERVER_URL = BASEURL;


    // 微信Appid
    public static String WXAPPID = "wx8c55439fc255912a";

    //阿里云信息
//    public static final String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
//    public static final String accessKeyId = "ynjAwXSJsm6tHvbW";
//    public static final String accessKeySecret = "lnrN4oWq90GPEXZnmarHj8HOgfQFVe";
//    public static final String aliyunBucketName = "zykshop";
//    public static final String aliyunPath1 = "http://zykshop.qqjlb.cn/";

    //2017.06.17更改
    public static final String endpoint = "http://oss.glo-iot.com";
    public static final String accessKeyId = "LTAILX1dJyVDfG7W";
    public static final String accessKeySecret = "porFTxA7VyZOM2nlxHCJw47DaSF2jk";
    public static final String aliyunBucketName = "qqwlw";
    //class首的aliyunBucketName更改
    public static final String aliyunPath1 = "http://qqwlw.oss-cn-shenzhen.aliyuncs.com/";


    // 当前版本号
    public static String VERSION = "1.5.0";

    // 当前版本名
    public static String SP_VERSIONNAME = "1.5.0";

    // SharedPreferences存储空间
    public static String MYSP = "HygoUnionMerchantInfo";
    //引导页是否展示过
    public static String SP_ISGUIDEPAGESHOWED = "ISGUIDEPAGESHOWED";
    // 手机信息json格式
    public static String SP_PHONEINFO_JSON = "SP_PHONEINFO_JSON";
    // 请求信息json格式(包括账号，随机码，手机id，手机型号，手机名称)
    public static String SP_REQUESTINFO_JSON = "SP_REQUESTINFO_JSON";
    // 手机高度
    public static String SP_PHONEHEIGHT = "SP_PHONEHEIGHT";
    // 手机ID
    public static String SP_PHENEID = "SP_PHENEID";
    // 手机信息kv格式
    public static String SP_PHONEINFO_KV = "SP_PHONEINFO_KV";
    // 用户随机码
    public static String SP_RANDOMCODE = "SP_RANDOMCODE";
    //    // 请求信息json格式(包括账号，随机码，手机id，手机型号，手机名称)
//    public static String SP_INFO_JSON = "SP_INFO_JSON";
    // 账号认证状态
    public static String SP_AUTHENTIFICATIONSTATE = "SP_AUTHENTIFICATIONSTATE";
    //账号登录状态
    public static String SP_LOGINSTATE = "SP_LOGINSTATE";
    //用户支付密码状态（已设置/未设置）
    public static String SP_ACCOUNTPAYPWDSTATE = "SP_ACCOUNTPAYPWDSTATE";
    //用户账号
    public static String SP_USERACCOUNT = "SP_USERACCOUNT";
    //账号昵称
    public static String SP_ACCOUNTNICKNAME = "SP_ACCOUNTNICKNAME";
    //账号名称（商户名称）
    public static String SP_ACCOUNTNAME = "SP_ACCOUNTNAME";
    //账号头像
    public static String SP_ACCOUNTPORTRAIT = "SP_ACCOUNTPORTRAIT";
    //和环游购绑定状态
    public static String SP_BINDINGHYGOSTATE = "SP_BINDINGHYGOSTATE";
    //账号类型
    public static String SP_ACCOUNTTYPE = "SP_ACCOUNTTYPE";
    //登录类型
    public static String SP_LOGINTYPE = "SP_LOGINTYPE";
    //小铺类别
    public static String SP_SHOPTYPE = "SP_SHOPTYPE";
    //身份类别（负责人/员工）
    public static String SP_IDENTITYTYPE = "SP_IDENTITYTYPE";
    // 小铺是否需要补全商品信息(以此确定是否显示补全信息)
    public static String SP_SHOPISNEEDTOCOMPLETECOMMODITYINFO = "SP_SHOPISNEEDTOCOMPLETECOMMODITYINFO";
    //产品管理地址
    public static String SP_PRODUCTMANAGEMENTURL = "SP_PRODUCTMANAGEMENTURL";
    //产品上传
    public static String SP_PRODUCTUPLOADURL = "SP_PRODUCTUPLOADURL";
    //订单管理
    public static String SP_ORDERMANAGEMENTURL = "SP_ORDERMANAGEMENTURL";
    //商品入库地址
    public static String SP_COMMODITYWAREHOUSINGURL = "SP_COMMODITYWAREHOUSINGURL";
    //用户管理地址
    public static String SP_USERMANAGEMENTURL = "SP_USERMANAGEMENTURL";
    //输入认证地址
    public static String SP_INPUTCODERENZHENGURL = "SP_INPUTCODERENZHENGURL";
    //认证记录地址
    public static String SP_RENZHENGRECORDURL = "SP_RENZHENGRECORDURL";
    //收款记录地址
    public static String SP_GATHERINGRECORDURL = "SP_GATHERINGRECORDURL";
    //认证详情地址
    public static String SP_RENZHENGDETAILURL = "SP_RENZHENGDETAILURL";
    //二维码地址
    public static String SP_QRCODEURL = "SP_QRCODEURL";//用于判断扫描的二维码是否符合要求，防止跳到其他App的服务器(不要这一项了，用输码认证的地址)
    //账号区域-省
    public static String SP_ACCOUNTSHENG = "SP_ACCOUNTSHENG";
    //账号区域-市
    public static String SP_ACCOUNTSHI = "SP_ACCOUNTSHI";
    //账号区域-区
    public static String SP_ACCOUNTQU = "SP_ACCOUNTQU";
    //账号区域-是否重置
    public static String SP_ISACCOUNTAREARESET = "SP_ISACCOUNTAREARESET";
    //账号区域-纬度
    public static String SP_ACCOUNTLATITUDE = "SP_ACCOUNTLATITUDE";
    //账号区域-经度
    public static String SP_ACCOUNTLONGITUDE = "SP_ACCOUNTLONGITUDE";
    //账号区域-经纬度是否重置
    public static String SP_ISACCOUNTLATLNGRESET = "SP_ISACCOUNTLATLNGRESET";
    //账号区域-位置信息详细描述
    public static String SP_ACCOUNTLOCATIONDETAILINFO = "SP_ACCOUNTLOCATIONDETAILINFO";
    //环游购账号
    public static String SP_HYGOACCOUNT = "SP_HYGOACCOUNT";
    //资质认证状态
    public static String SP_ZIZHIRENZHENGSTATE = "SP_ZIZHIRENZHENGSTATE";
    //环游购姓名
    public static String SP_HYGONAME = "SP_HYGONAME";
    //环游购手机号
    public static String SP_HYGOPHONENUM = "SP_HYGOPHONENUM";

    // 支付返回类型
    public static String SP_PAYTYPE = "SP_PAYTYPE";

    //是否是从登录页进入主页
    public static String SP_ISFROMLOGINTOMAIN = "SP_ISFROMLOGINTOMAIN";

}

package com.lib.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.andybrier.lib.R.string;
import com.lib.LibraryApplication;


public class UrlUtils {

    public static String HOST_OPAC = "http://opac.lib.xjtlu.edu.cn/opac/";
    public static String HOST = "http://opac.lib.xjtlu.edu.cn/";

    public final static String HOST_OPAC_ZH = "http://opac.lib.xjtlu.edu.cn/cn/opac/";
    public final static String HOST_ZH = "http://opac.lib.xjtlu.edu.cn/cn/";

    public final static String HOST_OPAC_EN = "http://opac.lib.xjtlu.edu.cn/opac/";
    public final static String HOST_EN = "http://opac.lib.xjtlu.edu.cn/";


    private static final String SEARCH_URL =
            HOST_OPAC
                    + "openlink.php?strSearchType={0}&historyCount=1&strText={1}"
                    + "&doctype=ALL&match_flag=any&displaypg=20&sort=CATA_DATE&orderby=desc&showmode=list&dept=ALL&with_ebook=on";

    private static final String SEARCH_URL_NEW="http://opac.lib.xjtlu.edu.cn/opac/search_adv_result.php?sType0={0}&q0={1}";

    private static final String DOUBAN_SEARCH_API = "http://api.douban.com/v2/book/isbn/";

    private static final String SEARCH_MORE =
            HOST_OPAC
                    + "openlink.php?dept=ALL&{0}={1}"
                    + "&doctype=ALL&with_ebook=on&lang_code=ALL&match_flag=any&displaypg=20&showmode=list&orderby=DESC&sort=CATA_DATE&onlylendable=no&"
                    + "&page={2}";
    /**
     * http://202.119.47.8:8080/opac/userpreg_result.php?marc_no=0003794862&count=1&preg_days1=31&
     * take_loca1=941++&callno1=TP312%2FH33%3A1151%281%29&location1=941++&pregKeepDays1=7&check=1
     */
    // 查询预约情况

    private static final String LOGIN_URL = "http://opac.lib.xjtlu.edu.cn/cn/reader/redr_verify.php";
//    private static final String LOGIN_URL = "http://opac.lib.xjtlu.edu.cn/reader/redr_verify.php";



    // 预约图书
    private static final String DORESERV =
            HOST_OPAC_ZH
                    + "userpreg_result.php?{0}&count=1&preg_days1=42&take_loca1={1}++&{2}={3}&{4}={5}&pregKeepDays1=3&check=1";

    public static String getSearchURL(String content, String type) {
  //      String searchUrl = MessageFormat.format(SEARCH_URL, type, content);
        String searchUrl = MessageFormat.format(SEARCH_URL_NEW, type, content);
//        if(type.compareTo("isbn")==0){
//            String isbn="http://opac.lib.xjtlu.edu.cn/cn/opac/openlink.php?strSearchType=isbn&match_flag=forward&historyCount=1&strText={0}&doctype=ALL&with_ebook=on&displaypg=20&showmode=list&sort=CATA_DATE&orderby=desc&dept=ALL";
//            searchUrl = MessageFormat.format(isbn,content);
//        }
//      http://opac.lib.xjtlu.edu.cn/cn/opac/openlink.php?strSearchType=title&match_flag=any&historyCount=1&strText=php&doctype=ALL&with_ebook=on&displaypg=20&showmode=list&sort=CATA_DATE&orderby=desc&dept=ALL
        LogUtils.log("SearchUrl", searchUrl);
        return searchUrl;
    }

    /**
     * 从豆瓣中查找图书
     *
     * @param content
     * @param type
     * @return
     */
    public static String getDoubanSearchURL(String isbn) {
        String searchUrl = DOUBAN_SEARCH_API + isbn;
        LogUtils.log("DoubanSearchUrl", searchUrl);
        return searchUrl;
    }

    public static String getPageURL(String content, String type, int count, int page) {
        return MessageFormat.format(SEARCH_MORE, type, content, page);
    }

    /**
     * 返回书详细信息：http://opac.lib.xjtlu.edu.cn/cn/opac/item.php?marc_no=0000132313
     *
     * @param bookUrl item.php?marc_no=0000132313
     * @return
     */
    public static String getBookDetailUtr(String bookUrl) {
        return HOST_OPAC + bookUrl;
    }

    /**
     * 获取读者预约信息
     * http://202.119.47.8:8080/opac/userpreg.php?marc_no=0003786538
     *
     * @return
     */
    public static String getBookLocationUrl(String maroUrl) {
        return HOST_OPAC + "userpreg.php?" + maroUrl.split("\\?", 2)[1];
    }

    /**
     * @param macoUrl    marc_no=0003794862
     * @param take_loca1 941++
     * @param callno1    : TP312%2FH33%3A1151%281%29
     * @param location1: 941++
     * @return 获取预约图书地址
     */
    public static String getReservUrl(String macoUrl, String take_loca1, String callno_no,
                                      String callno_value, String location_no, String location_value) {
        return MessageFormat.format(DORESERV, macoUrl.split("\\?", 2)[1], take_loca1.trim(), callno_no,
                callno_value, location_no, location_value);
    }

    /**
     * 查询本人的预约信息
     *
     * @return
     */
    public final static String getPregUrl() {

        return HOST + "reader/preg.php";

    }

    /**
     * 取消预约
     *
     * @param callNo
     * @param macroUrl
     * @return
     * @throws UnsupportedEncodingException
     */
    public final static String getCancelReserveUrl(String callNo, String macroUrl, String loca) {


        String now = String.valueOf(System.currentTimeMillis());
        String uriString = HOST_ZH + "reader/ajax_preg.php?call_no={0}&{1}&loca={3}%20%20&time={2}";
        try {
            return MessageFormat.format(uriString, java.net.URLEncoder.encode(callNo, "UTF-8"), macroUrl, now, loca);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String getLoginUrl() {
        return LOGIN_URL;
    }

    /**
     * 获取当前借阅信息
     *
     * @return
     */
    public final static String getMyLendUrl() {
        return HOST + "reader/book_lst.php";
    }

    /**
     * 获取续借的url
     *
     * @param barcode
     * @return
     */
    public final static String getRenewUrl(String barcode,String verifycode,String checkcode) {

        String now = String.valueOf(System.currentTimeMillis());
        String string = HOST_ZH + "reader/ajax_renew.php?bar_code={0}&time={1}&captcha={2}&check={3}";
        return MessageFormat.format(string, barcode, now,verifycode,checkcode);

    }

    /**
     * 借阅证挂失
     *
     * @param cardNo
     * @return
     */
    public static String getLostCardUrl() {
        return HOST + "reader/redr_lost_result.php";
    }


    /**
     * 借阅证挂失
     *
     * @param cardNo
     * @return
     */
    public static String getGoLostCardUrl() {
        return HOST_ZH + "reader/redr_lost.php";
    }


    /**
     * 读者推荐 http://opac.lib.xjtlu.edu.cn/cn/asord/asord_redr_con.php?b_name=+%E9%A2%98%E3%80%80%E5%90%
     * 8D&a_name
     * =%E8%B4%A3%E4%BB%BB%E8%80%85&b_type=U&b_pub=%E5%87%BA%E7%89%88%E7%A4%BE&b_date=%E5%87%BA
     * %E7%89%88%E5%B9%B4&b_isbn=SBN
     *
     * @param reason
     * @param cardNo
     * @return
     */
    public static String getRecommonUrl(String name, String author, String publisher,
                                        String pushlisherTime, String isbn, String reason) {

        String rec =
                HOST
                        + "asord/asord_redr_con.php?b_name={0}&a_name={1}&b_type=U&b_pub={2}&b_date={3}&b_isbn={4}";

        return MessageFormat.format(rec, name, author, publisher, pushlisherTime, isbn);
    }


    /**
     * 我的借阅历史
     *
     * @return
     */
    public static String getMyLendHistory() {
        return HOST + "reader/book_hist.php";
    }

    /**
     * 我的借阅历史
     *
     * @return
     */
    public static String getMyLendHistoryPaging(int page) {
        final String format = HOST + "reader/book_hist.php?page={0}";
        return MessageFormat.format(format, page);
    }


    /**
     * 获取会议室信息
     *
     * @param buildingname
     * @param m_year
     * @param m_month
     * @param m_day
     * @return
     */
    public static String getRoomStatusUrl() {
        String url = "http://222.92.148.165:8080/mbl/Json/GetRoomInfoJson.action";

        return url;
    }

    /**
     * 预订会议室
     *
     * @return
     */
    public static String getOrderRoomUrl() {
        return "http://222.92.148.165:8080/mbl/Json/OrderRoomJson.action";
    }

    /**
     * 学校新闻
     *
     * @param language
     * @param startPage
     * @return
     */
    public static String getLoadNewsUrl(int cata) {
        String format = null;
        switch (cata) {
            case 0: {
                format = "http://222.92.148.165:8080/mbl/Json/GetNews";
                break;
            }
            case 1: {
                format = "http://222.92.148.165:8080/mbl/Json/GetMediaNews";
                break;
            }
            case 2: {
                format = "http://222.92.148.165:8080/mbl/Json/GetHuoDongNews";
                break;
            }
            case 3: {
                format = "http://222.92.148.165:8080/mbl/Json/GetLibNews";
                break;
            }

            default:
                break;
        }
        return format;
    }


    /**
     * 获取预订状态
     *
     * @return
     */
    public static String getRoomBookInfo() {
        return "http://222.92.148.165:8080/mbl/Json/GetOrderInfoJson.action";
    }

    /**
     * 取消预约
     *
     * @return
     */
    public static String getDelRoomBookInfo() {
        return "http://222.92.148.165:8080/mbl/Json/UndoOrderJson.action";
    }

    /**
     * 校园电话
     *
     * @return
     */
    public static String getContactUrl() {

        if (!LanguageUtil.isChinese()) {
            return "http://222.92.148.165:8080/mbl/contact.html";
        } else {
            return "http://222.92.148.165:8080/mbl/contact_cn.html";
        }
    }

    /**
     * 西普概况
     *
     * @return
     */
    public static String getSchoolInfoUrl() {

        if (!LanguageUtil.isChinese()) {
            return "http://222.92.148.165:8080/mbl/schoolInfo.html";
        } else {
            return "http://222.92.148.165:8080/mbl/schoolInfo_cn.html";

        }
    }

    /**
     * 西普地图
     *
     * @return
     */
    public static String getSchoolMapUrl() {
        if (!LanguageUtil.isChinese())

        {
            return "http://222.92.148.165:8080/mbl/schoolMap.html";
        } else {
            return "http://222.92.148.165:8080/mbl/schoolMap_cn.html";

        }
    }


    /**
     * 获取个人信息
     *
     * @return
     */
    public static String getUserInfoUrl() {
        return HOST + "reader/redr_info.php";
    }

    public static String getEbscoUrl() {
        return "http://222.92.148.165:8080/mbl/proxy";
    }

    public static String getTrainPlanUrl() {
        return "http://libcal.lib.xjtlu.edu.cn/embed_mini_calendar.php?mode=month&cal_id=2272&l=5";
    }

    public static String getLibguildUrl() {
        return "http://libguides.lib.xjtlu.edu.cn";
    }

    public static String getTrainResUrl() {
        return "http://libguides.lib.xjtlu.edu.cn/content.php?pid=439940&sid=3601768";
    }

    public static String getverifyCode(){
        return "http://opac.lib.xjtlu.edu.cn/reader/captcha.php";
    }

}

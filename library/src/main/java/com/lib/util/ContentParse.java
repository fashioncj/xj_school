package com.lib.util;

import com.lib.activity.exception.BookReservationException;
import com.lib.activity.library.LendHistory;
import com.lib.activity.news.News;
import com.lib.activity.room.Data;
import com.lib.activity.room.adaptor.RoomBookingInfo;
import com.lib.bean.lib.Book;
import com.lib.bean.lib.BookDetailInfo;
import com.lib.bean.lib.BookReserveInfo;
import com.lib.bean.lib.GetBookLoction;
import com.lib.bean.lib.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ContentParse implements ReserveConstants {


    public static final String MAO = ": ";


    /**
     * 解析书籍馆藏详细
     *
     * @param content
     * @return
     */
    public static BookDetailInfo parseBookDetail(String content) {

        BookDetailInfo info = new BookDetailInfo();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        info.setLocationInfo(list);

        Document doc = Jsoup.parse(content);

        // 解析出题名 作者 出版社的信息
        Elements elements = doc.getElementsByClass("booklist");
        for (int i = 0; i < elements.size(); i++) {
            final String dt = elements.get(i).select("dt").get(0).html();
            String dd = elements.get(i).select("dd").get(0).html();

            if (dt.equals("题名/责任者:") || dt.equals("Title / Responsible:")) {

                String text = elements.get(i).select("dd").get(0).text();

                int index = text.lastIndexOf("/");

                String name = null;
                String author = "";
                if (index != -1) {

                    name = text.substring(0, index).trim();

                    if (text.endsWith(".") || text.endsWith("。")) {

                        author = text.substring(index + 1, text.length() - 1).trim();
                    } else {
                        author = text.substring(index + 1, text.length()).trim();
                    }
                } else {
                    name = text;
                }
                info.setName(name);
                info.setAuth(author);
            } else if (dt.equals("出版发行项:") || dt.equals("Publication,Distribution,etc.:")) {
                info.setPress(dd);
            } else if (dt.equals("ISBN:")) {
                info.setIsbn(dd);
            } else if (dt.equals("电子资源:") || dt.equals("Electronic Location and Access:")) {
                info.setElec(elements.get(i).select("a").get(0).html().toString());
            }
        }

        Element trss = doc.getElementById("item");
        //Elements trs = doc.select("tr");
        Elements trs = trss.getElementsByTag("tr");
        int totalTrs = trs.size();

        if (totalTrs > 0) {

            for (int i = 0; i < totalTrs - 1; i++) {

                Elements tds = trs.get(i + 1).select("td");
                int totalTds = tds.size();

                Map<String, Object> map = new HashMap<String, Object>();

                String bookInfo = "";
                String addressAndStatus = "";

                int addressStart = 0;
                if (totalTds == 6) {
                    // zhongwen
                    addressStart = 4;

                } else {
                    // english
                    addressStart = 3;
                }
                for (int j = 0; j < totalTds; j++) {

                    String text = tds.get(j).html().toString();
                    if (text.contains("<img")) {
                        break;
                    }

                    if (j == 0) {
                        bookInfo = tds.get(j).html().toString();
                    } else if (j == 1) {
                        bookInfo = bookInfo + " " + tds.get(j).html().toString();
                        map.put("info", bookInfo);
                    } else if (j == addressStart) {
                        addressAndStatus = tds.get(j).html().toString();
                    } else if (j == addressStart + 1) {
                        String status = "";
                        if (tds.get(j).select("font").size() == 0) {
                            status = tds.get(j).html().toString();
                        } else {
                            status = tds.get(j).select("font").get(0).html().toString();
                        }
                        addressAndStatus = addressAndStatus.replaceAll("&nbsp","").replaceAll(";","") + " " + status;
                        map.put("address", addressAndStatus);
                    }

                }
                list.add(map);
            }
        }
        return info;
    }

    /**
     * 解析书籍馆藏详细
     *
     * @param content
     * @return
     */
    public static List<BookReserveInfo> parseBookReserve(String content)
            throws BookReservationException {

        Document doc = Jsoup.parse(content);
        // 获取书个数
        Elements numbers = doc.select("input");
        if (numbers == null || numbers.size() == 0 || numbers.get(1).attr("value").equals("0")) {
            Pattern p = Pattern.compile("(?<=<font color=\"red\">).*(?=</font>)");
            Matcher matcher = p.matcher(content);
            if (matcher.find()) {
                throw new BookReservationException(matcher.group());
            }

            //如果是挂失情况
            p = Pattern.compile("(?<=<strong class=\"red\">).*(?=</strong>)");
            matcher = p.matcher(content);
            if (matcher.find()) {
                throw new BookReservationException(doc.select("#content li").get(2).html().toString());

            }
        }


        List<BookReserveInfo> list = new LinkedList<BookReserveInfo>();


        Elements trs = doc.select("tr");
        int totalTrs = trs.size();

        if (totalTrs > 0) {

            for (int i = 0; i < totalTrs - 1; i++) {

                Elements tds = trs.get(i + 1).select("td");
                int totalTds = tds.size();

                BookReserveInfo info = new BookReserveInfo();
                List<GetBookLoction> getBookLocation = new LinkedList<GetBookLoction>();
                info.setGetBookLocation(getBookLocation);

                // 获取索引号、馆藏地、取书地
                if (totalTds < 5) {
                    continue;
                }

                for (int j = 0; j < totalTds; j++) {
                    switch (j) {
                        // 索引号
                        case 0:
                            info.setCallNo(tds.get(j).html().toString());
                            break;
                        // 馆藏地
                        case 1:
                            info.setLocation(tds.get(j).html().toString());
                            break;

                        // 是否可以预约
                        case 5: {
                            if (tds.get(j).select("input") != null && tds.get(j).select("input").size() > 0) {
                                info.setCanReserve(null);
                            } else {
                                info.setCanReserve(tds.get(j).select("font").get(0).html());
                            }
                            break;
                        }
                        // 取书地
                        case 6: {
                            Elements elements = tds.get(j).select("option");
                            for (int m = 0; m < elements.size(); m++) {
                                GetBookLoction loction = new GetBookLoction();
                                getBookLocation.add(loction);
                                loction.setLocation(elements.get(m).html().toString());
                                loction.setLocationCode(elements.get(m).attr("value"));
                                loction.setBookReserveInfo(info);
                            }
                            break;
                        }
                    }
                }

                // 获取其它信息
                Elements inputtds = trs.get(i + 1).select("input");
                int totalInputs = inputtds.size();
                for (int k = 0; k < totalInputs; k++) {
                    if (inputtds.get(k).attr("name").contains("callno")) {
                        info.setHiddenCallNoName(inputtds.get(k).attr("name"));
                        info.setHiddenCallNoValue(inputtds.get(k).attr("value"));
                    } else if (inputtds.get(k).attr("name").contains("location")) {
                        info.setHiddenLocationName(inputtds.get(k).attr("name"));
                        info.setHidenLocationValue(inputtds.get(k).attr("value"));
                    }
                }

                list.add(info);
            }
        }
        return list;
    }

    public static boolean parseLogin(String content) {

        Document doc = Jsoup.parse(content);

        Elements tds = doc.select("td");
        int totalTds = tds.size();
        if (totalTds == 2) {
            return true;
        }
        return false;
    }

    public static boolean parseReserveResult(String content) {

        Document doc = Jsoup.parse(content);
        Elements ps = doc.select("p");
        int totalPs = ps.size();
        if (totalPs == 1) {
            return true;
        }
        return false;
    }

    /**
     * 解析我的预约信息
     *
     * @param content
     * @return
     */
    public static void parsePregInfo(String content, List<Map<String, String>> list) {


        if (content == null) {
            return;
        }

        list.clear();

        Document doc = Jsoup.parse(content);
        Elements trs = doc.select("tr");
        int totalTrs = trs.size();

        if (totalTrs > 0) {

            for (int i = 0; i < totalTrs - 1; i++) {

                Element tr = trs.get(i + 1);
                Elements tds = tr.select("td");
                int totalTds = tds.size();

                Map<String, String> map = new HashMap<String, String>();

                for (int j = 0; j < totalTds; j++) {

                    switch (j) {
                        case 0: {
                            map.put("local_code", tds.get(j).html().toString());
//                            map.put("local_code_in", tds.get(j).select("input").attr("value").toString());
                            map.put("local_code_in", tds.get(j).html().toString());

                            break;
                        }
                        case 1:
                            map.put("reserve_name", tds.get(j).select("a").get(0).html().toString());
                            map.put("marc_no", tds.get(j).select("a").attr("href").toString());
                            tds.get(j).select("a").remove();
                            map.put("author", tds.get(j).html().toString().replace("/", ""));
                            break;
//                        case 2: {
//                            map.put("author", tds.get(j).html().toString());
//                            break;
//                        }
                        case 3:
                            map.put("reserve_day", tds.get(j).html().toString());
                            break;
                        case 4:
                            map.put("end_day", tds.get(j).html().toString());
                            break;
                        case 5:
                            map.put("book_local", tds.get(j).html().toString());
                        case 6:
                            map.put("reserve_status", tds.get(j).html().toString());
                            break;
                    }
                }

                // 获取loca

                // onclick="javascript:getInLib('0000102418','CN/PL2837.E35./FAL/9','1','00011');"
                try{
                    String locaString = tr.select("input").get(1).attr("onclick");
                    int last = locaString.lastIndexOf("'");
                    int secondLast = locaString.lastIndexOf("'", last - 1);
                    String loca = locaString.substring(secondLast + 1, last);
                    map.put("loca", loca);
                }catch (Exception e){
                    map.put("loca", "");
                }
                list.add(map);
            }
        }
    }

    /**
     * 解析取消预约结果
     *
     * @param content
     * @return
     */
    public static boolean parseCancelReserve(String content) {
        Document doc = Jsoup.parse(content);
        Elements fonts = doc.select("font");
        String sCancelColor = fonts.get(0).attr("color");

        if (sCancelColor.equals("green")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 解析我的借阅信息
     *
     * @param myLendings
     * @return
     */
    public static ArrayList<Map<String, String>> parseMyLending(String myLendings) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

        Document doc = Jsoup.parse(myLendings);
        Elements list_doc=doc.getElementsByClass("table_line");
        Elements trs = list_doc.select("tr");
        //Elements trs = doc.select("tr");

        int totalTrs = trs.size();

        if (totalTrs > 0) {
            for (int i = 0; i < totalTrs - 1; i++) {

                Elements tds = trs.get(i + 1).select("td");
                int totalTds = tds.size();

                Map<String, String> map = new HashMap<String, String>();

                for (int j = 0; j < totalTds; j++) {

                    switch (j) {
                        case 0:
                            map.put("lend_code", tds.get(j).html().toString());
                            break;
                        case 1:
                            String tempstring=tds.get(j).select("a").get(0).html().toString();
                            tds.get(j).select("a").remove();
                            map.put("lend_name", tempstring);
                            break;
                        case 2:
                            map.put("lend_day", tds.get(j).html().toString());
                            break;
                        case 3:
                            map.put("back_day", tds.get(j).select("font").get(0).html().toString());
                            break;
                        case 4:
                            map.put("lend_times", tds.get(j).html().toString());
                            break;
                        case 5:
                            map.put("location", tds.get(j).html().toString());
                            break;
                    }
                }
                String tempcheckcode=trs.get(i + 1).getElementsByTag("input").attr("onclick");
                map.put("check_code",tempcheckcode.split("'")[3]);
                list.add(map);
            }
        }

        return list;
    }

    /**
     * 获取续借的结果
     *
     * @param content
     * @return
     */
    public final static String parseRenewResult(String content) {

        Document doc = Jsoup.parse(content);
        Elements ps = doc.select("font");
        String sRenew = ps.get(0).html().toString();

        return sRenew;

    }

    /**
     * 解析挂失的结果
     *
     * @param result
     * @return
     */
    public static String parseLostResult(String result) {


        Document doc = Jsoup.parse(result);
        Elements ps = doc.getElementsByClass("iconerr");

        if (ps != null && ps.size() == 1) {
            return ps.get(0).html().toString();
        }
        return null;


    }

    /**
     * 解析借阅历史
     */
    public static int parseLendHistory(final String result,
                                       final ArrayList<Map<String, Object>> list, final LendHistory lendHistory) {

        list.clear();

        if (result == null) {
            return 0;
        }

        Document doc = Jsoup.parse(result);
        Elements trs = doc.select("tr");
        int totalTrs = trs.size();

        // trs 数值大于 0时 表示存在列表
        if (totalTrs <= 0) {
            return 0;
        }

        // 获取结果页数
        int page = 1;
        Elements options = doc.select("option");
        if (options == null || options.size() == 0) {
            page = 1;
        } else {
            page = Integer.parseInt(options.get(options.size() - 1).html().toString());
        }
        LogUtils.log("MaxPage", "" + page);


        for (int i = 0; i < totalTrs - 1; i++) {

            Elements tds = trs.get(i + 1).select("td");
            int totalTds = tds.size();

            Map<String, Object> map = new HashMap<String, Object>();

            for (int j = 0; j < totalTds; j++) {

                switch (j) {
                    // 条形码
                    case 1:
                        map.put("isbn", tds.get(j).html().toString());
                        break;
                    // 题名
                    case 2:
                        map.put("maco", tds.get(j).select("a").attr("href").toString());
                        map.put("name", tds.get(j).select("a").get(0).html().toString());
                        break;
                    case 3:
                        map.put("auther", lendHistory.getAuthor() + MAO + tds.get(j).html().toString());
                        break;
                    case 4:
                        map.put("lend_date", lendHistory.getLendDate() + MAO + tds.get(j).html().toString());
                        break;
                    case 5:
                        map.put("return_date", lendHistory.getReturnDate() + MAO + tds.get(j).html().toString());
                        break;
                    case 6:
                        map.put("location", lendHistory.getLocation() + MAO + tds.get(j).html().toString());
                        break;
                }
            }
            list.add(map);
        }
        return page;
    }

    public static String parseIsbnSearch(String result) {

        String macroUrl = null;

        Document doc = Jsoup.parse(result);
        Elements trs = doc.select("tr");

        if (trs.size() >= 2) {
            Element tr = trs.get(1);

            Element td = tr.select("td").get(1);

            macroUrl = td.select("a").attr("href").toString();
        }

        return macroUrl;
    }

    /**
     * 解析豆瓣API查询图书的结果
     *
     * @param result
     * @return
     */
    public static Book parseDoubanIsbnSearch(String result) {

        if (StringUtils.isEmpty(result)) {
            return null;
        }

        LogUtils.log("Douban search by isbn result", result);

        Book book = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);

            book = new Book();

            book.setTitle(jsonObject.getString("title"));
            book.setPub_year(jsonObject.getString("pubdate"));
            book.setPublisher(jsonObject.getString("publisher"));
            JSONArray array = jsonObject.getJSONArray("author");
            book.setAuthor(array.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return book;
    }

    public static void parseRoomStatus(String content) {

        if (content == null) {
            content =
                    "{\"roomInfoList\": [ " + "{ " + " \"roomName\": \"114(21)\" ,"
                            + " \"roomState\": \"0000100001000000000000000\"" + " } ," + " { "
                            + "   \"roomName\": \"115(21)\"," + " \"roomState\": \"0000000000000000000000000\" "
                            + "}]}";
        }


        LogUtils.log("RoomContent", content);

        Data.buildAllRoomStatus.clear();
        try {
            JSONObject jsonObject = new JSONObject(content);

            JSONArray array = jsonObject.getJSONArray("roomInfoList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);

                Data.buildAllRoomStatus.put(object.getString("roomID"), object.getString("roomState"));
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    public static List<News> parserNews(String content) {

        List<News> retList = new LinkedList<News>();

        if (content == null) {
            return retList;
        }

        LogUtils.log("School news", content);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(content);
            JSONArray array = jsonObject.getJSONArray("News");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);

                String summary = object.getString("summary");
                News news = new News(object.getString("title"), object.getString("date"));
                news.setUrl(object.getString("url"));
                news.setImageUrl(object.getString("imageUrl"));

                if (summary.equals("null")) {
                    summary = "";
                }
                news.setSummer(summary);

                LogUtils.log("news", news.toString());
                retList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retList;
    }

    public static String parseOrderResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            return jsonObject.getString("orderResult");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    /**
     * 解析room预订信息
     * <p/>
     * {"entryInfoList":[{"meetingDesc":"18021307982","meetingID":"73700","meetingName":"模联",
     * "meetingRoom":"Room: Public Areas in Building 1 - 1st Floor Artium","meetingTime":
     * "Tuesday 16 April 2013 08:00:00 - 14 hours"
     * ,"meetingType":"Internal","meetingUser":"weiwei.jiang"}
     * ,{"meetingDesc":"13895005091","meetingID":"73871","meetingName":"haoyu.feng","meetingRoom":
     * "Room: Sports Area Booking (Outdoors) - B4+ Tennis Court 1"
     * ,"meetingTime":"Tuesday 16 April 2013 15:00:00 - 2 hours"
     * ,"meetingType":"Internal","meetingUser":"weiwei.jiang"}
     *
     * @param content
     * @param list
     * @return
     */
    public static void parserRoomBook(String content, List<RoomBookingInfo> list) {

        list.clear();
        if (content == null) {
            return;
        }
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(content);
            JSONArray array = jsonObject.getJSONArray("entryInfoList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);

                RoomBookingInfo info = new RoomBookingInfo();
                info.setDesc(object.getString("meetingDesc"));
                info.setId(object.getString("meetingID"));
                info.setName(object.getString("meetingName"));
                info.setRoom(object.getString("meetingRoom"));
                info.setTime(object.getString("meetingTime"));
                list.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String parserDelRoomBook(String content) {
        try {
            JSONObject jsonObject = new JSONObject(content);

            return jsonObject.getString("undoResult");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    public static Person parserUserInfo(String content) {
        Person p = new Person();
        if (content == null) {
            return p;
        }


        Document doc = Jsoup.parse(content);

        Elements tds = doc.select("td");
        int totalTds = tds.size();

        final String emailTag = "Email";
        for (int j = 0; j < totalTds; j++) {

            final String tdcontent = tds.get(j).html().toString();
            if (tdcontent.contains(emailTag)) {
                p.setEmail(tdcontent.split("span>")[1]);

                LogUtils.log("Found email", p.getEmail());
            }

        }
        return p;
    }

    public static String parseGoLostResult(String _result) {
        Document doc = Jsoup.parse(_result);
        Elements ps = doc.getElementsByClass("iconerr");

        if (ps != null && ps.size() == 1) {
            return ps.get(0).html().toString();
        }
        return null;

    }
}

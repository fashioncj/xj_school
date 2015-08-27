package com.lib.activity.room;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.lib.activity.news.News;



public class Data {

  public final static Map<String, String[]> roomData = new LinkedHashMap<String, String[]>();

  static {

    String rooms1[] = {"137", "173", "1E", "1W", "2E", "2S", "2W", "3E", "3W", "4E", "4W"};
    roomData.put("Lecture Theatres in Foundation Building", rooms1);

    String rooms3[] =
        {"305", "307", "314", "316", "321", "328", "329", "332", "336", "337", "340", "341", "342",
            "362", "364", "365", "368", "369", "372", "376", "377", "385", "388", "392", "395",
            "397"};
    roomData.put("3F Classrooms in Foundation Building", rooms3);

    String rooms9[] = {"264", "265", "269", "272", "277", "288"};
    roomData.put("2F Classrooms in Foundation Building", rooms9);

    String rooms13[] =
        {"247", "451 (With Projector)", "453", "6A??642With Projector)", "6B(644 With Projector)",
            "804 (With Projector)", "811"};
    roomData.put("Meeting Rooms in Foundation Building", rooms13);

    String rooms14[] =
        {"1st Floor Artium", "2nd Floor Artium", "5th Floor Artium", "6th Floor Artium"};
    roomData.put("Public Areas in Foundation Building", rooms14);

    String rooms15[] =
        {"B1 Basketball Court 1", "B1 Basketball Court 2", "B4+ Basketball Court 1",
            "B4+ Basketball Court 2", "B4+ Tennis Court 1", "B4+ Tennis Court 2"};
    roomData.put("Sports Area Booking (Outdoors)", rooms15);

    String rooms17[] = {"459", "468", "563", "564", "572"};
    roomData.put("Computer Rooms in Foundation Building", rooms17);

    String rooms22[] = {"SA231", "SC231", "SD263", "SD267", "SD528"};
    roomData.put("Meeting Rooms in Science Building", rooms22);

    String rooms23[] = {"SC375", "SC379", "SD319", "SD323", "SD546", "SD554"};
    roomData.put("Computer Rooms in Science Building", rooms23);

    String rooms24[] =
        {"114", "115", "116", "121", "125", "132", "136", "140", "141", "168", "172", "181", "185",
            "192", "193"};
    roomData.put("1F Classrooms in Foundation Building", rooms24);

    String rooms26[] =
        {"SB219", "SB221", "SB252", "SC236", "SC279", "SC454", "SC464", "SD219", "SD223", "SD254",
            "SD354"};
    roomData.put("Classrooms in Science Building", rooms26);

    String rooms27[] =
        {"SA136", "SA164", "SA169", "SA236", "SB102", "SB123", "SB152", "SB220", "SB230", "SC140",
            "SC162", "SC169", "SC176", "SC262", "SD102", "SD114", "SD120", "SD154", "SD214",
            "SD220"};
    roomData.put("Lecture Theatres in Science Building", rooms27);

    String rooms32[] = {"MCQ Reader 1 (Ouma)", "MCQ Reader 2 ( NHII )"};
    roomData.put("Registry MCQ Reader on 8th floor in Central Building", rooms32);

    String rooms33[] =
        {"Block SA 1F West Corridor", "Block SA 2F West Corridor", "Block SB 1F East Corridor",
            "Block SC 1F West Corridor", "Block SD 1F East Corridor"};
    roomData.put("Public Areas in Science Building", rooms33);

    String rooms34[] =
        {"BB122", "BB124", "BB126", "BB341", "BB441", "BB541", "EB415", "EB519", "EB565", "EE505"};
    roomData.put("Meeting Rooms in Business&Engineering&Public Building", rooms34);

    String rooms35[] =
        {"BA104", "BA105", "BA116", "BA204", "BA205", "BA218", "BA305", "BA318", "BA405", "BA418",
            "EB111", "EB115", "EB119", "EB138", "EB233", "EB237", "EB275", "EB277", "EE110",
            "EE114", "EE118", "EE122", "P214", "P216", "P402", "P418"};
    roomData.put("Lecture Theatres in Business&Engineering&Public Building", rooms35);

    String rooms36[] = {"P201", "P202", "P215", "P218", "P221", "P223", "P321"};
    roomData.put("Classrooms in Public Building", rooms36);

    String rooms37[] =
        {"BA505", "BA512", "BA518", "EB441", "EB447", "EE305", "EE309", "EE311", "P310", "P318"};
    roomData.put("Computer Rooms in Business&Engineering&Public Building", rooms37);

    String rooms39[] =
        {"EB331", "EB347", "EB352", "EB356", "EB360", "EB409", "EB475", "EB476", "EB480", "EB509"};
    roomData.put("Classrooms in Engineering Building (EB)", rooms39);

    String rooms40[] =
        {"BA109", "BA209", "BA309", "BA409", "BB119", "BB123", "BB137", "BB139", "BB237", "BB241"};
    roomData.put("Classrooms in Business Building", rooms40);

    String rooms43[] =
        {"B2 Table-tennis Area", "B4+ Table-tennis Area 1", "B4+Table-tennis Area 2"};
    roomData.put("Staff Table-tennis Booking", rooms43);

    String rooms44[] =
        {"Table-tennis No1-G15", "Table-tennis No2-G15", "Table-tennis No3-G15",
            "Table-tennis No4-G15", "Table-tennis No5-G15", "Table-tennis No6-G15",
            "Table-tennis No7-G15", "Table-tennis No8-G15"};
    roomData.put("Table-tennis Booking", rooms44);

    String rooms45[] =
        {"Sports Activity Room1P020", "Sports Activity Room2P020", "Sports Activity Room3P020",
            "Sports Activity Room4-G12", "Sports Activity Room5-G01"};
    roomData.put("Sports Area Booking (Indoors)", rooms45);

    String rooms49[] =
        {"102E (No Projector)", "1053 (No Projector)", "1055 (No Projector)",
            "1056D(No Projector)", "1103 (With Projector)", "1105 (With Projector)",
            "111E (No Projector)", "113E (No Projector)", "117W", "1217 (With Projector)",
            "1226 (With Projector)", "1242(With Projector)", "1261 (With Projector)",
            "257(No Projector)", "847(No Projector)", "955(With Projector)", "959(With Projector)"};
    roomData.put("Meeting Rooms in Central Building", rooms49);

    String rooms50[] =
        {"1106 (With Projector)", "1113 (With Projector)", "1115 (With Projector)",
            "1117 (With Projector)", "303(With Projector)"};
    roomData.put("Training Rooms in Central Building", rooms50);

    String rooms51[] = {"North Sports Ground(Half)", "South Sports Ground(Half)"};
    roomData.put("South Campus Sports Ground Booking", rooms51);

    String rooms52[] = {"G13W (With Projector)", "G23W (With Projector)"};
    roomData.put("Multi-Function Hall in Central Building", rooms52);

    String rooms53[] = {"463", "516", "529", "563"};
    roomData.put("Computer rooms in Central Building", rooms53);



  }

  /**
   * 建筑所有room的状态
   */
  public static Map<String, String> buildAllRoomStatus = new HashMap<String, String>();

  public static String buildId = null;
  public static int year = 0;
  public static int mon = 0;
  public static int day = 0;
  public static int currentSelect = 0;

}

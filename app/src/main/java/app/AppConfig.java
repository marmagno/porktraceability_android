package app;

/**
 * Created by marmagno on 11/10/2015.
 */
public class AppConfig {
    // Server user login url

    public static String IP = "10.0.5.65";

    public static String URL_GETALLDATA =
            "http://" + IP + "/phpork/android_connect/getTablesData.php";

    public static String URL_SENDNEWDATA =
            "http://" + IP + "/phpork/android_connect/insertNewData.php";


}

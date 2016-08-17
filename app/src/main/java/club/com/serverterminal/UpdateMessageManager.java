package club.com.serverterminal;

/**
 * Created by kingroc on 16-8-17.
 */

public class UpdateMessageManager {
    private UpdateMessageManager(){}

    public static void storeMessage(String orgMsg){
        CNTrace.d("store msg : " + orgMsg);
    }
}

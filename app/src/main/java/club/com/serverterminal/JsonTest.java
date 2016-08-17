package club.com.serverterminal;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kingroc on 16-8-4.
 */
public class JsonTest {

    public static class Port{
        String name;
        String state;
        int sequence;
        String customName;
    };

    public static class Module{
        String name;
        String customName;
        List<Port> list;

        public Module(){
            list = new ArrayList();
        }
    };

    public static void test (){
        Module modle = new Module();
        modle.customName="灯光滴定模块";
        modle.name="20812";

        for(int i = 0; i < 5; i++){
            Port port = new Port();
            port.name = "PWM";
            port.sequence = i;

            switch(i){
                case 0:
                    port.state = "30%";
                    port.customName = "LED red";
                    break;

                case 1:
                    port.state = "40%";
                    port.customName = "LED white";
                    break;

                case 2:
                    port.state = "50%";
                    port.customName = "LED blue";
                    break;

                case 3:
                    port.state = "5ml/day";
                    port.customName = "Ca";
                    break;

                case 4:
                    port.state = "6ml/day";
                    port.customName = "Mg";
                    break;

                case 5:
                    port.state = "3ml/day";
                    port.customName = "KH";
                    break;
            }
            modle.list.add(port);
        }

        Gson gson = new Gson();
        String json = gson.toJson(modle);
        CNTrace.d(json.toString());
    }

}

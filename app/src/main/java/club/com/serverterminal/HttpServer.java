package club.com.serverterminal;

import android.content.Intent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

;

/**
 * Created by kingroc on 16-8-16.
 */

public class HttpServer extends NanoHTTPD {

    public enum Status implements NanoHTTPD.Response.IStatus {
        SWITCH_PROTOCOL(101, "Switching Protocols"),
        NOT_USE_POST(700, "not use post");

        private final int requestStatus;
        private final String description;

        Status(int requestStatus, String description) {
            this.requestStatus = requestStatus;
            this.description = description;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public int getRequestStatus() {
            return 0;
        }
    }

    public HttpServer(int port) {
        super(port);
    }

    private void sendMsg(String ip, String data){
        Intent i = new Intent(App.getContext(), MainService.class);
        i.putExtra("id", MainService.UPDATE_MSG);
        i.putExtra("ip", ip);
        App.getContext().startService(i);
    }

    @Override
    public Response serve(IHTTPSession session) {

        if (Method.POST.equals(session.getMethod())) {
            Map<String, String> files = new HashMap<String, String>();
            Map<String, String> header = session.getHeaders();

            try {
                session.parseBody(files);
                String body = session.getQueryParameterString();
                CNTrace.d("header : " + header);
                CNTrace.d("body : " + body);
                UpdateMessageManager.storeMessage(body);
                sendMsg(header.get("http-client-ip"), body);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", "HelloWorld");
        }else
            return newFixedLengthResponse(Status.NOT_USE_POST, "text/html", "use post");
    }
}

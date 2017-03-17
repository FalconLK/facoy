import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

public class XMPPSendMessage {

	public static boolean sendMessage(String message, String receipient) {
		//Create Message
		JID jid = new JID(receipient);
        Message msg = new MessageBuilder()
            .withRecipientJids(jid)
            .withBody(message)
            .build();

        //Send Message
        XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        SendResponse status = xmpp.sendMessage(msg);
        
        //Get and return if successful
        boolean messageSent = false;
        messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
        return messaageSent;
	}
	
}

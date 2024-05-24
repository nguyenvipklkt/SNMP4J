import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpExample {

    public static void main(String[] args) {
        try {
            // Tạo TransportMapping và Snmp
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();             
            // Tạo địa chỉ đích
            Address targetAddress = GenericAddress.parse("udp:localhost/161");
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(1500);
            target.setVersion(SnmpConstants.version2c);

            System.out.println("");
            System.out.println("Version in snmp : " + target.getVersion());
            System.out.println("Address : " + target.getAddress());

            // Tạo PDU
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(".1.3.6.1.2.1.1.1.0")));
            pdu.setType(PDU.GET);

            // Gửi yêu cầu và nhận phản hồi
            System.out.println("Sending SNMP GET request to " + targetAddress);
            ResponseEvent response = snmp.send(pdu, target);
            System.out.println("Response: " + response);

            if (response != null) {
                System.out.println("Received response from: " + response.getPeerAddress());
                PDU responsePDU = response.getResponse();
                System.out.println("Response PDU: " + responsePDU);
                if (responsePDU != null) {
                    System.out.println("Response PDU has error status: " + responsePDU.getErrorStatusText());
                    for (Object vbObject : responsePDU.getVariableBindings()) {
                        VariableBinding vb = (VariableBinding) vbObject;
                        System.out.println(vb.getOid() + " = " + vb.getVariable());
                    }
                } else {
                    System.out.println("Response PDU is null");
                }
            } else {
                System.out.println("Timeout or no response");
            }

            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

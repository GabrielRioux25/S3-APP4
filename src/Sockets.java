import java.net.InetAddress;
import java.net.UnknownHostException;

public class Sockets extends Couche {
    InetAddress dest_adresse = null;
    int port;
    protected ReceptionThread thread;

    private static Sockets instance ;

    private Sockets(){

    }
    public static Sockets getInstance(){
        if(instance == null){
            instance = new Sockets();
        }
        return instance;

    }

    public void setDest_adresse(String adresse) {
        try {
            this.dest_adresse = InetAddress.getByName(adresse);
        }catch (UnknownHostException e){

        }
    }
    public void setDestPort(int port){
        this.port = port;
    }
    @Override
    protected void receiveFromUp(byte[] PDU) {

    }

    @Override
    protected void receiveFromDown(byte[] PDU) throws ErreurTransmissionExeption {

    }
}

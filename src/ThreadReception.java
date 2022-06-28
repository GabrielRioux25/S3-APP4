import com.sun.jdi.connect.TransportTimeoutException;
/*
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ThreadReception extends Thread {
    public DatagramSocket socket;
    private Sockets parent;
    public boolean running = true;

    public ThreadReception(int port, Sockets parent) throws SocketException {
        super("Thread de Reception");
        socket = new DatagramSocket();
        this.parent = parent;
    }

    //ecoute le socket de reception
    public void run() {
        while (running == true) {

            try{
                byte buffer[] = new byte[204];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                parent.receiveFromDown(packet.getData());
            }catch (IOException| ErreurTransmissionExeption e){
                running = false;
                socket.close();
            }
        }
        socket.close();

    }
}
*/

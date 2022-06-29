import java.io.*;
import java.net.*;



public class BerkeleySockets extends Couche {
    InetAddress address = null;
    int port = 0;
    protected ReceptionThread thread;
    public int delay = 3;
    public static int ErreurBitShift = -1;
    public int packetSent = 0;

    // Singleton
    private static BerkeleySockets instance;

    /**
     *
     */
    private BerkeleySockets(){}

    /**
     *
     * @return
     */
    static public BerkeleySockets getInstance() {
        return instance == null ? new BerkeleySockets() : instance;
    }

    /**
     *
     * @param i
     */
    public static void setErreurShiftBit(int i) {
        ErreurBitShift = i;
    }

    /**
     *
     * @param address
     */
    public void setDestAddress(String address) {
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param port
     */
    public void setDestPort(int port) {
        this.port = port;
    }

    /**
     *
     */
    public void start() {
        thread.running = true;
        thread.start();
    }

    /**
     *
     */
    public void stop() {
        thread.running = false;
        thread.stop();
    }

    /**
     *
     * @return
     */
    public boolean threadRunnint() {
        return thread.running;
    }

    /**
     *
     * @param PDU
     */
    @Override
    protected void receiveFromUp(byte[] PDU) {
        // get a datagram socket
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // shift bit for error
        packetSent++;
        //System.out.println("Packet Sent= "+packetSent+" "+"Error Delay = "+ errorDelay);
        if(packetSent == ErreurBitShift){
            PDU[10] <<= 100;}

        // send request
        DatagramPacket packet = new DatagramPacket(PDU, PDU.length, address, port);
        try {
            socket.send(packet);
            Thread.sleep(delay);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param PDU
     * @throws ErreurTransmissionExeption
     */
    @Override
    public void receiveFromDown(byte[] PDU) throws  ErreurTransmissionExeption {

        passUp(PDU);
    }

    /**
     *
     * @param port
     * @throws IOException
     */
    public void createReceptionThread(int port) throws IOException {
        this.thread = new ReceptionThread(port, this);
    }


    /**
     *
     */
    private class ReceptionThread extends Thread{
        protected DatagramSocket socket = null;
        private BerkeleySockets parent;
        public boolean running = true;

        public ReceptionThread(int port, BerkeleySockets parent) throws IOException {
            super(" Thread de reception " + Math.random());
            socket = new DatagramSocket(port);
            this.parent = parent;
        }


        /**
         *
         */
        public void run() {
            while (running) {
                try {
                    byte[] buf = new byte[204];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    // Send packet data to parent
                    parent.receiveFromDown(packet.getData());
                } catch (IOException | ErreurTransmissionExeption e) {
                    running = false;
                    socket.close();
                    System.out.println(e.getLocalizedMessage());
                }
            }
            socket.close();
        }
    }

}

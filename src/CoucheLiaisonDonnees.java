import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.zip.CRC32;

import static java.lang.System.arraycopy;

public class CoucheLiaisonDonnees extends Couche{

    private int erreursCRC = 0;
    private int packetsRecus =0;
    private int packetsTransmis = 0;
    private static CoucheLiaisonDonnees instance;
    private CoucheLiaisonDonnees(){

    }
    static public CoucheLiaisonDonnees getInstance(){
        ecrireLog("Get instace Couche liaison de donnees")
        if(instance == null){
            instance = new CoucheLiaisonDonnees();
        }
        return instance;
    }

    public static void ecrireLog(String operation) {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        try {
            FileWriter fileWriter = new FileWriter("liaisonDeDonnees.log",true);
            fileWriter.write("Operation: "+operation + "yyyy-mm-dd hh:mm:ss:mmm: " + currentTimestamp);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void receiveFromUp(byte[] PDU) {
        ecrireLog("Reception d'un packet de la couche physique");
        //extraction des datas du PDU
        byte[] paquet = new byte[PDU.length - 4];
        arraycopy(PDU, 4, paquet, 0, paquet.length);

        //crc
        CRC32 crc = new CRC32();
        crc.update(paquet);
        int crcValue = (int) crc.getValue();
        int oldCRC = (((int) PDU[0] << 24) & 0xFF000000) | (((int) PDU[1] << 16) & 0x00FF0000) | (((int) PDU[2] << 8) & 0x0000FF00) | ((int) PDU[3] & 0x000000FF);
        if (crcValue != oldCRC) {  // Error in CRC
            System.out.println("Error CRC32");
            erreursCRC++;
            return; //abandonne le paquet
        }

        // Send PDU to network layer
        packetsRecus++;
        System.out.println("COUCHE DATALINK paquets recus = "+ packetsRecus);
        passUp(paquet);
    }

    @Override
    protected void receiveFromDown(byte[] PDU) {

    }
}

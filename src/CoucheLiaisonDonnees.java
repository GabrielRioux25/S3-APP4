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

    /**
     *
     */
    private CoucheLiaisonDonnees(){

    }

    /**
     *
     * @return
     */
    static public CoucheLiaisonDonnees getInstance(){
        ecrireLog("Get instace Couche liaison de donnees");
        if(instance == null){
            instance = new CoucheLiaisonDonnees();
        }
        return instance;
    }

    /**
     *
     * @param operation
     */
    public static void ecrireLog(String operation) {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        try {
            FileWriter fileWriter = new FileWriter("liaisonDeDonnees.log",true);
            fileWriter.write("Operation: "+operation + "yyyy-mm-dd hh:mm:ss:mmm: " + currentTimestamp+ "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param PDU
     * @throws ErreurTransmissionExeption
     */
    @Override
    protected void receiveFromDown(byte[] PDU) throws ErreurTransmissionExeption {


        ecrireLog("Reception d'un packet de la couche physique (Scokets de berkeley)");
        //extraction des datas du PDU
        byte[] paquet = new byte[PDU.length - 4];
        arraycopy(PDU, 4, paquet, 0, paquet.length);

        //crc
        CRC32 crc = new CRC32();
        crc.update(paquet);
        int crcValue = (int) crc.getValue();
        int oldCRC = (((int) PDU[0] << 24) & 0xFF000000) | (((int) PDU[1] << 16) & 0x00FF0000) | (((int) PDU[2] << 8) & 0x0000FF00) | ((int) PDU[3] & 0x000000FF);

        if (crcValue != oldCRC) {  // Error in CRC
            System.out.println("Erreur CRC32");
            ecrireLog("Erreur de CRC ");
            erreursCRC++;
            return; //abandonne le paquet
        }


        packetsRecus++;

        ecrireLog("Envoit vers la couche transport");
        passUp(paquet);

    }

    /**
     *
     * @param PDU
     */
    @Override
    protected void receiveFromUp(byte[] PDU) {

        ecrireLog("Reception de la couche transport");

        // nouveau pdu
        byte[] trame = new byte[PDU.length + 4];

        // Calcul du  CRC utilisant le polynome 0x82608EDB (default)
        CRC32 crc = new CRC32();
        crc.update(PDU);
        long crcValue = crc.getValue();
        //System.out.println("CRCVALUE = "+crcValue);
        byte[] CRCBytes = new byte[] {
                (byte) (crcValue >> 24),
                (byte) (crcValue >> 16),
                (byte) (crcValue >> 8),
                (byte) crcValue};

        //les 4 premiers bits de la trame sont les vieux crc.
        arraycopy(CRCBytes, 0, trame, 0, CRCBytes.length);


        // Copie le  PDU dans la  trame
        arraycopy(PDU, 0, trame, 4, PDU.length);


        packetsTransmis++;
        ecrireLog("Envoit vers la couches physique (Sockets)");
        passDown(trame);
    }
}

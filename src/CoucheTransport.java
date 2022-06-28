


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.arraycopy;
import static java.util.Arrays.fill;

public class CoucheTransport extends Couche{

//CODES HEADER HUMAINEMENT LISIBLE
private final char CODE_DEBUT = 'd';
    private final char CODE_FIN = 'f';
    private final char CODE_NORMAL = ' ';
    private final char CODE_ACK = 'a';
    private final char CODE_RESEND = 'r';


    private final int TAILLE_HEADER = 12;
    private final int SIZE = 188;
    private final int SIZE_HEADER_POS = 9;
    private final int SEQ_HEADER_POS = 1;

    private int errors =0;
    private int sequenceFin = -1;
    private byte[][] TPDU;
    private Map<Integer, byte[]> receiveBuffer;

    private static CoucheTransport instance;

    /**
     *
     */
    private CoucheTransport(){

    }

    /**
     *
     * @return
     */
    public static CoucheTransport getInstance() {
        if(instance == null){
            instance = new CoucheTransport();
        }
        return instance;
    }

    /**
     *
     * @param PDU
     */
    @Override
    protected void receiveFromUp(byte[] PDU) {
            int count = (int) Math.ceil((double) PDU.length / SIZE);
            System.out.println("COUCHE TRANSPORT: Divison en  = " +count);
            TPDU = new byte[count][200];

            // Division of PDU for size of 200 each & end send to Layer
            for(int i = 0; i < count; i++) {
                int taille = SIZE;
                if (i == count - 1) {
                    taille = PDU.length % SIZE; //taille de chaque paquet
                }


                arraycopy(PDU, i * SIZE, TPDU[i], TAILLE_HEADER, taille);





                //System.out.println("i = "+i+  ", taille = "+taille);


                //debut/fin
                char code = CODE_NORMAL;
                if (i == 0) {
                    code = CODE_DEBUT;
                } else if (i == count - 1) {
                    code = CODE_FIN;
                }


                TPDU[i][0] = (byte) code;
                arraycopy(convertIntToASCII(i, 8), 0, TPDU[i], SEQ_HEADER_POS, 8); // SEQUENCE
                arraycopy(convertIntToASCII(taille, 3), 0, TPDU[i], SIZE_HEADER_POS, 3); // taille

                // envoie a la couche inferieure
                passDown(TPDU[i]);
            }
            System.out.println("COUCHE TRANSPORT: FIN DE LA TRANSMISSION DE DONNEES");
    }

    /**
     *
     * @param PDU
     * @throws ErreurTransmissionExeption
     */
    @Override
    protected void receiveFromDown(byte[] PDU) throws ErreurTransmissionExeption {

        byte[] seq_bytes = Arrays.copyOfRange(PDU, SEQ_HEADER_POS, SIZE_HEADER_POS);
        byte[] size_bytes = Arrays.copyOfRange(PDU, SIZE_HEADER_POS, TAILLE_HEADER );
        char code = (char) PDU[0];
        int seq = convertAsciiToInt(seq_bytes);
        int size = convertAsciiToInt(size_bytes);
        //System.out.println("RECEPTION DE LA COUCHE DATA/LINK : SEQ = "+ seq +"Taille= "+size);
        byte[] data_bytes = Arrays.copyOfRange(PDU, TAILLE_HEADER , TAILLE_HEADER + size);

        if ( code == CODE_DEBUT){
            //debut de la communication, reset le buffer de reception
            receiveBuffer = new HashMap<>();
            savePDU(0, data_bytes);
        }
        else if (code == CODE_FIN) {
            sequenceFin = seq;
            savePDU(seq, data_bytes);

        }
        else if (code == CODE_NORMAL) {
            savePDU(seq,data_bytes);
        }
        else if (code == CODE_RESEND) {

            errors ++;
            System.out.println("Renvoit du packet: " +errors);
            passDown(TPDU[seq]);
        }

        if(sequenceFin != -1) {

            if (receiveBuffer.size() <= sequenceFin)
                return;
            int arrayL = (receiveBuffer.size() - 1) * SIZE + receiveBuffer.get(sequenceFin).length;
            byte[] passUpBuffer = new byte[arrayL];
            int count = 0;
            for (Map.Entry<Integer, byte[]> key_value : receiveBuffer.entrySet()) {
                arraycopy(key_value.getValue(), 0, passUpBuffer, count, key_value.getValue().length);
                count += key_value.getValue().length;
            }
            System.out.println("EREURS:" + errors);
            passUp(passUpBuffer);
        }

    }

    /**
     *
     * @param data
     * @param size
     * @return
     */
    private byte[] convertIntToASCII(int data, int size) {
        String converted = Integer.toString(data);
        byte[] converted2 = converted.getBytes(StandardCharsets.US_ASCII);

        // Copy data with padding on the left
        byte[] newData = new byte[size];
        fill(newData, (byte) '0');
        arraycopy(converted2, 0, newData, size - converted2.length, converted2.length);

        return newData;
    }

    /**
     *
     * @param data
     * @return
     */
    private int convertAsciiToInt(byte[] data) {
        String data_string = new String(data);
        return Integer.parseInt(removeLeadingZeros(data_string));
    }

    /**
     *
     * @param str
     * @return
     */
    private String removeLeadingZeros(String str) {
        String regex = "^0+(?!$)";
        str = str.replaceAll(regex, "");

        return str;
    }

    /**
     *
     * @param seq
     * @param data_bytes
     * @throws ErreurTransmissionExeption
     */
    private void savePDU(int seq, byte[] data_bytes) throws ErreurTransmissionExeption {
        if (seq != 0 && receiveBuffer.get(seq - 1) == null) {
            errors++;
            System.out.println("Ajout d'une erreur");
            if(errors >= 3) {

                throw new ErreurTransmissionExeption("3 erreurs, connexion perdu");
            }
            byte[] rPDU = createResendPDU(seq - 1);
            passDown(rPDU);
        }
        if (receiveBuffer.get(seq) != null)
            return;

        receiveBuffer.put(seq, data_bytes);
        byte[] ackPDU = createAckPDU(seq);
        passDown(ackPDU);
    }

    /**
     *
     * @param seq
     * @return
     */
    private byte[] createResendPDU(int seq) {
        int taille = 0;
        byte[] resendPDU = new byte[200];

        // Copy data size as ASCII in header
        resendPDU[0] = (byte) CODE_RESEND;       // Assuming sizeof(char) == sizeof(byte)
        arraycopy(convertIntToASCII(seq, 8), 0, resendPDU, SEQ_HEADER_POS, 8); // SEQUENCE
        arraycopy(convertIntToASCII(taille, 3), 0, resendPDU, SIZE_HEADER_POS, 3); // SIZE

        // Send lower Layer
        return resendPDU;
    }

    /**
     *
     * @param seq
     * @return
     */
    private byte[] createAckPDU(int seq) {
        byte[] ackPDU = new byte[200];


        ackPDU[0] = (byte) CODE_ACK;
        arraycopy(convertIntToASCII(seq, 8), 0, ackPDU, SEQ_HEADER_POS, 8); // SEQUENCE
        arraycopy(convertIntToASCII(0, 3), 0, ackPDU, SIZE_HEADER_POS, 3); // SIZE


        return ackPDU;
    }

}

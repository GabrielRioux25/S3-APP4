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

    private int errors;

    private byte[][] TPDU;
    private Map<Integer, byte[]> receiveBuffer;

    private static CoucheTransport instance;
    private CoucheTransport(){

    }


    public static CoucheTransport getInstance() {
        if(instance == null){
            instance = new CoucheTransport();
        }
        return instance;
    }

    @Override
    protected void receiveFromUp(byte[] PDU) {
            int count = (int) Math.ceil((double) PDU.length / SIZE);
            System.out.println("Count= " +count);
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

                // Copy data size as ASCII in header
                TPDU[i][0] = (byte) code;       // Assuming sizeof(char) == sizeof(byte)
                arraycopy(convertIntToASCII(i, 8), 0, TPDU[i], SEQ_HEADER_POS, 8); // SEQUENCE
                arraycopy(convertIntToASCII(taille, 3), 0, TPDU[i], SIZE_HEADER_POS, 3); // SIZE

                // envoie a la couche inferieure
                passDown(TPDU[i]);
            }
            System.out.println("COUCHE TRANSPORT: FIN DE LA TRANSMISSION DE DONNEES");
    }

    @Override
    protected void receiveFromDown(byte[] PDU) {
        byte[] seq_bytes = Arrays.copyOfRange(PDU, SEQ_HEADER_POS, SIZE_HEADER_POS);
        byte[] size_bytes = Arrays.copyOfRange(PDU, SIZE_HEADER_POS, TAILLE_HEADER );
        char code = (char) PDU[0];
        int seq = convertAsciiToInt(seq_bytes);
        int size = convertAsciiToInt(size_bytes);
        System.out.println("RECEPTION DE LA COUCHE DATA/LINK : SEQ = "+ seq +"Taille= "+size);
        byte[] data_bytes = Arrays.copyOfRange(PDU, TAILLE_HEADER , TAILLE_HEADER + size);
        if ( code == CODE_DEBUT){
            //debut de la communication, reset le buffer de reception
            receiveBuffer = new HashMap<>();


        }
    }


    private byte[] convertIntToASCII(int data, int size) {
        String converted = Integer.toString(data);
        byte[] converted2 = converted.getBytes(StandardCharsets.US_ASCII);

        // Copy data with padding on the left
        byte[] newData = new byte[size];
        fill(newData, (byte) '0');
        arraycopy(converted2, 0, newData, size - converted2.length, converted2.length);

        return newData;
    }

    private int convertAsciiToInt(byte[] data) {
        String data_string = new String(data);
        return Integer.parseInt(removeLeadingZeros(data_string));
    }

    private String removeLeadingZeros(String str) {
        String regex = "^0+(?!$)";
        str = str.replaceAll(regex, "");

        return str;
    }
}

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.lang.System.arraycopy;

public class CoucheApplication extends Couche {
    //singleton
    private static CoucheApplication instance;


    //constructeur prive
    private CoucheApplication(){

    }

    public static CoucheApplication getInstance() {
        if (instance == null){
            instance = new CoucheApplication();
        }
        return instance;
    }

    @Override
    protected void receiveFromUp(byte[] PDU) {
        // pas besoin, Couche la plus haute du modele OSI
    }


    //Recoit les packets de la couches Inferieure (Transport). Puis cree un nouveau fichier et dompe les data des packets dans ce fichiers.
    @Override
    protected void receiveFromDown(byte[] PDU) {
        System.out.println("COUCHE APPLICATION: Reception de la couche transport");
        String nomFichier = new String(Arrays.copyOfRange(PDU,0,188)).trim();
        System.out.println("COUCHE APPLICATION: --> nom fichier= "+ nomFichier);

        //188 bits aloues au titre du fichier, raw-datas suivent
        byte[] data_bytes = Arrays.copyOfRange(PDU, 188,PDU.length);
        try {
            String filePath = new File("").getAbsolutePath();
            File file = new File(filePath + "/dest/" + nomFichier);
            if(file.exists())
                file.delete();
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
            try (FileOutputStream fos = new FileOutputStream(file.getPath())) {
                System.out.println("Writing stream.");
                fos.write(data_bytes);
                System.out.println("Done writing.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    //envoit le fichier a la couche transport

    public void sendFile(String file) throws IOException, InterruptedException {
        File file2 = new File(file);
        byte[] APDU;
        byte[] filename = file2.getName().getBytes();
        Path filePath = file2.toPath();
        byte[] fileBytes = Files.readAllBytes(filePath);
        APDU = new byte[188 + fileBytes.length];
        //nom
        arraycopy(filename, 0, APDU, 0, filename.length);
        //data
        arraycopy(fileBytes, 0, APDU, 188, fileBytes.length);
        passDown(APDU);
        Thread.sleep(1000);
        System.exit(0);




    }

}

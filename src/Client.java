import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        CoucheApplication coucheApplication;
        CoucheTransport coucheTransport;
        CoucheLiaisonDonnees coucheLiaisonDonnees;

        BerkeleySockets berkeleySockets;
        String nomFichier = args[0];
        boolean Erreur = Boolean.parseBoolean(args[1]);


        coucheApplication = CoucheApplication.getInstance();
        coucheLiaisonDonnees = CoucheLiaisonDonnees.getInstance();
        coucheTransport = CoucheTransport.getInstance();
        berkeleySockets = BerkeleySockets.getInstance();

        if(Erreur == true){
            BerkeleySockets.setErreurShiftBit(2);
        }

        coucheApplication.setCoucheInferieure(coucheTransport);
        coucheTransport.setCoucheInferieure(coucheLiaisonDonnees);
        coucheTransport.setCoucheSuperieure(coucheApplication);

        coucheLiaisonDonnees.setCoucheInferieure(berkeleySockets);
        coucheLiaisonDonnees.setCoucheSuperieure(coucheTransport);

        berkeleySockets.setCoucheSuperieure(coucheLiaisonDonnees);

        berkeleySockets.createReceptionThread(4449);
        berkeleySockets.setDestPort(4449);
        berkeleySockets.setDestAddress("localhost");
        berkeleySockets.start();
        coucheApplication.sendFile(nomFichier);







    }
}

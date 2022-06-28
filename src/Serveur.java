import java.io.IOException;

public class Serveur {

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        CoucheTransport coucheTransport;
         CoucheLiaisonDonnees coucheLiaisonDonnees;
         CoucheApplication coucheApplication;
         //Sockets sockets;
        BerkeleySockets berkeleySockets;

         coucheApplication = CoucheApplication.getInstance();
         coucheLiaisonDonnees = CoucheLiaisonDonnees.getInstance();
         coucheTransport = CoucheTransport.getInstance();
         //sockets = Sockets.getInstance();
        berkeleySockets = BerkeleySockets.getInstance();


        coucheApplication.setCoucheInferieure(coucheTransport);
        coucheTransport.setCoucheInferieure(coucheLiaisonDonnees);
        coucheTransport.setCoucheSuperieure(coucheApplication);
        //coucheLiaisonDonnees.setCoucheInferieure(sockets);
        coucheLiaisonDonnees.setCoucheInferieure(berkeleySockets);
        coucheLiaisonDonnees.setCoucheSuperieure(coucheTransport);
        //sockets.setCoucheSuperieure(coucheLiaisonDonnees);
        berkeleySockets.setCoucheSuperieure(coucheLiaisonDonnees);


        berkeleySockets.createReceptionThread(25008);
        berkeleySockets.setDestPort(4445);
        berkeleySockets.setDestAddress("localhost");
        //sockets.setThreadReception(25009);
        //sockets.setDest_adresse("localhost");
        //sockets.setDestPort(4445);
       //sockets.Start();
        while(berkeleySockets.threadRunnint()) {
            int command = System.in.read();
            switch (command) {
                case 113:
                case 81:
                    System.exit(0);
                    break;
            }

    }}}







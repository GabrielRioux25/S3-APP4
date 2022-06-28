/*****************************************************************
 * Modele Osi
 *
 *    Aplication
 *    Transport
 *    Liaison de donnees
 *    Physical(sockets de berkeley)
 *
 * DEUX CHEMINS POSSIBLEs
 * Soit on part De l'application, on se rend vers la couche physique (VERS LE BAS)
 * Soit on part de la couche physique, on se rend vers la couche application (VERS LE HAUT)
 * Classe qui fait la définition des envois/receptions d'une couche adjacente (Haut ou bas).
 ******************************************************************/


public abstract class Couche {
    private Couche coucheSuperieure;
    private Couche coucheInferieure;

    /**
     *
     * @param PDU
     */
    protected abstract void receiveFromUp(byte[] PDU);

    /**
     *
     * @param PDU
     * @throws ErreurTransmissionExeption
     */
    protected abstract void receiveFromDown(byte[] PDU) throws ErreurTransmissionExeption;

    /**
     *
     * @param PDU
     * @throws ErreurTransmissionExeption
     */
    //pour le modele chaine de responsabilités
    protected void passUp(byte[] PDU) throws ErreurTransmissionExeption {
        coucheSuperieure.receiveFromDown(PDU);
    }

    /**
     *
     * @param PDU
     */
    protected  void passDown(byte[] PDU){
        coucheInferieure.receiveFromUp(PDU);
    }

    //setup des couches precedentes et suivantes de chaque couches

    /**
     *
     * @param inferieure
     */
    public void setCoucheInferieure(Couche inferieure){
        coucheInferieure = inferieure;

    }

    /**
     *
     * @param Superieure
     */
    public void setCoucheSuperieure(Couche Superieure){
        coucheSuperieure = Superieure;
    }


}

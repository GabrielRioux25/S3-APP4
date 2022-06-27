/*****************************************************************
 * Modele Osi
 *
 *    Aplication
 *    Transport
 *    Reseau
 *    Liaison de donnees
 *    Physical
 *
 * DEUX CHEMINS POSSIBLEs
 * Soit on part De l'application, on se rend vers la couche physique (VERS LE BAS)
 * Soit on part de la couche physique, on se rend vers la couche application (VERS LE HAUT)
 * Classe qui fait la définition des envois/receptions d'une couche adjacente (Haut ou bas).
 ******************************************************************/


public abstract class Couche {
    private Couche coucheSuivante;
    private Couche couchePrecedente;

    protected abstract void receiveFromUp(byte[] PDU);

    protected abstract void receiveFromDown(byte[] PDU);

    protected void passUp(byte[] PDU){
        coucheSuivante.receiveFromDown(PDU);
    }

    protected  void passDown(byte[] PDU){
        couchePrecedente.receiveFromUp(PDU);
    }

    //setup des couches precedentes et suivantes de chaque couches

    public void setCoucheSuivante(Couche suivante){
        this.coucheSuivante = suivante;

    }
    public void setCouchePrecedente(Couche precedente){
        this.couchePrecedente = precedente;
    }


}

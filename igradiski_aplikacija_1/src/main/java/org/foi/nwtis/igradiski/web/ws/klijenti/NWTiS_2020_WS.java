/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.ws.klijenti;



/**
 *
 * @author Korisnik
 */
public class NWTiS_2020_WS {
    /**
     * Dohvaca status grupe korisnika kod zahjtjeva
     * @param korisnik korisnicko ime
     * @param lozinka lozinka
     * @return stauts
     */
    public StatusKorisnika dajStatusGrupe(String korisnik,String lozinka){
       AerodromiWS_Service service = new AerodromiWS_Service();
       return service.getAerodromiWSPort().dajStatusGrupe(korisnik, lozinka);
    }
    /**
     * Prijavljuje grupu korisnika
     * @param korisnik korisnicko ime
     * @param lozinka lozinka
     * @return true false
     */
    public boolean prijaviGrupu(String korisnik,String lozinka){
        AerodromiWS_Service service = new AerodromiWS_Service();
        Boolean odgovor =service.getAerodromiWSPort().autenticirajGrupu(korisnik, lozinka);
        return odgovor;
    }
    /**
     * registrira grupu
     * @param korisnik korisnicko ime
     * @param lozinka lozinka
     * @return 
     */
    public boolean registriraj(String korisnik,String lozinka){
        AerodromiWS_Service service = new AerodromiWS_Service();
        Boolean odgovor =service.getAerodromiWSPort().registrirajGrupu(korisnik, lozinka);
        return odgovor;
    }
    /**
     * 
     * @param korisnik korisnicko ime
     * @param lozinka lozinka
     * @return 
     */
    public boolean odjaviGrupu(String korisnik,String lozinka){
        AerodromiWS_Service service = new AerodromiWS_Service();
        Boolean deregistrirajGrupu = service.getAerodromiWSPort().deregistrirajGrupu(korisnik, lozinka);
        return deregistrirajGrupu;
    }
    
    public boolean aktivirajGrupu(String korisnik,String lozinka){
        AerodromiWS_Service service = new AerodromiWS_Service();
        Boolean aktivirana= service.getAerodromiWSPort().aktivirajGrupu(korisnik, lozinka);
        return aktivirana;
    }
    
    public boolean blokirajGrupu(String korisnik,String lozinka){
         AerodromiWS_Service service = new AerodromiWS_Service();
         boolean blokirana = service.getAerodromiWSPort().blokirajGrupu(korisnik, lozinka);
         return blokirana;
    }
    
    public void posaljiKomanduAerodromi(String korisnik,String lozinka){

    }
    
    public boolean obrisiPostojeceAerodrome(String korisnik,String lozinka){
         AerodromiWS_Service service = new AerodromiWS_Service();
         boolean obrisano =service.getAerodromiWSPort().obrisiSveAerodromeGrupe(korisnik, lozinka);
         return obrisano;
    }
    
    public boolean dodajAerodromGrupi(String korisnik, String lozinka, String icao) {
        AerodromiWS_Service service = new AerodromiWS_Service();
        boolean dodano = service.getAerodromiWSPort().dodajAerodromIcaoGrupi(korisnik, lozinka, icao);
        return dodano;
    }
    
    public int broj (String korisnik, String lozinka){
       AerodromiWS_Service service = new AerodromiWS_Service();
       return service.getAerodromiWSPort().dajSveAerodromeGrupe(korisnik, lozinka).size();
    }
    
}

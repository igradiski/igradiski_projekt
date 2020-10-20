/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.sb;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Korisnik
 */

@Singleton
@Startup
public class PokretacDretve {
    
    
    private DretvaZaKomunikaciju dretvaZaKomunikaciju;
    public BP_Konfiguracija konf;
    public boolean dopustenoPreuzimanje;
    public AirportsFacade airportsFacade;
    public MyairportsFacade myairportsFacade; 
    public MyairportslogFacade myairportslogFacade;
    public KorisniciFacade korisniciFacade;
    public AirplanesFacade airplanesFacade;
    
    
    
    @PreDestroy
    public void prekiniDretvu(){
        if(dretvaZaKomunikaciju!=null)
            dretvaZaKomunikaciju.interrupt();
    }

    public void pokreniDretvu(BP_Konfiguracija konf, boolean dopustenoPreuzimanjeDretve,
            AirplanesFacade airplanesFacade, AirportsFacade airportsFacade, MyairportsFacade myairportsFacade,
            MyairportslogFacade myairportslogFacade) {
        
        dretvaZaKomunikaciju = new DretvaZaKomunikaciju(konf,dopustenoPreuzimanjeDretve,
                airplanesFacade,airportsFacade,myairportsFacade,myairportslogFacade);
        dretvaZaKomunikaciju.start();
    }
    
    
}

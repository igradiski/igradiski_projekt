package org.foi.nwtis.igradiski.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.igradiski.ejb.sb.AirplanesFacade;
import org.foi.nwtis.igradiski.ejb.sb.AirportsFacade;
import org.foi.nwtis.igradiski.ejb.sb.KorisniciFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportsFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportslogFacade;
import org.foi.nwtis.igradiski.ejb.sb.PokretacDretve;
import org.foi.nwtis.igradiski.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.igradiski.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;

/**
 * Web application lifecycle listener.
 *
 * @author Korisnik
 */
@WebListener
public class SlusacAplikacije1 implements ServletContextListener {

    public boolean dopustenoPreuzimanjeDretve = true;

    @Inject
    PokretacDretve pokretac;

    @Inject
    AirportsFacade airportsFacade;

    @Inject
    MyairportsFacade myairportsFacade;

    @Inject
    MyairportslogFacade myairportslogFacade;

    @Inject
    KorisniciFacade korisniciFacade;

    @Inject
    AirplanesFacade airplanesFacade;

    /**
     * Metoda koja pokrece dretvu za preuzimanje aerodroma letova i ostalo
     *
     * @param sce servelt kontekst
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("/WEB-INF") + File.separator + datoteke;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            PokreniModul2(konf);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PokreniModul2(BP_Konfiguracija konf) {
        pokretac.dopustenoPreuzimanje = dopustenoPreuzimanjeDretve;
        pokretac.konf = konf;
        pokretac.airplanesFacade = airplanesFacade;
        pokretac.airportsFacade = airportsFacade;
        pokretac.myairportsFacade = myairportsFacade;
        pokretac.myairportslogFacade = myairportslogFacade;
        pokretac.pokreniDretvu(konf, dopustenoPreuzimanjeDretve, airplanesFacade,
                airportsFacade, myairportsFacade, myairportslogFacade);
    }

    /**
     * Kada je kontekst unisten gasi dretvu da ne ostane daemon
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Aplikacija je zaustavljena");
    }
}

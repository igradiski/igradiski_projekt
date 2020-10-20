package org.foi.nwtis.igradiski.web.slusaci;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.igradiski.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.igradiski.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.igradiski.web.dretve.PosluziteljDretva;
import static org.foi.nwtis.igradiski.web.dretve.PosluziteljDretva.ProvjeraZauzetostiPorta;
import org.foi.nwtis.igradiski.web.dretve.ZahtjevDretva;

/**
 * Web application lifecycle listener.
 *
 * @author Korisnik
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    PosluziteljDretva posluzitelj = null;

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
            PosluziteljDretva pd = new PosluziteljDretva(konf);
            pd.start();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Aplikacija je pokrenuta");
    }

    private void PripremiServerSocket(BP_Konfiguracija konf) {
        int brojCekaca = 100;
        String portPostavka = konf.getKonfig().dajPostavku("posluzitelj.port");
        int port = Integer.parseInt(portPostavka);
        if (ProvjeraZauzetostiPorta(port)) {
            try {
                ServerSocket server = new ServerSocket(port, brojCekaca);
                PokreniServerZaZahtjeveKorisnika(konf, server);
            } catch (IOException ex) {
                Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void PokreniServerZaZahtjeveKorisnika(BP_Konfiguracija konf, ServerSocket server) {
        while (true) {
            try {
                Socket veza = server.accept();
                if (veza != null) {
                    ZahtjevDretva dretvaZahtjeva = new ZahtjevDretva();
                    dretvaZahtjeva.veza = veza;
                    dretvaZahtjeva.konf = konf;
                    dretvaZahtjeva.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(PosluziteljDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Kada je kontekst unisten gasi dretvu da ne ostane daemon
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (posluzitelj != null) {
            posluzitelj.interrupt();
        }
        System.out.println("Aplikacija je zaustavljena");
    }
}

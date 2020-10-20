/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.dretve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Korisnik
 */
public class PosluziteljDretva extends Thread {

    private BP_Konfiguracija konf;
    
    public boolean posluziteljRadi = true;

    public boolean dretvaRadi = true;

    public PosluziteljDretva(BP_Konfiguracija konfiguracija) {
        this.konf = konfiguracija;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        ObradiNaredbe();

    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        posluziteljRadi = true;
    }

    public void ObradiNaredbe() {
        PripremiServerSocket();
    }

    private void PripremiServerSocket() {
        int brojCekaca = 100;
        String portPostavka = konf.getKonfig().dajPostavku("posluzitelj.port");
        int port = Integer.parseInt(portPostavka);
        if (ProvjeraZauzetostiPorta(port)) {
            try {
                ServerSocket server = new ServerSocket(port, brojCekaca);
                PokreniServerZaZahtjeveKorisnika(konf, server);
            } catch (IOException ex) {
                Logger.getLogger(PosluziteljDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean ProvjeraZauzetostiPorta(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

    private void PokreniServerZaZahtjeveKorisnika(BP_Konfiguracija konf, ServerSocket server) {
        while (dretvaRadi) {
            try {
                Socket veza = server.accept();
                if (veza != null) {
                    ZahtjevDretva dretvaZahtjeva = new ZahtjevDretva();
                    dretvaZahtjeva.preuzimanjePodataka = posluziteljRadi;
                    dretvaZahtjeva.veza = veza;
                    dretvaZahtjeva.korisnickoIme = konf.getKonfig().dajPostavku("svn.korisnik");
                    dretvaZahtjeva.lozinka = konf.getKonfig().dajPostavku("svn.lozinka");
                    dretvaZahtjeva.konf = konf;
                    dretvaZahtjeva.pd = this;
                    dretvaZahtjeva.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(PosluziteljDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

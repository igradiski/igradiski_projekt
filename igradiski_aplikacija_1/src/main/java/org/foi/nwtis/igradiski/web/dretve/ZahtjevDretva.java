/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.dretve;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.igradiski.web.podaci.KorisnikDAO;
import org.foi.nwtis.igradiski.web.ws.klijenti.NWTiS_2020_WS;
import org.foi.nwtis.igradiski.web.ws.klijenti.StatusKorisnika;

/**
 *
 * @author Korisnik
 */
public class ZahtjevDretva extends Thread {

    ReentrantLock lock;
    
    public Socket veza;
    
    String odgovorServera = "";
    
    public BP_Konfiguracija konf;
    
    boolean radi = true;
    
    public boolean preuzimanjePodataka;
    
    PosluziteljDretva pd = null;

    public String korisnickoIme = "";
    
    public String lozinka = "";

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        while (radi) {
            if (veza != null) {
                if (preuzimanjePodataka) {
                } else {
                }
                obradiZahtjev();
                veza = null;
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public void obradiZahtjev() {
        try {
            InputStream inps = this.veza.getInputStream();
            OutputStream outs = this.veza.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(outs);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while (true) {
                int i = inps.read();
                if (i == -1) {
                    break;
                }
                output.write(i);
            }
            byte outputByteova[] = output.toByteArray();
            String vrijednost = new String(outputByteova, "UTF-8");
            OdrediKomanduGrupiKorisniku(vrijednost);
            System.out.println(odgovorServera);
            osw.write(odgovorServera);
            osw.flush();
            veza.shutdownOutput();
            veza.shutdownInput();
            veza.close();
            radi = false;
            this.interrupt();
        } catch (IOException ex) {
        }
    }

    private void ObradiZahtjevKlijenta(String vrijednost) {
        String sintaksa = "(KORISNIK [a-z|A-Z|0-9|_|-|!|#|*]{1,50}; )?(LOZINKA [a-z|A-Z|0-9|_|-|!|#|*|-]{1,50};)"
                + "( DODAJ;)?( PAUZA;)?( KRAJ;)?( STANJE;)?( RADI;)?";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(vrijednost);
        if (m.matches()) {
            String[] komande = vrijednost.split(";");
            if (komande.length == 2) {
                ProvjeriKorisnikaBaza(komande);
            } else {
                ObradiKomandu(komande);
            }
        } else {
            //kriva komanda
        }

    }

    private void OdrediKomanduGrupiKorisniku(String vrijednost) {
        if (vrijednost.contains("GRUPA")) {
            ObradiZahtjevGrupe(vrijednost);
        } else {
            ObradiZahtjevKlijenta(vrijednost);
        }
    }

    private void ObradiZahtjevGrupe(String vrijednost) {
        String sintaksa = "(KORISNIK [a-z|A-Z|0-9|_|-|!|#|*]{1,50}; )(LOZINKA [a-z|A-Z|0-9|_|-|!|#|*|-]{1,50};)( GRUPA DODAJ;)?( GRUPA PRIJAVI;)?( GRUPA ODJAVI;)?( GRUPA AKTIVIRAJ;)?( GRUPA BLOKIRAJ;)?( GRUPA STANJE;)?( GRUPA AERODROMI [a-z|A-Z|0-9|_|-|!|#|*|,|-]{1,500};)?";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(vrijednost);
        if (m.matches()) {
            String[] komande = vrijednost.split(";");
            if (komande.length == 2) {
                ProvjeriKorisnikaBaza(komande);
            } else {
                ObradiKomanduGrupe(komande);
            }
        } else {
            //kriva komanda
        }
    }

    private void ProvjeriKorisnikaBaza(String[] komande) {
        KorisnikDAO korisnik = new KorisnikDAO(konf);
        String korisnickoIme = komande[0].substring(9);
        String lozinka = komande[1].substring(9);
        boolean postoji = korisnik.ProvjeriPostojanjeKorisnika(korisnickoIme, lozinka);
        if (postoji) {
            odgovorServera = "OK 10;";
        } else {
            odgovorServera = "ERR 11;";
        }
    }

    private void ObradiKomandu(String[] komande) {
        String korisnickoIme = komande[0].substring(9);
        String lozinka = komande[1].substring(9);
        KorisnikDAO korisnik = new KorisnikDAO(konf);
        boolean postoji = korisnik.ProvjeriPostojanjeKorisnika(korisnickoIme, lozinka);
        if (komande[2].contains("DODAJ")) {
            odgovorServera = korisnik.RegistrirajKorisnika(korisnickoIme, lozinka);
        }
        if (postoji) {
            if (komande[2].contains("PAUZA")) {
                promijeniStanje();
            }
            if (komande[2].contains("RADI")) {
                promijeniStanjeRadi();
            }
            if (komande[2].contains("KRAJ")) {
                prekiniSvaPreuzimanja();
            }
            if (komande[2].contains("STANJE")) {
                posaljiStanje();
            }
        }
    }

    private void prekiniSvaPreuzimanja() {
        System.out.println("Stigla komanda kraj!");
        preuzimanjePodataka = false;
        pd.posluziteljRadi = preuzimanjePodataka;
        pd.dretvaRadi = false;
        pd.interrupt();
        radi = false;
        this.interrupt();
        System.exit(1);
    }

    private void promijeniStanje() {
        if (preuzimanjePodataka) {
            preuzimanjePodataka = false;
            pd.posluziteljRadi = preuzimanjePodataka;
            odgovorServera = "OK 10;";
        } else {
            odgovorServera = "ERR 13;";
        }

    }

    private void promijeniStanjeRadi() {
        if (!preuzimanjePodataka) {
            preuzimanjePodataka = true;
            pd.posluziteljRadi = preuzimanjePodataka;
            odgovorServera = "OK 10;";
        } else {
            odgovorServera = "ERR 13;";
        }
    }

    private void posaljiStanje() {
        if (preuzimanjePodataka) {
            odgovorServera = "OK 11;";
        } else {
            odgovorServera = "OK 12;";
        }
    }

    private void ObradiKomanduGrupe(String[] komande) {
        String korisnickoIme = komande[0].substring(9);
        String lozinka = komande[1].substring(9);
        KorisnikDAO korisnik = new KorisnikDAO(konf);
        boolean postoji = korisnik.ProvjeriPostojanjeKorisnika(korisnickoIme, lozinka);
        if (postoji) {
            ProvjeriIzvrsiTipKomande(komande[2]);
        }
    }

    private void ProvjeriIzvrsiTipKomande(String komanda) {
        if (komanda.contains("PRIJAVI")) {
            prijaviGrupu(korisnickoIme, lozinka);
        }
        if (komanda.contains("ODJAVI")) {
            odjaviGrupu(korisnickoIme, lozinka);
        }
        if (komanda.contains("AKTIVIRAJ")) {
            aktivirajGrupu(korisnickoIme, lozinka);
        }
        if (komanda.contains("BLOKIRAJ")) {
            blokirajGrupu(korisnickoIme, lozinka);
        }
        if (komanda.contains("STANJE")) {
            dohvatiStanjeGrupe(korisnickoIme, lozinka);
        }
        if (komanda.contains("AERODROMI")) {
            dodajAerodrome(korisnickoIme, lozinka, komanda);
        }
    }

    private void dodajAerodrome(String user, String lozinka, String komanda) {
        NWTiS_2020_WS nWTiS_2020_WS = new NWTiS_2020_WS();
        String komandaObradena = komanda.substring(17);
        String[] aerodromi = komandaObradena.split(",");
        StatusKorisnika status = nWTiS_2020_WS.dajStatusGrupe(user, lozinka);
        if (status.toString() == "BLOKIRAN") {
            boolean obrisano = nWTiS_2020_WS.obrisiPostojeceAerodrome(user, lozinka);
            if (obrisano) {
                dodajAerodromGrupi(user, lozinka, aerodromi);
            } else {
                odgovorServera = "ERR 32;";
            }
        } else {
            odgovorServera = "ERR 31;";
        }
    }

    private void dodajAerodromGrupi(String user, String lozinka, String[] aerodromi) {
        NWTiS_2020_WS nWTiS_2020_WS = new NWTiS_2020_WS();
        boolean dodano = true;
        for (String aerodrom : aerodromi) {
            dodano = nWTiS_2020_WS.dodajAerodromGrupi(user, lozinka, aerodrom);
        }
        if (!dodano) {
            odgovorServera = "OK 20;";
        } else {
            odgovorServera = "ERR 32;";
        }
    }

    private void blokirajGrupu(String user, String lozinka) {
        NWTiS_2020_WS nWTiS_2020_WS = new NWTiS_2020_WS();
        StatusKorisnika status = nWTiS_2020_WS.dajStatusGrupe(user, lozinka);
        if (status.toString() == "AKTIVAN") {
            nWTiS_2020_WS.blokirajGrupu(user, lozinka);
            odgovorServera = "OK 20;";
        }
        if (status.toString() == "REGISTRIRAN") {
            odgovorServera = "ERR 23;";
        }
        if (status.toString() == "NEAKTIVAN") {
            odgovorServera = "ERR 23;";
        }
        if (status.toString() == "NEPOSTOJI") {
            odgovorServera = "ERR 21;";
        }

    }

    private void aktivirajGrupu(String user, String lozinka) {
        NWTiS_2020_WS nWTiS_2020_WS = new NWTiS_2020_WS();
        StatusKorisnika status = nWTiS_2020_WS.dajStatusGrupe(user, lozinka);
        if (status.toString() == "REGISTRIRAN" || status.toString() == "BLOKIRAN") {
            boolean aktiviran = nWTiS_2020_WS.aktivirajGrupu(user, lozinka);
            if (aktiviran) {
                odgovorServera = "OK 20;";
            } else {
                odgovorServera = "ERR 21;";
            }
        }

    }

    private void odjaviGrupu(String user, String lozinka) {
        NWTiS_2020_WS nWTiS_2020_WS = new NWTiS_2020_WS();
        boolean odjavljena = nWTiS_2020_WS.odjaviGrupu(user, lozinka);
        if (odjavljena) {
            odgovorServera = "OK 20;";
        } else {
            odgovorServera = "ERR 21";
        }
    }

    private void prijaviGrupu(String user, String lozinka) {
        NWTiS_2020_WS nWTiS_2020_WS = new NWTiS_2020_WS();
        boolean prijava = nWTiS_2020_WS.prijaviGrupu(user, lozinka);
        System.out.println(nWTiS_2020_WS.dajStatusGrupe(user, lozinka));
        if (prijava) {
            boolean registrirana = nWTiS_2020_WS.registriraj(user, lozinka);
            System.out.println(nWTiS_2020_WS.dajStatusGrupe(user, lozinka));
            if (registrirana) {
                odgovorServera = "OK 20;";
            } else {
                odgovorServera = "ERR 20";
            }
        } else {
            odgovorServera = "ERR 20";
        }

    }

    private void dohvatiStanjeGrupe(String user, String lozinka) {
        NWTiS_2020_WS nWTiS_2020_WS = new NWTiS_2020_WS();
        StatusKorisnika status = nWTiS_2020_WS.dajStatusGrupe(user, lozinka);
        System.out.println(nWTiS_2020_WS.dajStatusGrupe(user, lozinka));
        if (status.toString() == "AKTIVAN") {
            odgovorServera = "OK 21;";
        }
        if (status.toString() == "BLOKIRAN") {
            odgovorServera = "OK 22;";
        }
        if (status.toString() == "NEPOSTOJI") {
            odgovorServera = "ERR 21;";
        }
    }
}

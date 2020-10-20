/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.zrna;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.igradiski.ejb.eb.Myairports;
import org.foi.nwtis.igradiski.ejb.sb.MyairportsFacade;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Korisnik
 */
@Named(value = "statusPosluzitelja")
@ViewScoped
public class statusPosluzitelja implements Serializable {

    @Inject
    PrijavaKorisnika prijavaKorisnika;

    @Getter
    @Setter
    String stanjePosluzitelja = "";

    @Getter
    @Setter
    String stanjeGrupe = "";

    @Inject
    ServletContext context;

    @Inject
    MyairportsFacade myairportsFacade;

    BP_Konfiguracija konf;
    private int port;

    public void dodajAerodrome() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String aerodromi = dohvatiAerodrome(korisnik).substring(1);
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA AERODROMI " + aerodromi + ";";
        PokreniSlusanjeSocketa(komanda);
    }

    public String dohvatiAerodrome(String korisnik) {
        String aerodromi = "";
        List<Myairports> lista = new ArrayList<>();
        lista = myairportsFacade.findAll();
        for (Myairports a : lista) {
            if (a.getUsername().equals(korisnik)) {
                aerodromi += "," + a.getIdent().getIdent();
            }
        }
        return aerodromi;
    }

    public void prijaviGrupu() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA PRIJAVI;";
        PokreniSlusanjeSocketa(komanda);
    }

    public void blokirajGrupu() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA BLOKIRAJ;";
        PokreniSlusanjeSocketa(komanda);
    }

    public void aktivirajGrupu() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA AKTIVIRAJ;";
        PokreniSlusanjeSocketa(komanda);
    }

    public void odjaviGrupu() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA ODJAVI;";
        PokreniSlusanjeSocketa(komanda);
    }

    public void dohvatiStanjeGrupe() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA STANJE;";
        PokreniSlusanjeSocketa(komanda);
    }

    public String dohvatiStanje() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; STANJE;";
        PokreniSlusanjeSocketa(komanda);
        return stanjePosluzitelja;
    }

    public void prebaciPosluziteljaUPasivniMod() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; PAUZA;";
        PokreniSlusanjeSocketa(komanda);
        dohvatiStanje();
    }

    public void posaljiKomanduKraj() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; KRAJ;";
        PokreniSlusanjeSocketa(komanda);
    }

    public void prebaciPosluziteljaUAktivniMod() {
        String korisnik = prijavaKorisnika.getKorisnik();
        String lozinka = prijavaKorisnika.getLozinka();
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; RADI;";
        PokreniSlusanjeSocketa(komanda);
        dohvatiStanje();
    }

    public void PokreniSlusanjeSocketa(String komanda) {
        dohvatiPostavkeZaSocket();
        try {
            Socket veza = new Socket("localhost", port);
            InputStream inps = veza.getInputStream();
            OutputStream outs = veza.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(outs);
            osw.write(komanda);
            osw.flush();
            veza.shutdownOutput();
            StringBuilder tekst = new StringBuilder();
            while (true) {
                int i = inps.read();
                if (i == -1) {
                    break;
                }
                tekst.append((char) i);
            }
            System.out.println("Primljeno je : " + tekst.toString());
            obradiOdgovor(tekst.toString(), komanda);
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void obradiOdgovor(String odgovor, String komanda) {
        if (odgovor.contains("OK") && komanda.contains("STANJE") && !komanda.contains("GRUPA")) {
            if (odgovor.contains("11")) {
                stanjePosluzitelja = "preuzima podatke za aerodrom";
            } else {
                stanjePosluzitelja = "ne preuzima podatke za aerodrom";
            }
        }
        if (komanda.contains("GRUPA")) {
            obradiOdgovorGrupe(odgovor, komanda);
        }
    }

    public void obradiOdgovorGrupe(String odgovor, String komanda) {
        if (komanda.contains("PRIJAVI")) {
            if (odgovor.contains("OK")) {
                stanjeGrupe = "Grupa je registrirana";
            } else {
                stanjeGrupe = "Grupa je vec registrirana";
            }
        }
        if (komanda.contains("ODJAVI")) {
            if (odgovor.contains("OK")) {
                stanjeGrupe = "Grupa je DEregistrirana";
            } else {
                stanjeGrupe = "Grupa nije registrirana";
            }
        }
        if (komanda.contains("AKTIVIRAJ")) {
            if (odgovor.contains("OK")) {
                stanjeGrupe = "Grupa je aktivirana";
            } else {
                stanjeGrupe = "Grupa nije aktivirana";
            }
        }
        if (komanda.contains("BLOKIRAJ")) {
            if (odgovor.contains("OK")) {
                stanjeGrupe = "Grupa je BLOKIRANA";
            } else {
                obradiBlokiraj(odgovor);
            }
        }
        if (komanda.contains("STANJE")) {
            if (odgovor.contains("OK")) {
                obradiStanjeOK(odgovor);
            } else {
                stanjeGrupe = "Grupa ne postoji";
            }
        }
        if (komanda.contains("AERODROMI")) {
            obradiOdgovorAerodromi(odgovor);
        }
    }

    public void obradiOdgovorAerodromi(String odgovor) {
        if (odgovor.contains("OK")) {
            stanjeGrupe = "Aerodromi su dodani!";
        } else {
            if (odgovor.contains("31")) {
                stanjeGrupe = "Grupa nije blokirana";
            }
            if (odgovor.contains("32")) {
                stanjeGrupe = "Greska kod unosa aerodroma!";
            }
        }
    }

    public void obradiStanjeOK(String odgovor) {
        if (odgovor.contains("21")) {
            stanjeGrupe = "Grupa je aktivna";
        } else {
            stanjeGrupe = "Grupa blokirana";
        }
    }

    public void obradiBlokiraj(String odgovor) {
        if (odgovor.contains("23")) {
            stanjeGrupe = "Grupa nije bila aktivan";
        } else {
            stanjeGrupe = "Grupa ne postoji";
        }
    }

    public void dohvatiPostavkeZaSocket() {
        konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        port = Integer.parseInt(konf.getKonfig().dajPostavku("posluzitelj.port"));
    }
}

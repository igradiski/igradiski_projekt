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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.igradiski.ejb.eb.Dnevnik;
import org.foi.nwtis.igradiski.ejb.eb.Korisnici;
import org.foi.nwtis.igradiski.ejb.eb.MqttZapis;
import org.foi.nwtis.igradiski.ejb.sb.DnevnikFacade;
import org.foi.nwtis.igradiski.ejb.sb.KorisniciFacade;
import org.foi.nwtis.igradiski.ejb.sb.MqttZapisFacade;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Korisnik
 */
@Named(value = "prijavaKorisnika")
@SessionScoped
public class PrijavaKorisnika implements Serializable {

    @Getter
    @Setter
    private String korisnik;
    @Getter
    @Setter
    private String lozinka;
    @Getter
    @Setter
    private String korisnikRegistracija;
    @Getter
    @Setter
    private String lozinkaRegistracija;

    @Getter
    @Setter
    private int brojRedova;

    @Getter
    @Setter
    private boolean tocniPodaciKorisnika = false;

    @Getter
    @Setter
    private String prijavaString = "Prijava";

    @Inject
    ServletContext context;

    @Inject
    KorisniciFacade korisniciFacade;

    @Inject
    DnevnikFacade dnevnikFacade;

    @Inject
    MqttZapisFacade mqttZapisFacade;

    @Getter
    List<MqttZapis> poruke = new ArrayList<>();

    @Getter
    List<Korisnici> korisnici = new ArrayList<>();

    @Getter
    List<Dnevnik> dnevnici = new ArrayList<>();

    String mojePoruke = "";
    BP_Konfiguracija konf;
    private int port;

    public void PrijaviKorisnika() {
        konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        brojRedova = Integer.parseInt(konf.getKonfig().dajPostavku("tablice.zapisi"));
        mojePoruke = konf.getKonfig().dajPostavku("moje.poruke");
        String komanda = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + ";";
        PokreniSlusanjeSocketa(komanda);
        ProvjeriPrijavu();
        DohvatiPorukeKorisnika();
    }

    public int brojRedovaTablice() {
        konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        brojRedova = Integer.parseInt(konf.getKonfig().dajPostavku("tablice.zapisi"));
        return brojRedova;
    }

    public void ProvjeriPrijavu() {
        if (korisnik.length() > 0 && lozinka.length() > 0) {
            prijavaString = "odjava";
        } else {
            prijavaString = "Prijava";
        }
    }

    public void obrisiMQTTPoruku(MqttZapis poruka) {
        poruke.remove(poruka);
        mqttZapisFacade.remove(poruka);
        poruke.clear();
        DohvatiPorukeKorisnika();

    }

    public void RegistrirajKorisnika() {
        if (!korisnikRegistracija.isEmpty() && !lozinkaRegistracija.isEmpty()) {
            if (korisnikRegistracija.length() >= 6 && lozinkaRegistracija.length() >= 6) {
                String komanda = "KORISNIK " + korisnikRegistracija + "; LOZINKA " + lozinkaRegistracija + "; DODAJ;";
                PokreniSlusanjeSocketa(komanda);
            }
        }
        registrirajKorisnikaEJB(korisnikRegistracija, lozinkaRegistracija);
        ProvjeriPrijavu();
    }

    public void registrirajKorisnikaEJB(String user, String lozinka) {
        List<Korisnici> lista = korisniciFacade.findAll();
        boolean dodavanje = true;
        for (Korisnici k : lista) {
            if (k.getKorisnickoIme().equals(user)) {
                dodavanje = false;
            }
        }
        if (dodavanje) {
            Korisnici korisnik = new Korisnici();
            korisnik.setKorisnickoIme(user);
            korisnik.setLozinka(lozinka);
            korisniciFacade.create(korisnik);
            korisnici = korisniciFacade.findAll();
        }
    }

    public void dohvatiPostavkeZaSocket() {
        konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        port = Integer.parseInt(konf.getKonfig().dajPostavku("posluzitelj.port"));
        korisnici = korisniciFacade.findAll();
        DohvatiPodatkeDnevnika();
        DohvatiPorukeKorisnika();
        ProvjeriPrijavu();
    }

    public void DohvatiPodatkeDnevnika() {
        List<Dnevnik> dnevniciPomocna = new ArrayList<>();
        dnevniciPomocna = dnevnikFacade.findAll();
        for (Dnevnik d : dnevniciPomocna) {
            if (d.getUsername().equals(korisnik)) {
                dnevnici.add(d);
            }
        }
    }

    public void DohvatiPorukeKorisnika() {
        poruke.clear();
        List<MqttZapis> porukePomocna = new ArrayList<>();
        porukePomocna = mqttZapisFacade.findAll();
        for (MqttZapis p : porukePomocna) {
            if (p.getKorisnik().equals(mojePoruke)) {
                poruke.add(p);
            }
        }
    }

    public void obrisiSvePoruke() {
        List<MqttZapis> porukePomocna = new ArrayList<>();
        porukePomocna = mqttZapisFacade.findAll();
        for (MqttZapis p : porukePomocna) {
            mqttZapisFacade.remove(p);
        }
        poruke.clear();
        DohvatiPorukeKorisnika();
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
            obradiOdgovor(tekst.toString());
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void obradiOdgovor(String odgovor) {
        if (odgovor.contains("10")) {
            tocniPodaciKorisnika = true;
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("glavniIzbornik.xhtml");
                korisnici = korisniciFacade.findAll();
            } catch (IOException ex) {
                Logger.getLogger(PrijavaKorisnika.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void odjavaKorisnika() {
        if (korisnik.length() > 0 && lozinka.length() > 0) {
            korisnik = "";
            lozinka = "";
            tocniPodaciKorisnika = false;
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(PrijavaKorisnika.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ProvjeriPrijavu();
    }

}

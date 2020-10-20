/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.igradiski.ejb.eb.Airplanes;
import org.foi.nwtis.igradiski.ejb.eb.Airports;
import org.foi.nwtis.igradiski.ejb.eb.Myairports;
import org.foi.nwtis.igradiski.ejb.eb.Myairportslog;
import org.foi.nwtis.igradiski.ejb.sb.AirplanesFacade;
import org.foi.nwtis.igradiski.ejb.sb.AirportsFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportsFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportslogFacade;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.igradiski.servisi.rest.aerodromiREST;
import org.foi.nwtis.podaci.OdgovorAerodrom;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

/**
 *
 * @author Korisnik
 */
@Named(value = "prikazAerodromaApp4")
@ViewScoped
public class PrikazAerodromaApp4 implements Serializable {

    @Inject
    ServletContext context;

    @Inject
    PrijavaKorisnikaApp4 prijava;

    @Inject
    MyairportsFacade myairportsFacade;

    @Inject
    MyairportslogFacade myairportsLogFacade;

    @Inject
    AirplanesFacade airplanesFacade;

    @Inject
    AirportsFacade airportsFacade;

    @Getter
    @Setter
    int brojRedovaTablice;

    @Getter
    @Setter
    String geoSirina = "";

    @Getter
    @Setter
    String geoDuzina = "";

    @Getter
    @Setter
    String temperatura = "";

    @Getter
    @Setter
    String imeAerodroma = "";

    @Getter
    @Setter
    String vlaga = "";

    @Getter
    @Setter
    String odabraniAerodrom = "";

    @Getter
    @Setter
    List<Myairports> listaMojihAerodroma = new ArrayList<>();

    @Getter
    @Setter
    String odgovorRest = "";

    @Getter
    @Setter
    List<Airports> listaAerodroma = new ArrayList<>();

    List<Airplanes> listaSvihAviona = new ArrayList<>();

    BP_Konfiguracija konf;

    public int dohvatiBrojRedova() {
        konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        int brojRedova = Integer.parseInt(konf.getKonfig().dajPostavku("tablice.zapisi"));
        brojRedovaTablice = brojRedova;
        return brojRedova;

    }

    public Object povratakNaPrijavu() {
        System.out.println("Povratak na prijavu");
        return "";
    }

    @PostConstruct
    public void dohvatiMojeAerodrome() {
        listaAerodroma = airportsFacade.findAll();
        konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        int brojRedova = Integer.parseInt(konf.getKonfig().dajPostavku("tablice.zapisi"));
        brojRedovaTablice = brojRedova;
        List<Myairports> lista = new ArrayList<>();
        lista = myairportsFacade.findAll();
        if (prijava.getKorisnik() != null && prijava.getLozinka() != null) {
            for (Myairports airMyairports : lista) {
                if (airMyairports.getUsername().equals(prijava.getKorisnik())) {
                    listaMojihAerodroma.add(airMyairports);
                }
            }
        }
    }

    public int dohvatiBrojKorisnika(Myairports a) {
        List<Myairports> lista = new ArrayList<>();
        lista = myairportsFacade.findAll();
        int broj = 0;
        for (Myairports airport : lista) {
            if (airport.getIdent().getIdent().equals(a.getIdent().getIdent())) {
                broj++;
            }
        }
        return broj;
    }

    public int dohvatiBrojDana(Myairports a) {
        int broj = 0;
        List<Myairportslog> lista = new ArrayList<>();
        lista = myairportsLogFacade.findAll();
        for (Myairportslog log : lista) {
            if (log.getAirports().getIdent().equals(a.getIdent().getIdent())) {
                broj++;
            }
        }
        return broj;
    }

    public int dohvatiBrojLetova(Myairports a) {
        int broj = 0;
        List<Myairportslog> listaLogova = a.getIdent().getMyairportslogList();
        for (Myairportslog log : listaLogova) {
            broj += log.getBrojLetova();
        }
        return broj;
    }

    public void prikaziPodatkeAerodroma(Myairports a) {
        dohvatiSirinuDuzinu(a);
        dohvatiMeteo(a, geoSirina, geoDuzina);
    }

    public void dohvatiMeteo(Myairports a, String sirina, String duzina) {
        String apiKey = konf.getKonfig().dajPostavku("OpenWeatherMap.apikey");
        OWMKlijent klijent = new OWMKlijent(apiKey);
        MeteoPodaci podaci = klijent.getRealTimeWeather(sirina, duzina);
        temperatura = podaci.getTemperatureValue().toString();
        vlaga = podaci.getHumidityValue().toString();
    }

    public void dohvatiSirinuDuzinu(Myairports a) {
        String apiKey = konf.getKonfig().dajPostavku("LocationIQ.token");
        LIQKlijent klijent = new LIQKlijent(apiKey);
        Lokacija l = klijent.getGeoLocation(a.getIdent().getIdent());
        geoSirina = l.getLatitude();
        geoDuzina = l.getLongitude();
    }

    public void dohvatiAerodromeIme() {
        listaAerodroma = airportsFacade.pretraziPremaImenu(odabraniAerodrom);
    }

    public void dodajAerodromUBazu() {
        System.out.println("Dodavanje+" + imeAerodroma);
        aerodromiREST aerREST = new aerodromiREST(prijava.getKorisnik(), prijava.getLozinka());
        aerREST.dajAerodomeKorisnika(OdgovorAerodrom.class, imeAerodroma);
        System.out.println("Dodano!");
    }

}

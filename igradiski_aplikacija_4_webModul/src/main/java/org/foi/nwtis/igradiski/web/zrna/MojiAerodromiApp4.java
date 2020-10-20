/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.igradiski.servisi.rest.aerodromiREST;
import org.foi.nwtis.igradiski.servisi.soap.App2WS;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.OdgovorAerodrom;

/**
 *
 * @author Korisnik
 */
@Named(value = "mojiAerodromiApp4")
@ViewScoped
public class MojiAerodromiApp4 implements Serializable {

    @Inject
    ServletContext context;

    @Inject
    PrijavaKorisnikaApp4 prijava;

    @Getter
    @Setter
    List<Aerodrom> listaMojihAerodroma = new ArrayList<>();

    @Getter
    @Setter
    List<org.foi.nwtis.igradiski.web.serveri.Aerodrom> avioniUdaljenost = new ArrayList<>();

    @Getter
    @Setter
    String odabraniAerodrom = "";

    @Getter
    @Setter
    String odabraniAerodromUdaljenost = "";

    @Getter
    @Setter
    String minimalnaUdaljenost = "";

    @Getter
    @Setter
    String udaljenostIzmeduAerodroma = "";

    @Getter
    @Setter
    String maxUdaljenost = "";

    public Object povratakNaPrijavu() {
        System.out.println("Povratak na prijavu");
        return "";
    }

    @PostConstruct
    public void dohvatiMojeAerodrome() {
        String korisnik = prijava.getKorisnik();
        String lozinka = prijava.getLozinka();
        System.out.println(korisnik + " " + lozinka);
        aerodromiREST rest = new aerodromiREST(korisnik, lozinka);
        OdgovorAerodrom odgovor = rest.dohvatiMojeAerodrome(OdgovorAerodrom.class);
        listaMojihAerodroma = Arrays.asList(odgovor.getOdgovor());
    }

    public void obrisiAvione() {
        System.out.println("Aerodrom: " + odabraniAerodrom);
        String korisnik = prijava.getKorisnik();
        String lozinka = prijava.getLozinka();
        aerodromiREST rest = new aerodromiREST(korisnik, lozinka);
        rest.obrisiAvioneIzBaze(OdgovorAerodrom.class, odabraniAerodrom);
    }

    public void obrisiAerodrom() {
        System.out.println("Aerodrom: " + odabraniAerodrom);
    }

    public void dohvatiAerodromePremaUdaljenosti() {
        String korisnik = prijava.getKorisnik();
        String lozinka = prijava.getLozinka();
        int min = Integer.parseInt(minimalnaUdaljenost);
        int max = Integer.parseInt(maxUdaljenost);
        App2WS ws = new App2WS();
        avioniUdaljenost = ws.dohvatiAerodromeUdaljenost(korisnik, lozinka, odabraniAerodrom, min, max);
        System.out.println(avioniUdaljenost.size());
    }

    public void dohvatiUdaljenost() {
        String korisnik = prijava.getKorisnik();
        String lozinka = prijava.getLozinka();
        App2WS ws = new App2WS();
        double duzina = ws.dohvatiUdaljenost(korisnik, lozinka, odabraniAerodrom, odabraniAerodromUdaljenost);
        udaljenostIzmeduAerodroma = Double.toString(duzina);
    }

}

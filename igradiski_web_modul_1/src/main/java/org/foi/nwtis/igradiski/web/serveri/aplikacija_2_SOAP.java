/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.serveri;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import org.foi.nwtis.igradiski.ejb.eb.Dnevnik;
import org.foi.nwtis.igradiski.ejb.sb.AirplanesFacade;
import org.foi.nwtis.igradiski.ejb.sb.AirportsFacade;
import org.foi.nwtis.igradiski.ejb.sb.DnevnikFacade;
import org.foi.nwtis.igradiski.ejb.sb.KorisniciFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportsFacade;
import org.foi.nwtis.igradiski.podaci.AerodromiPodaciSOAP;
import org.foi.nwtis.igradiski.podaci.AvionPodaciSOAP;
import org.foi.nwtis.igradiski.podaci.KorisnikPodaciSOAP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author Korisnik
 */
@WebService(serviceName = "aplikacija_2_SOAP")
public class aplikacija_2_SOAP {

    @Inject
    ServletContext context;

    @Inject
    AirportsFacade airportsFacade;

    @Inject
    KorisniciFacade korisniciFacade;

    @Inject
    MyairportsFacade myairportsFacade;

    @Inject
    AirplanesFacade airplanesFacade;

    @Inject
    DnevnikFacade dnevnikFacade;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "popisAerodromaPoNazivu")
    public List<Aerodrom> popisAerodromaPoNazivu(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "naziv") String naziv) {
        long pocetak = System.currentTimeMillis();
        List<Aerodrom> lista = new ArrayList<Aerodrom>();
        KorisnikPodaciSOAP kpsoap = new KorisnikPodaciSOAP(korisniciFacade);
        if (kpsoap.ProvjeriPodatkeKorisnika(korisnik, lozinka)) {
            AerodromiPodaciSOAP apsoap = new AerodromiPodaciSOAP(airportsFacade, korisniciFacade);
            lista = apsoap.popisAerodromaPoNazivu(korisnik, lozinka, naziv);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_web_modul_1/aplikacija_2_SOAP";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return lista;
    }

    @WebMethod(operationName = "popisAerodromaPoDrzavi")
    public List<Aerodrom> popisAerodromaPoDrzavi(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "drzava") String drazava) {
        long pocetak = System.currentTimeMillis();
        KorisnikPodaciSOAP kpsoap = new KorisnikPodaciSOAP(korisniciFacade);
        List<Aerodrom> lista = new ArrayList<Aerodrom>();
        if (kpsoap.ProvjeriPodatkeKorisnika(korisnik, lozinka)) {
            AerodromiPodaciSOAP apsoap = new AerodromiPodaciSOAP(airportsFacade, korisniciFacade);
            lista = apsoap.popisAerodromaPoDrzavi(korisnik, lozinka, drazava);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_web_modul_1/aplikacija_2_SOAP";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return lista;
    }

    @WebMethod(operationName = "popisVlastitihAerodroma")
    public List<Aerodrom> popisVlastitihAerodroma(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka) {
        long pocetak = System.currentTimeMillis();
        List<Aerodrom> lista = new ArrayList<Aerodrom>();
        KorisnikPodaciSOAP kpsoap = new KorisnikPodaciSOAP(korisniciFacade);
        if (kpsoap.ProvjeriPodatkeKorisnika(korisnik, lozinka)) {
            AerodromiPodaciSOAP apsoap = new AerodromiPodaciSOAP(airportsFacade, korisniciFacade);
            lista = apsoap.popisVlastitihAerodroma(korisnik, lozinka, myairportsFacade, korisnik);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_web_modul_1/aplikacija_2_SOAP";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return lista;
    }

    @WebMethod(operationName = "popisLetovaInterval")
    public List<AvionLeti> popisLetovaInterval(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "vrijemeOD") int vrijemeOD,
            @WebParam(name = "vrijemeDO") int vrijemeDO) {
        long pocetak = System.currentTimeMillis();
        List<AvionLeti> lista = new ArrayList<AvionLeti>();
        KorisnikPodaciSOAP kpsoap = new KorisnikPodaciSOAP(korisniciFacade);
        if (kpsoap.ProvjeriPodatkeKorisnika(korisnik, lozinka)) {
            AvionPodaciSOAP apsoap = new AvionPodaciSOAP(airportsFacade, korisniciFacade, airplanesFacade);
            lista = apsoap.popisLetovaInterval(icao, vrijemeOD, vrijemeDO);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_web_modul_1/aplikacija_2_SOAP";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return lista;
    }

    @WebMethod(operationName = "popisLetovaAvionInterval")
    public List<AvionLeti> popisLetovaAvionInterval(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "vrijemeOD") int vrijemeOD,
            @WebParam(name = "vrijemeDO") int vrijemeDO) {
        long pocetak = System.currentTimeMillis();
        List<AvionLeti> lista = new ArrayList<AvionLeti>();
        KorisnikPodaciSOAP kpsoap = new KorisnikPodaciSOAP(korisniciFacade);
        if (kpsoap.ProvjeriPodatkeKorisnika(korisnik, lozinka)) {
            AvionPodaciSOAP apsoap = new AvionPodaciSOAP(airportsFacade, korisniciFacade, airplanesFacade);
            lista = apsoap.popisLetovaAvionInterval(icao, vrijemeOD, vrijemeDO);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_web_modul_1/aplikacija_2_SOAP";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return lista;
    }

    @WebMethod(operationName = "udaljenostIzmeduDvaAerodroma")
    public double udaljenostIzmeduDvaAerodroma(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao1") String icao1,
            @WebParam(name = "icao2") String icao2) {
        long pocetak = System.currentTimeMillis();
        AerodromiPodaciSOAP apsoap = new AerodromiPodaciSOAP(airportsFacade, korisniciFacade);
        KorisnikPodaciSOAP kpsoap = new KorisnikPodaciSOAP(korisniciFacade);
        double udaljenost = 0;
        if (kpsoap.ProvjeriPodatkeKorisnika(korisnik, lozinka)) {
            udaljenost = apsoap.izracunajUdaljenost(icao1, icao2);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_web_modul_1/aplikacija_2_SOAP";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return udaljenost;
    }

    @WebMethod(operationName = "popisVlastitihAerodromaUGranicama")
    public List<Aerodrom> popisVlastitihAerodromaUGranicama(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "intervalOD") int intervalOD,
            @WebParam(name = "intervalDO") int intervalDO) {
        long pocetak = System.currentTimeMillis();
        AerodromiPodaciSOAP apsoap = new AerodromiPodaciSOAP(airportsFacade, korisniciFacade);
        List<Aerodrom> lista = new ArrayList<Aerodrom>();
        KorisnikPodaciSOAP kpsoap = new KorisnikPodaciSOAP(korisniciFacade);
        if (kpsoap.ProvjeriPodatkeKorisnika(korisnik, lozinka)) {
            lista = apsoap.dohvatiAerodromeKojiZadovoljavajuInterval(icao, intervalOD, intervalDO, myairportsFacade, korisnik);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_web_modul_1/aplikacija_2_SOAP";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return lista;
    }

    public void StvoriZapisDnevnik(String url, String username, long trajanje) {
        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setUrlAdresa(url);
        dnevnik.setUsername(username);
        dnevnik.setTrajanjeObrade((int) trajanje);
        Date date = new Date();
        Timestamp vrijemePrijema = new Timestamp(date.getTime());
        dnevnik.setVrijemePrijema(vrijemePrijema);
        dnevnikFacade.create(dnevnik);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.rest.serveri;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.igradiski.ejb.eb.Airports;
import org.foi.nwtis.igradiski.ejb.eb.Dnevnik;
import org.foi.nwtis.igradiski.ejb.sb.AirplanesFacade;
import org.foi.nwtis.igradiski.ejb.sb.AirportsFacade;
import org.foi.nwtis.igradiski.ejb.sb.DnevnikFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportsFacade;
import org.foi.nwtis.igradiski.ws.klijenti.App2_SOAP_klijent;
import org.foi.nwtis.igradiski.web.serveri.Aerodrom;
import org.foi.nwtis.igradiski.ws.socketi.socketKlijentEndpoint_app3;
import org.foi.nwtis.podaci.Odgovor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author Korisnik
 */
@Path("app3_REST")
public class App3_RESTservis {

    @Inject
    DnevnikFacade dnevnikFacade;

    @Inject
    MyairportsFacade myairportsFacade;

    @Inject
    AirplanesFacade airplanesFacade;

    @Inject
    AirportsFacade airportsFacade;

    /**
     * Creates a new instance of App3_RESTservis
     */
    public App3_RESTservis() {
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodomeKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {
        long pocetak = System.currentTimeMillis();
        App2_SOAP_klijent asoap = new App2_SOAP_klijent();
        List<Aerodrom> aerodromi = new ArrayList<>();
        aerodromi = asoap.dajAerodromeKorisnika(korisnik, lozinka);
        Odgovor odgovor = new Odgovor();
        if (aerodromi == null || aerodromi.isEmpty()) {
            odgovor = kreirajOdgovor("40", "Greska kod dohvacanja aerodroma", aerodromi);
        } else {
            odgovor = kreirajOdgovor("10", "Aerodromi dohvaceni", aerodromi);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_aplikacija_3/webresources/app3_REST";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    @Path("/svi")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dohvatiAerodromeParametar(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("naziv") String nazivAerodroma,
            @QueryParam("drzava") String drzava) {
        long pocetak = System.currentTimeMillis();
        App2_SOAP_klijent asoap = new App2_SOAP_klijent();
        List<Aerodrom> aerodromi = new ArrayList<>();
        aerodromi = asoap.dohvatiAerodrome(korisnik, lozinka, nazivAerodroma, drzava, airportsFacade);
        Odgovor odgovor = new Odgovor();
        if (aerodromi.isEmpty()) {
            odgovor = kreirajOdgovor("40", "Nisu dohvacni aerodromi", aerodromi);
        } else {
            odgovor = kreirajOdgovor("10", "Podaci uspjesno dohvaceni", aerodromi);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_aplikacija_3/webresources/app3_REST" + "/svi";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodomeKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("icao") String icaoAerodrom) {
        Odgovor odgovor = new Odgovor();
        KreirajWSKlijenta(korisnik, icaoAerodrom);
        Aerodrom a = new Aerodrom();
        a.setIcao(icaoAerodrom);
        List<Aerodrom> aerodromi = new ArrayList<>();
        aerodromi.add(a);
        odgovor.setOdgovor(aerodromi.toArray());
        odgovor.setPoruka("Poslan zahtjev za dodavanje!");
        odgovor.setStatus("10");
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    public void KreirajWSKlijenta(String korisnik, String icao) {
        URI uri;
        try {
            uri = new URI("ws://localhost:8084/igradiski_web_modul_1/app2Socket");
            final socketKlijentEndpoint_app3 klijent = new socketKlijentEndpoint_app3(uri);
            klijent.sendMessage(korisnik + ";" + icao);
        } catch (URISyntaxException ex) {
            Logger.getLogger(App3_RESTservis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Odgovor kreirajOdgovor(String status, String poruka, List<Aerodrom> aerodromi) {
        Odgovor odgovor = new Odgovor();
        odgovor.setOdgovor(aerodromi.toArray());
        odgovor.setStatus(status);
        odgovor.setPoruka(poruka);
        return odgovor;
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

    @GET
    @Path("{icao}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dohvatiPodatkeAerodroma(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        long pocetak = System.currentTimeMillis();
        App2_SOAP_klijent asoap = new App2_SOAP_klijent();
        List<Aerodrom> aerodromi = new ArrayList<>();
        Odgovor odgovor = new Odgovor();
        aerodromi = asoap.dohvatiPodatkeAerodroma(korisnik, lozinka, icao, myairportsFacade);
        if (aerodromi.isEmpty()) {
            odgovor = kreirajOdgovor("40", "Greska kod dohvacanja aerodroma", aerodromi);
        } else {
            odgovor = kreirajOdgovor("10", "Izabrani aerodrom dohvaceni", aerodromi);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_aplikacija_3/webresources/app3_REST/" + icao;
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    @DELETE
    @Path("{icao}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response BrisanjePracenogAerodroma(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        long pocetak = System.currentTimeMillis();
        Odgovor odgovor = new Odgovor();
        App2_SOAP_klijent asoap = new App2_SOAP_klijent();
        Airports airport = asoap.dohvatiPraceniAerodrom(korisnik, lozinka, icao, myairportsFacade);
        boolean imaAerodrome = asoap.ProvjeriPostojanjeAerodroma(korisnik, lozinka, airport, airplanesFacade);
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (imaAerodrome) {
            kreirajOdgovor("40", "Ne moze se brisati jer ima aerodrome", aerodromi);
        } else {
            asoap.obrisiPraceniAerodrom(korisnik, lozinka, airport, myairportsFacade);
            kreirajOdgovor("10", "Aerodrom uspjesno obrisan", aerodromi);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_aplikacija_3/webresources/app3_REST/" + icao;
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    @DELETE
    @Path("{icao}/avioni")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response BrisanjeLetovaIzabranogAerodroma(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        long pocetak = System.currentTimeMillis();
        Odgovor odgovor = new Odgovor();
        App2_SOAP_klijent asoap = new App2_SOAP_klijent();
        Airports airport = asoap.dohvatiPraceniAerodrom(korisnik, lozinka, icao, myairportsFacade);
        List<Aerodrom> aerodromi = new ArrayList<>();
        boolean obrisano = asoap.ObrisiAvioneAerodroma(korisnik, lozinka, airport, airplanesFacade);
        if (obrisano) {
            kreirajOdgovor("10", "Avioni aerodroma obrisani", aerodromi);
        } else {
            kreirajOdgovor("40", "Brisanje nije uspjelo", aerodromi);
        }
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "http://localhost:8084/igradiski_aplikacija_3/webresources/app3_REST/" + icao + "/avioni";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.servisi.rest;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:App3_RESTservis
 * [app3_REST]<br>
 * USAGE:
 * <pre>
 *        aerodromiREST client = new aerodromiREST();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Korisnik
 */
public class aerodromiREST {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/igradiski_aplikacija_3_webModul/webresources";
    private String korisnik = "";
    private String lozinka = "";

    public aerodromiREST(String korisnik, String lozinka) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("app3_REST");
        this.korisnik = korisnik;
        this.lozinka = lozinka;
    }

    public <T> Response dajAerodomeKorisnika(Class<T> responseType, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        System.out.println("U servisu : " + icao);
        resource = resource.queryParam("icao", icao);
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .post(Entity.entity(icao, MediaType.APPLICATION_JSON));
    }

    public <T> T dohvatiMojeAerodrome(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        return (T) resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);

    }

    public <T> T obrisiAvioneAerodroma(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        return (T) resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);

    }

    public <T> T obrisiAvioneIzBaze(Class<T> responseType, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.queryParam("icao", icao);
        resource.path("/avioni");
        return (T) resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka).delete();

    }

    public void close() {
        client.close();
    }

}

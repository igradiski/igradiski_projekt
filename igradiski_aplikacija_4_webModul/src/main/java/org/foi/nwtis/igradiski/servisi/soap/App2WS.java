/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.servisi.soap;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.igradiski.web.serveri.Aerodrom;
import org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service;
import org.foi.nwtis.igradiski.web.serveri.AvionLeti;

/**
 *
 * @author Korisnik
 */
public class App2WS {

    public List<Aerodrom> dohvatiMojeAerodrome(String korisnik, String lozinka) {
        List<Aerodrom> lista = new ArrayList<>();
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service servis = new Aplikacija2SOAP_Service();
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP port = servis.getAplikacija2SOAPPort();
        lista = port.popisVlastitihAerodroma(korisnik, lozinka);
        return lista;
    }

    public List<AvionLeti> dohvatiLetove(String korisnik, String lozinka, String icao, int vrijemeOD, int vrijemDo) {
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service servis = new Aplikacija2SOAP_Service();
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP port = servis.getAplikacija2SOAPPort();
        List<AvionLeti> lista = port.popisLetovaInterval(korisnik, lozinka, icao, vrijemeOD, vrijemDo);
        System.out.println(lista.size());
        return lista;
    }

    public List<AvionLeti> dohvatiLetoveAviona(String korisnik, String lozinka, String icao, int vrijemeOD, int vrijemDo) {
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service servis = new Aplikacija2SOAP_Service();
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP port = servis.getAplikacija2SOAPPort();
        List<AvionLeti> lista = port.popisLetovaAvionInterval(korisnik, lozinka, icao, vrijemeOD, vrijemDo);
        return lista;
    }

    public List<Aerodrom> dohvatiAerodromeUdaljenost(String korisnik, String lozinka, String icao, int min, int max) {
        List<Aerodrom> lista = new ArrayList<>();
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service servis = new Aplikacija2SOAP_Service();
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP port = servis.getAplikacija2SOAPPort();
        lista = port.popisVlastitihAerodromaUGranicama(korisnik, lozinka, icao, min, max);
        return lista;
    }

    public double dohvatiUdaljenost(String korisnik, String lozinka, String icao1, String icao2) {
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service servis = new Aplikacija2SOAP_Service();
        org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP port = servis.getAplikacija2SOAPPort();
        double duzina = port.udaljenostIzmeduDvaAerodroma(korisnik, lozinka, icao1, icao2);
        return duzina;
    }

}

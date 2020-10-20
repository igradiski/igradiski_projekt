/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.podaci;

import java.sql.Timestamp;

/**
 *
 * @author Korisnik
 */
public class MQTTPoruka {

    private String korisnik;
    private String aerodrom;
    private String avion;
    private String oznaka;
    private String poruka;
    private String vrijeme;
    private Timestamp vrijemeTS;

    public MQTTPoruka() {
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getAerodrom() {
        return aerodrom;
    }

    public void setAerodrom(String aerodrom) {
        this.aerodrom = aerodrom;
    }

    public String getAvion() {
        return avion;
    }

    public void setAvion(String avion) {
        this.avion = avion;
    }

    public String getOznaka() {
        return oznaka;
    }

    public void setOznaka(String oznaka) {
        this.oznaka = oznaka;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public Timestamp getVrijemeTS() {
        return vrijemeTS;
    }

    public void setVrijemeTS(Timestamp vrijemeTS) {
        this.vrijemeTS = vrijemeTS;
    }

}

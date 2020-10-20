/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.zrna;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.igradiski.servisi.soap.App2WS;
import org.foi.nwtis.igradiski.web.serveri.Aerodrom;
import org.foi.nwtis.igradiski.web.serveri.AvionLeti;

/**
 *
 * @author Korisnik
 */
@Named(value = "pregledLetovaApp4")
@ViewScoped
public class PregledLetovaApp4 implements Serializable {

    @Inject
    PrijavaKorisnikaApp4 prijava;

    @Getter
    @Setter
    List<Aerodrom> listaMojihAerodroma = new ArrayList<>();

    @Getter
    @Setter
    List<AvionLeti> listaAvionaAerodroma = new ArrayList<>();

    @Getter
    @Setter
    List<AvionLeti> listaLetovaAviona = new ArrayList<>();

    @Getter
    @Setter
    String vrijemeOd = "04.03.2020 20:40";

    @Getter
    @Setter
    String vrijemeDo = "05.03.2020 20:40";

    public Object povratakNaPrijavu() {
        System.out.println("Povratak na prijavu");
        return "";
    }

    @PostConstruct
    public void dohvatiMojeAerodrome() {
        String korisnik = prijava.getKorisnik();
        String lozinka = prijava.getLozinka();
        App2WS app2WS = new App2WS();
        listaMojihAerodroma = app2WS.dohvatiMojeAerodrome(korisnik, lozinka);
    }

    public void dohvatiAvione(Aerodrom a) {
        Date dateOD = formatirajDatume(vrijemeOd);
        Date dateDO = formatirajDatume(vrijemeDo);
        int unixOD = (int) (dateOD.getTime() / 1000);
        int unixDO = (int) (dateDO.getTime() / 1000);
        String korisnik = prijava.getKorisnik();
        String lozinka = prijava.getLozinka();
        App2WS app2WS = new App2WS();
        System.out.println("1586206740= " + unixDO);
        System.out.println("Od: " + unixOD + " do: " + unixDO + " " + a.getIcao() + " " + korisnik + " " + lozinka);
        listaAvionaAerodroma = app2WS.dohvatiLetove(korisnik, lozinka, a.getIcao(), unixOD, unixDO);
        System.out.println("Ima ih: " + listaAvionaAerodroma.size());
    }

    public Date formatirajDatume(String vrijeme) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date1 = new Date();
        try {
            date1 = sdf.parse(vrijeme);
        } catch (ParseException ex) {
            System.out.println("Greska u parsanju!");
        }
        return date1;
    }

    public String preracunajUnixUDatum(int broj) {
        long unixSeconds = broj;
        Date date = new java.util.Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public void dohvatiLetAviona(AvionLeti a) {
        System.out.println("Dohvacam let aviona");
        String korisnik = prijava.getKorisnik();
        String lozinka = prijava.getLozinka();
        Date dateOD = formatirajDatume(vrijemeOd);
        Date dateDO = formatirajDatume(vrijemeDo);
        int unixOD = (int) (dateOD.getTime() / 1000);
        int unixDO = (int) (dateDO.getTime() / 1000);
        App2WS app2WS = new App2WS();
        listaLetovaAviona = app2WS.dohvatiLetoveAviona(korisnik, lozinka, a.getIcao24(), unixOD, unixDO);
        System.out.println("Lista size: " + listaLetovaAviona.size());
    }
}

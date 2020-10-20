/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.zrna;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.igradiski.ejb.eb.Korisnici;
import org.foi.nwtis.igradiski.ejb.sb.KorisniciFacade;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Korisnik
 */
@Named(value = "prijavaKorisnikaApp4")
@SessionScoped
public class PrijavaKorisnikaApp4 implements Serializable {

    @Inject
    ServletContext contextServlet;

    @Getter
    @Setter
    private String jezik = "hr";

    @Getter
    @Setter
    private String korisnik;

    @Getter
    @Setter
    private String lozinka;

    @Inject
    FacesContext context;

    @Inject
    KorisniciFacade korisniciFacade;

    BP_Konfiguracija konf;

    @PostConstruct
    public void dohvatiMojeAerodrome() {
        konf = (BP_Konfiguracija) contextServlet.getAttribute("BP_Konfig");
        int brojRedova = Integer.parseInt(konf.getKonfig().dajPostavku("tablice.zapisi"));
        System.out.println("broj redova: " + brojRedova);

    }

    public Object odaberiJezik() {
        System.out.println(jezik);
        context.getViewRoot().setLocale(new Locale(jezik));
        return "";
    }

    public Object povratakNaPrijavu() {
        return "";
    }

    public Object PrijaviKorisnika() {
        boolean postoji = false;
        List<Korisnici> listaKorisnika = korisniciFacade.findAll();
        for (Korisnici k : listaKorisnika) {
            if (k.getKorisnickoIme().equals(korisnik)
                    && k.getLozinka().equals(lozinka)) {
                postoji = true;
            }
        }
        if (postoji) {
            return "";
        } else {
            return null;
        }
    }

    public Object prikaziAerodrome() {
        return "";
    }

    public Object prikaziMojeAerodrome() {
        return "";
    }

    public Object prikaziPregledLetova() {
        return "";
    }

}

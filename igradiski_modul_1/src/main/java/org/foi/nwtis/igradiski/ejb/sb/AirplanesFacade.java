/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.foi.nwtis.igradiski.ejb.eb.Airplanes;
import org.foi.nwtis.igradiski.ejb.eb.Airports;

/**
 *
 * @author Korisnik
 */
@Stateless
public class AirplanesFacade extends AbstractFacade<Airplanes> {

    @PersistenceContext(unitName = "NWTiS_Projekt_PU")
    private EntityManager em;

    @Override
    protected synchronized EntityManager getEntityManager() {
        return em;
    }

    public AirplanesFacade() {
        super(Airplanes.class);
    }
    
    public List<Airplanes> pretraziPremaImenuAerodromaCAPI(String naziv) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Airplanes.class);
        Root<Airplanes> airplanes = cq.from(Airplanes.class);
        Expression<String> zaNaziv = airplanes.get("estdepartureairport");
        cq.where(cb.like(zaNaziv, naziv));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }
    
    public List<Airplanes> pretraziPremaImenuAvionaCAPI(String naziv) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Airplanes.class);
        Root<Airplanes> airplanes = cq.from(Airplanes.class);
        Expression<String> zaNaziv = airplanes.get("icao24");
        cq.where(cb.like(zaNaziv, naziv));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }
    
}

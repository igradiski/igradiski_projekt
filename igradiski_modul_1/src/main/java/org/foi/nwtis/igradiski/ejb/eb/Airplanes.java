/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Korisnik
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Airplanes.findAll", query = "SELECT a FROM Airplanes a")})
public class Airplanes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(nullable = false, length = 30)
    private String icao24;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int firstseen;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(nullable = false, length = 10)
    private String estdepartureairport;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int lastseen;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(nullable = false, length = 10)
    private String estarrivalairport;
    @Size(max = 20)
    @Column(length = 20)
    private String callsign;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int estdepartureairporthorizdistance;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int estdepartureairportvertdistance;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int estarrivalairporthorizdistance;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int estarrivalairportvertdistance;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int departureairportcandidatescount;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int arrivalairportcandidatescount;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date stored;

    public Airplanes() {
    }

    public Airplanes(Integer id) {
        this.id = id;
    }

    public Airplanes(Integer id, String icao24, int firstseen, String estdepartureairport, int lastseen, String estarrivalairport, int estdepartureairporthorizdistance, int estdepartureairportvertdistance, int estarrivalairporthorizdistance, int estarrivalairportvertdistance, int departureairportcandidatescount, int arrivalairportcandidatescount, Date stored) {
        this.id = id;
        this.icao24 = icao24;
        this.firstseen = firstseen;
        this.estdepartureairport = estdepartureairport;
        this.lastseen = lastseen;
        this.estarrivalairport = estarrivalairport;
        this.estdepartureairporthorizdistance = estdepartureairporthorizdistance;
        this.estdepartureairportvertdistance = estdepartureairportvertdistance;
        this.estarrivalairporthorizdistance = estarrivalairporthorizdistance;
        this.estarrivalairportvertdistance = estarrivalairportvertdistance;
        this.departureairportcandidatescount = departureairportcandidatescount;
        this.arrivalairportcandidatescount = arrivalairportcandidatescount;
        this.stored = stored;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIcao24() {
        return icao24;
    }

    public void setIcao24(String icao24) {
        this.icao24 = icao24;
    }

    public int getFirstseen() {
        return firstseen;
    }

    public void setFirstseen(int firstseen) {
        this.firstseen = firstseen;
    }

    public String getEstdepartureairport() {
        return estdepartureairport;
    }

    public void setEstdepartureairport(String estdepartureairport) {
        this.estdepartureairport = estdepartureairport;
    }

    public int getLastseen() {
        return lastseen;
    }

    public void setLastseen(int lastseen) {
        this.lastseen = lastseen;
    }

    public String getEstarrivalairport() {
        return estarrivalairport;
    }

    public void setEstarrivalairport(String estarrivalairport) {
        this.estarrivalairport = estarrivalairport;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public int getEstdepartureairporthorizdistance() {
        return estdepartureairporthorizdistance;
    }

    public void setEstdepartureairporthorizdistance(int estdepartureairporthorizdistance) {
        this.estdepartureairporthorizdistance = estdepartureairporthorizdistance;
    }

    public int getEstdepartureairportvertdistance() {
        return estdepartureairportvertdistance;
    }

    public void setEstdepartureairportvertdistance(int estdepartureairportvertdistance) {
        this.estdepartureairportvertdistance = estdepartureairportvertdistance;
    }

    public int getEstarrivalairporthorizdistance() {
        return estarrivalairporthorizdistance;
    }

    public void setEstarrivalairporthorizdistance(int estarrivalairporthorizdistance) {
        this.estarrivalairporthorizdistance = estarrivalairporthorizdistance;
    }

    public int getEstarrivalairportvertdistance() {
        return estarrivalairportvertdistance;
    }

    public void setEstarrivalairportvertdistance(int estarrivalairportvertdistance) {
        this.estarrivalairportvertdistance = estarrivalairportvertdistance;
    }

    public int getDepartureairportcandidatescount() {
        return departureairportcandidatescount;
    }

    public void setDepartureairportcandidatescount(int departureairportcandidatescount) {
        this.departureairportcandidatescount = departureairportcandidatescount;
    }

    public int getArrivalairportcandidatescount() {
        return arrivalairportcandidatescount;
    }

    public void setArrivalairportcandidatescount(int arrivalairportcandidatescount) {
        this.arrivalairportcandidatescount = arrivalairportcandidatescount;
    }

    public Date getStored() {
        return stored;
    }

    public void setStored(Date stored) {
        this.stored = stored;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Airplanes)) {
            return false;
        }
        Airplanes other = (Airplanes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.igradiski.ejb.eb.Airplanes[ id=" + id + " ]";
    }
    
}

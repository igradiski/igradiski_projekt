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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Korisnik
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Myairportslog.findAll", query = "SELECT m FROM Myairportslog m")})
public class Myairportslog implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MyairportslogPK myairportslogPK;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date stored;
    @Column(name = "BROJ_LETOVA")
    private Integer brojLetova;
    @JoinColumn(name = "IDENT", referencedColumnName = "IDENT", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Airports airports;

    public Myairportslog() {
    }

    public Myairportslog(MyairportslogPK myairportslogPK) {
        this.myairportslogPK = myairportslogPK;
    }

    public Myairportslog(MyairportslogPK myairportslogPK, Date stored) {
        this.myairportslogPK = myairportslogPK;
        this.stored = stored;
    }

    public Myairportslog(String ident, Date flightdate) {
        this.myairportslogPK = new MyairportslogPK(ident, flightdate);
    }

    public MyairportslogPK getMyairportslogPK() {
        return myairportslogPK;
    }

    public void setMyairportslogPK(MyairportslogPK myairportslogPK) {
        this.myairportslogPK = myairportslogPK;
    }

    public Date getStored() {
        return stored;
    }

    public void setStored(Date stored) {
        this.stored = stored;
    }

    public Integer getBrojLetova() {
        return brojLetova;
    }

    public void setBrojLetova(Integer brojLetova) {
        this.brojLetova = brojLetova;
    }

    public Airports getAirports() {
        return airports;
    }

    public void setAirports(Airports airports) {
        this.airports = airports;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (myairportslogPK != null ? myairportslogPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Myairportslog)) {
            return false;
        }
        Myairportslog other = (Myairportslog) object;
        if ((this.myairportslogPK == null && other.myairportslogPK != null) || (this.myairportslogPK != null && !this.myairportslogPK.equals(other.myairportslogPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.igradiski.ejb.eb.Myairportslog[ myairportslogPK=" + myairportslogPK + " ]";
    }
    
}

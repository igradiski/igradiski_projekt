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
    @NamedQuery(name = "Dnevnik.findAll", query = "SELECT d FROM Dnevnik d")})
public class Dnevnik implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Size(max = 100)
    @Column(name = "URL_ADRESA", length = 100)
    private String urlAdresa;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false, length = 50)
    private String username;
    @Basic(optional = false)
    @NotNull
    @Column(name = "VRIJEME_PRIJEMA", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vrijemePrijema;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TRAJANJE_OBRADE", nullable = false)
    private int trajanjeObrade;

    public Dnevnik() {
    }

    public Dnevnik(Integer id) {
        this.id = id;
    }

    public Dnevnik(Integer id, String username, Date vrijemePrijema, int trajanjeObrade) {
        this.id = id;
        this.username = username;
        this.vrijemePrijema = vrijemePrijema;
        this.trajanjeObrade = trajanjeObrade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrlAdresa() {
        return urlAdresa;
    }

    public void setUrlAdresa(String urlAdresa) {
        this.urlAdresa = urlAdresa;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getVrijemePrijema() {
        return vrijemePrijema;
    }

    public void setVrijemePrijema(Date vrijemePrijema) {
        this.vrijemePrijema = vrijemePrijema;
    }

    public int getTrajanjeObrade() {
        return trajanjeObrade;
    }

    public void setTrajanjeObrade(int trajanjeObrade) {
        this.trajanjeObrade = trajanjeObrade;
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
        if (!(object instanceof Dnevnik)) {
            return false;
        }
        Dnevnik other = (Dnevnik) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.igradiski.ejb.eb.Dnevnik[ id=" + id + " ]";
    }
    
}

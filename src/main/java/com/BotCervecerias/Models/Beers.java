package com.BotCervecerias.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="beers")
public class Beers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private   String name;
    @ManyToOne
    @JoinColumn(name = "beers_type")
    private BeersType beersType;
    private Double alcohol_grad;
    private Double btu;
    private String description;
    private String image;
    @ManyToOne
    @JoinColumn(name = "companies")
    private Companies companies;

    public Long getCompanies() {
        return companies != null ? companies.getId():null;
    }
    public String getCompaniesName() {
        return companies != null ? companies.getName():null;
    }

    public void setCompanies(Long user) {
        if (this.companies == null) {
            this.companies = new Companies();
        }
        this.companies.setId(user);
    }
    public Long getBeersTypeId() {
        return beersType != null ? beersType.getId():null;
    }

    public void setBeersTypeId(Long beersType) {
        if (this.beersType == null) {
            this.beersType = new BeersType();
        }
        this.beersType.setId(beersType);
    }
    public void setBeersTypeName(String tipo) {
        if (this.beersType==null){
            this.beersType=new BeersType();
        }this.beersType.setName(tipo);
    }
    public String getBeersTypeName() {
        return beersType != null ? beersType.getName():null;
    }


}

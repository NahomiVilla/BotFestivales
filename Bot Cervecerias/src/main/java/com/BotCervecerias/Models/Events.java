package com.BotCervecerias.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="events")
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "companies")
    private Companies companies;


    public Long getCompanies() {
        return companies != null ? companies.getId():null;
    }
    public void setCompanies(Long companies) {
        if (this.companies == null) {
            this.companies = new Companies();
        }
        this.companies.setId(companies);
    }

}

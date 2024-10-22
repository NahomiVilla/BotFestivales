package com.BotCervecerias.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="companies")
@JsonIgnoreProperties({"events"})
public class Companies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String logo;
    private String addres;
    @ManyToOne
    @JoinColumn(name = "users")
    private Users users;
    @OneToMany(mappedBy = "organizer")
    @JsonIgnore
    @ToString.Exclude
    private List<Events> events;


    public Long getUsers() {
        return users != null ? users.getId():null;
    }

    public void setUsers(Long user) {
        if (this.users == null) {
            this.users = new Users();
        }
        this.users.setId(user);
    }

    public  Companies setCompanyName(String companyName) {
        this.name = companyName;
        return this;
    }
}

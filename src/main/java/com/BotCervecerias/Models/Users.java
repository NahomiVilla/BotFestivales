package com.BotCervecerias.Models;

import jakarta.persistence.*;
import  jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Table(name="users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String name;
    @Email
    @NotBlank
    private  String email;
    private  String password;
    @ManyToOne
    @JoinColumn(name = "usertype")
    private UsersType usersType;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_date;

    @PrePersist
    protected  void onCreate(){
        this.created_date=new Date();
    }
    public void setUsersTypeId(Long usersType) {
        if (this.usersType == null) {
            this.usersType = new UsersType();
        }
        this.usersType.setId(usersType);
    }

    public Long getUsersTypeId() {
        return usersType != null ? usersType.getId():null;
    }
}

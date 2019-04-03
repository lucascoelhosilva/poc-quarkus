package org.acme.quickstart.models;

import javax.persistence.*;

@Entity
@Table(name = "users")
@NamedQuery(name = "Users.findAll",
        query = "SELECT u FROM User u ORDER BY u.name",
        hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@Cacheable
public class User {

    @Id
    @SequenceGenerator(
            name = "usersSequence",
            sequenceName = "users_id_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersSequence")
    private Integer id;

    @Column(name = "name")
    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
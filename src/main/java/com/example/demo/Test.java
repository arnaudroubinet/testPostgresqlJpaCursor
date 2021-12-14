package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Test {
    private Long id;
    private String value;

    public Test(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public Test() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}

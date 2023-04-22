package com.driver.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    private int perKmRate;

    private boolean available;

    @OneToOne
    @JoinColumn
    private Driver driver;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
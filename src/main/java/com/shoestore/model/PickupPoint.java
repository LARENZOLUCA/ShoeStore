package com.shoestore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pickup_points")
public class PickupPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String address;

    public PickupPoint() {}

    public PickupPoint(String address) {
        this.address = address;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
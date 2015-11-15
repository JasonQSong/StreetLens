package edu.osu.burden.streetlens;

import java.util.List;

/**
 * Created by WeiPan on 11/14/15.
 */
public class Discount {
    public String _id;

    public double loc[];

    public Address address;

    public String domain;
    public String name;
    public String phone;
    public String description;
    public String storeid;
    public String logo;

    public List<Offer> offer;


    public Discount(double[] loc){
        this.loc[0] = loc[0];
        this.loc[1] = loc[1];
    }



}

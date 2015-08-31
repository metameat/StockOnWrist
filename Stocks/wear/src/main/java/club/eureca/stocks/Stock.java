package club.eureca.stocks;

import java.io.Serializable;

/**
 * Created by Sven on 2015/8/5.
 */
public class Stock implements Serializable{
    private String name;
    private double price;
    private double rate;
    private String code;

    public Stock(String name, double price, double rate, String code) {
        this.name = name;
        this.price = price;
        this.rate = rate;
        this.code = code;
    }
    public Stock(String name, double price, double rate) {
        this.name = name;
        this.price = price;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        String s = name+"\t\t"+rate+"%"+"\n"+price;
        return s;
    }

}

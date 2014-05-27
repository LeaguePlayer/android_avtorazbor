package ru.amobilestudio.razborapp.helpers;

/**
 * Created by vetal on 27.05.14.
 */
public class Part {
    private int _id;
    private String _name;
    private String _date;
    private double price_sell;
    private double price_buy;
    private String comment;
    private int category_id;
    private int car_model_id;
    private int location_id;
    private int supplier_id;
    private int used_car_id;

/*    public Part(int _id, String _name, String _date, long price_sell, long price_buy, String comment,
                int category_id, int car_model_id, int location_id, int supplier_id, int used_car_id) {
        this._id = _id;
        this._name = _name;
        this._date = _date;
        this.price_sell = price_sell;
        this.price_buy = price_buy;
        this.comment = comment;
        this.category_id = category_id;
        this.car_model_id = car_model_id;
        this.location_id = location_id;
        this.supplier_id = supplier_id;
        this.used_car_id = used_car_id;
    }*/

    @Override
    public String toString() {
        return get_id() + " - " + get_name();
    }

    public double getPrice_sell() {
        return price_sell;
    }

    public int get_id() {
        return _id;
    }

    public String get_name() {
        return _name;
    }

    public String get_date() {
        return _date;
    }

    public double getPrice_buy() {
        return price_buy;
    }

    public String getComment() {
        return comment;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getCar_model_id() {
        return car_model_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public int getUsed_car_id() {
        return used_car_id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public void setPrice_sell(double price_sell) {
        this.price_sell = price_sell;
    }

    public void setPrice_buy(double price_buy) {
        this.price_buy = price_buy;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setCar_model_id(int car_model_id) {
        this.car_model_id = car_model_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public void setUsed_car_id(int used_car_id) {
        this.used_car_id = used_car_id;
    }
}

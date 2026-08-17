public class Order {
    int id;
    String item;
    double amount;
    String country;
    int userId;

    
    public Order(int id, String item, double amount, String country, int userId) {
        this.id = id;
        this.item = item;
        this.amount = amount;
        this.country = country;
        this.userId = userId;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getItem() {
        return item;
    }


    public void setItem(String item) {
        this.item = item;
    }


    public double getAmount() {
        return amount;
    }


    public void setAmount(double amount) {
        this.amount = amount;
    }


    public String getCountry() {
        return country;
    }


    public void setCountry(String country) {
        this.country = country;
    }


    public int getUserId() {
        return userId;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }

    
}

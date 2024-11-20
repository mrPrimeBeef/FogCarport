package app.entities;

public class EmailReceipt {
    private String carportWidth;
    private String carportLength;
    private String trapeztag;
    private String shedWidth;
    private String shedLength;
    private String notes;
    private String name;
    private String address;
    private String zip;
    private String city;
    private String mobil;
    private String email;

    public EmailReceipt(String carportWidth, String carportLength, String trapeztag, String shedWidth, String shedLength,
                        String notes, String name, String address, String zip, String city, String mobil, String email) {
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.trapeztag = trapeztag;
        this.shedWidth = shedWidth;
        this.shedLength = shedLength;
        this.notes = notes;
        this.name = name;
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.mobil = mobil;
        this.email = email;

        printEmail();
    }

    private void printEmail() {
        System.out.println("---- Email kvittering ----");
        System.out.println("Navn: " + name);
        System.out.println("Addresse: " + address);
        System.out.println("Postnummer: " + zip);
        System.out.println("By: " + city);
        System.out.println("Mobile: " + mobil);
        System.out.println("Email: " + email);
        System.out.println();

        System.out.println("Carport dimensioner:");
        System.out.println("bredde: " + carportWidth + " cm");
        System.out.println("længde: " + carportLength + " cm");
        System.out.println("trapeztag: " + trapeztag);
        System.out.println();

        System.out.println("Redskabskur dimensioner:");
        System.out.println("bredde: " + shedWidth + " cm");
        System.out.println("længde: " + shedLength + " cm");
        System.out.println();

        if (notes != null && !notes.isEmpty()) {
            System.out.println("Dine bemærkninger: " + notes);
        }
        System.out.println("-----------------------");
    }
}


package app.entities;

public class EmailReceipt {
    private int carportWidth;
    private int carportLength;
    private int carportHeight;
    private String name;
    private String address;
    private int zip;
    private String city;
    private String phone;
    private String email;

    public EmailReceipt(int carportWidth, int carportLength, int carportHeight, String name, String address, int zip, String mobil, String email) {
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.carportHeight = carportHeight;
        this.name = name;
        this.address = address;
        this.zip = zip;
        this.phone = phone;
        this.email = email;

        printEmail();
    }

    private void printEmail() {
        System.out.println("---- Email kvittering ----");
        System.out.println("Navn: " + name);
        System.out.println("Addresse: " + address);
        System.out.println("Postnummer: " + zip);
        System.out.println("By: " + city);
        System.out.println("Telfon: " + phone);
        System.out.println("Email: " + email);
        System.out.println();

        System.out.println("Carport dimensioner:");
        System.out.println("bredde: " + carportWidth + " cm");
        System.out.println("længde: " + carportLength + " cm");
        System.out.println("Højden: " + carportHeight + " cm");
        System.out.println();

//        System.out.println("Redskabsrum dimensioner:");
//        System.out.println("bredde: " + shedWidth + " cm");
//        System.out.println("længde: " + shedLength + " cm");
//        System.out.println();
//
//        if (notes != null && !notes.isEmpty()) {
//            System.out.println("Dine bemærkninger: " + notes);
//        }
        System.out.println("-----------------------");
    }
}


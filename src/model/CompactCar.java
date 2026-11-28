package model;

public class CompactCar extends Car{
    public CompactCar()
    {
        super();
    }

    public CompactCar(int numberOfPeople)
    {
        super(numberOfPeople);
    }

    public static void main(String[] args) {
        CompactCar morning = new CompactCar(2);
        morning.goForward(60);

        Vehicle v = morning;
        System.out.println("경차인가? "+ (v instanceof CompactCar));
        System.out.println("승용차인가? " + (v instanceof Car));
    }

}

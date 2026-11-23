package model;

public class Driver
{
    private String name;
    private Vehicle vehicle;

    public Driver(String driverName)
    {
        name = driverName;
    }

    public void purchase(Vehicle v)
    {
        vehicle = v;
    }

    public void driveAtSpeed(int speed)
    {
        vehicle.goForward(speed);
    }

    public String getName()
    {
        return name;
    }

    /* hasBoat(): returns if this driver has a boat */

    public boolean hasBoat()
    {
        return (vehicle instanceof Boat);
    }

    /* checkHarbor(): prints out current harbor info */

    public void checkHarbor()
    {
        /*
        Boat b = (Boat)vehicle;
        b.currentHarbor();
        */

        if (hasBoat() == true)
        {
            // prints out harbor info
            Boat b = (Boat)vehicle;     
            b.currentHarbor();
        }
        else 
        {
            // do nothing
            System.out.println("Has no boat");
        }
    }

    public Vehicle getVehicle()
    {
        return vehicle;
    }

    public static void main(String[] args)
    {
        Driver carDriver = new Driver("john");
        Driver truckDriver = new Driver("jane");
        Driver boatDriver = new Driver("dooly");

        Vehicle c = new Car(4);
        carDriver.purchase(c);
        truckDriver.purchase(new Truck(1000));

        Vehicle b = new Boat("Busan");
        boatDriver.purchase(b);

        System.out.println(carDriver.getName());
        carDriver.driveAtSpeed(100);
        System.out.println(carDriver.hasBoat());
        carDriver.checkHarbor();
        System.out.println();

        System.out.println(truckDriver.getName());
        truckDriver.driveAtSpeed(80);
        System.out.println(truckDriver.hasBoat());
        truckDriver.checkHarbor();
        System.out.println();

        System.out.println(boatDriver.getName());
        boatDriver.driveAtSpeed(30);
        System.out.println(boatDriver.hasBoat());
        boatDriver.checkHarbor();

        System.out.println();
    }
}

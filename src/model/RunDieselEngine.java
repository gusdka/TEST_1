package model;

public class RunDieselEngine
{
    public static void main(String[] args)
    {
        DieselEngineMountable bongo = new Truck(1000);

        bongo.putDieselEngine(3000);
    }
}

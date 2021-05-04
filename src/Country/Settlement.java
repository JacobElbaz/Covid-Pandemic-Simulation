package Country;

import Location.Location;
import Location.Point;
import Population.*;
import Simulation.Clock;
import Virus.IVirus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Settlement
{
    private final String name;
    private final Location location;
    private List<Person> people;
    private RamzorColor ramzorColor;
    private double mekadem;
    // new
    private double max_person;
    private int numberVaccineDose ;
    private Settlement[] settlementConnected;
    private List<Sick> sickPerson;
    private List<Person> healthyPerson;
    private int numOfDead;

    Settlement(String n, Location l, List<Person> p, int population) {
        name = n;
        location = l;
        people = p;
        mekadem = 1;
        ramzorColor = RamzorColor.Green;
        max_person = population * 1.3;
        settlementConnected = new Settlement[0];
        numberVaccineDose = 0;
        sickPerson = new ArrayList<>();
        healthyPerson = new ArrayList<>(p);
        numOfDead = 0;


    }
    public RamzorColor calculateRamzorGrade() {
        if ( mekadem < 0.4) {
            ramzorColor = RamzorColor.Green;
            return RamzorColor.Green;
        }
        else if(mekadem < 0.6) {
            ramzorColor = RamzorColor.Yellow;
            return RamzorColor.Yellow;
        }
        else if(mekadem < 0.8) {
            ramzorColor = RamzorColor.Orange;
            return RamzorColor.Orange;
        }
        else {
            ramzorColor = RamzorColor.Red;
            return RamzorColor.Red;
        }
    }

    public double contagiousPercent() {
        return numOfSicks()/(double) people.size();
    }
    public Point randomLocation() {
        int range_x = location.getPosition().getM_x() + location.getSize().getWidth();
        int range_y = location.getPosition().getM_y() + location.getSize().getHeight();
        int x = (int)(Math.random()*range_x)+location.getPosition().getM_x();
        int y = (int)(Math.random()*range_y)+location.getPosition().getM_y();
        return new Point(x, y);
    }
    public boolean addPerson(Person newPerson)
    {
        if (getPeople().size() < max_person )
        {
            people.add(newPerson);

            if (newPerson instanceof Sick)
                sickPerson.add((Sick) newPerson);

            if (newPerson instanceof Healthy)
                healthyPerson.add(newPerson);

            return true;
        }
        else{
            System.out.println("The person cannot be added/transferred in the settlement");
            return false;
        }
    }

    public void isDead(Sick miskine1)
    {
        this.people.remove(miskine1);
        this.sickPerson.remove(miskine1);
        numOfDead++;
    }

    public void isSick(Person miskine2, IVirus v)
    {
        healthyPerson.remove(miskine2);
        people.remove(miskine2);
        Sick newSick = (Sick) miskine2.contagion(v);
        sickPerson.add(newSick);
        people.add(newSick);
    }


    public boolean transferPerson(Person person, Settlement newPlace) {

        double stat = newPlace.getRamzorColor().percent * getRamzorColor().percent;
        float i = new Random().nextFloat();
        if (i < stat && newPlace.addPerson(person)) {
            people.remove(person);
            if (person instanceof Sick)
                sickPerson.remove(person);
            else
                healthyPerson.remove(person);

            return true;
        }
        else
            return false;
    }

    public void addNeighbours(Settlement newNeighbours)
    {
        ArrayList<Settlement> updateArray = new ArrayList<Settlement>(Arrays.asList(this.settlementConnected));
        updateArray.add(newNeighbours);
        this.settlementConnected = updateArray.toArray(this.settlementConnected);
    }
    @Override
    public String toString() {
        return "Settlement{" +
                "name='" + name + "', " +
                 location +
                ", ramzorColor=" + ramzorColor +
                ", mekadem=" + mekadem +
                '}';
    }
    public boolean equals(Settlement other) {
        return this.name.equals(other.name);
    }

    public String getName() {
        return name;
    }

    public double getMekadem() {
        return mekadem;
    }

    protected void setMekadem(double mekadem) {
        this.mekadem = mekadem;
    }

    public List<Person> getPeople() {
        return people;
    }

    public int numOfSicks() {
        return sickPerson.size();
    }

    public RamzorColor getRamzorColor() {
        return calculateRamzorGrade();
    }

    public double getSickPercent()
    {
        return this.numOfSicks() / (double)people.size();
    }

    public int getDead()
    {
        return this.numOfDead;
    }

    public int getGivenVaccineDose()
    {
        return numberVaccineDose;
    }

    public void setNumberVaccineDose(int numberVaccineDose) {
        this.numberVaccineDose += numberVaccineDose;
    }

    public void setRamzorColor() {
        this.ramzorColor = ramzorColor;
    }

    public Location getLocation(){
        return location;
    }
    public Settlement[] getNeighbours(){
        return settlementConnected;
    }
    public int getNumOfHealthy() { return healthyPerson.size(); }
    public List<Person> getHealthyPerson() { return this.healthyPerson ;}
    public void checkConvalescents() {
        for (int i = 0; i < sickPerson.size(); i++) {
            if ((Clock.now() - sickPerson.get(i).getContagiousTime())/Clock.getTicks_per_day() > 25){
                Convalescent convalescent = sickPerson.remove(i).recover();
                healthyPerson.add(convalescent);
                people = new ArrayList<Person>(healthyPerson);
                people.addAll(sickPerson);
            }
        }
    }

    public List<Sick> getSickPerson() {
        return sickPerson;
    }

    public void vaccinePopulation() {
        for (int j = 0; j < healthyPerson.size(); j++) {
            if (healthyPerson.get(j) instanceof Healthy && numberVaccineDose > 0) {
                Vaccinated person = ((Healthy) healthyPerson.remove(j)).vaccinate();
                healthyPerson.add(person);
                numberVaccineDose--;
            }
        }
    }
}


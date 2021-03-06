package Virus;

import Population.Person;
import Population.Sick;
import Simulation.Clock;

import java.util.Random;

public abstract class Variant implements IVirus{
    abstract double killingProbability(Person p);

    @Override
    public abstract double contagionProbability(Person p);

    @Override
    public boolean tryToContagion(Sick p, Person other) {
        if(!(other instanceof Sick)) {
            if (Clock.numOfDays(p.getContagiousTime()) >= 5) {
                double distance = Math.sqrt(Math.pow(p.getLocation().getM_x() - other.getLocation().getM_x(), 2) + Math.pow(p.getLocation().getM_y() - other.getLocation().getM_y(), 2));
                double probability = contagionProbability(other) * Math.min(1, 0.14 + Math.exp(2 - 0.25 * distance));
                int i = new Random().nextInt(100);
                return i < probability * 100;
            }
            else return false;
        }
        else return false;
    }

    @Override
    public boolean tryToKill(Sick p) {
        long t = Clock.now() - p.getContagiousTime();
        double probability = Math.max(0, killingProbability(p) - 0.01*killingProbability(p)*Math.pow(t-15, 2));
        int i = new Random().nextInt(100);
        return i < probability * 100;
    }
}

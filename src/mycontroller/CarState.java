package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public class CarState {
	Coordinate position;
	float angle;
	ArrayList<Float> previousSpeeds; // last 5 speeds including current speed
	float health;
	
	
	public CarState(String position, float angle, float health){
		this.position = new Coordinate(position);
		this.angle = angle;
		this.health = health;
		previousSpeeds = new ArrayList<>();
		previousSpeeds.add((float)-1);
	}
	
	public void updateCarState(String position, float angle, float speed, float health){
		this.position = new Coordinate(position);
		this.angle = angle;
		this.health = health;
		previousSpeeds.add(speed);
	}

}

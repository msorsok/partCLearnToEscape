package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public class CarState {
	Coordinate position;
	float angle;
	float speed;
	float health;
	float previousSpeed;
	
	
	public CarState(String position, float angle, float health, float speed){
		this.position = new Coordinate(position);
		this.angle = angle;
		this.health = health;
		this.speed = speed;
		this.previousSpeed = -1;
	}
	
	public void updateCarState(String position, float angle, float speed, float health){
		this.position = new Coordinate(position);
		this.angle = angle;
		this.health = health;
		this.previousSpeed = this.speed;
		this.speed = speed;
	}

}

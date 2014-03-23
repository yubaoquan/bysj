package test.testObjectReadAndWrite;

import java.io.Serializable;

public class Cow implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int weight;
	public String name;
	
	public Cow(String name, int weight) {
		this.weight = weight;
		this.name = name;
	}
	
	public void ah() {
		System.out.println("I am " + name + ", I weights " + weight + "Kg. moo!!");
	}
}

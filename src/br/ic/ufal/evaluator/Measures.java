package br.ic.ufal.evaluator;


public class Measures {
	
	private double designsize = 0;
	private double abstraction = 0;
	private double encapsulation = 0;
	private double coupling = 0;
	private double cohesion = 0;
	private double composition = 0;
	private double inheritance = 0;
	private double polymorphism = 0;
	private double messaging = 0;
	
	public Measures() {
	
	}

	public double getDesignsize() {
		return designsize;
	}

	public void setDesignsize(double designsize) {
		this.designsize = designsize;
	}

	public double getAbstraction() {
		return abstraction;
	}

	public void setAbstraction(double abstraction) {
		this.abstraction = abstraction;
	}

	public double getEncapsulation() {
		return encapsulation;
	}

	public void setEncapsulation(double encapsulation) {
		this.encapsulation = encapsulation;
	}

	public double getCoupling() {
		return coupling;
	}

	public void setCoupling(double coupling) {
		this.coupling = coupling;
	}

	public double getCohesion() {
		return cohesion;
	}

	public void setCohesion(double cohesion) {
		this.cohesion = cohesion;
	}

	public double getComposition() {
		return composition;
	}

	public void setComposition(double composition) {
		this.composition = composition;
	}

	public double getInheritance() {
		return inheritance;
	}

	public void setInheritance(double inheritance) {
		this.inheritance = inheritance;
	}

	public double getPolymorphism() {
		return polymorphism;
	}

	public void setPolymorphism(double polymorphism) {
		this.polymorphism = polymorphism;
	}

	public double getMessaging() {
		return messaging;
	}

	public void setMessaging(double messaging) {
		this.messaging = messaging;
	}

	@Override
	public String toString() {
		return "Design Size = " +designsize + "\n" + 
			   "Abstraction = " +abstraction + "\n" + 
			   "Encapsulation = " +encapsulation + "\n" + 
			   "Coupling = " +coupling + "\n" + 
			   "Cohesion = " +cohesion + "\n" + 
			   "Composition = " +composition + "\n" + 
			   "Inheritance = " +inheritance + "\n" + 
			   "Polymorphism = " +polymorphism + "\n" + 
			   "Messaging = " +messaging + "\n";
	}

}

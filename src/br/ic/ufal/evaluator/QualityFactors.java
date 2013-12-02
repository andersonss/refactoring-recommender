package br.ic.ufal.evaluator;

public class QualityFactors {

	private double reusability = 0;
	private double flexibility = 0;
	private double extensibility = 0;
	private double effectiveness = 0;
	
	private Measures measures = null;
	
	public QualityFactors(Measures measures) {
		this.measures = measures;
	}

	public double evaluateReusability(){
		return this.reusability = -0.25*measures.getCoupling()
						   +0.25*measures.getCohesion()
						   +0.5*measures.getMessaging()
						   +0.5*measures.getDesignsize();
	}
	
	public double evaluateFlexibility(){
		return this.flexibility = 0.25*measures.getEncapsulation()
						   -0.25*measures.getCoupling()
						   +0.5*measures.getComposition()
						   +0.5*measures.getPolymorphism();
	}
	
	public double evaluateExtendibility(){
		return this.flexibility = 0.5*measures.getAbstraction()
						   -0.5*measures.getCoupling()
						   +0.5*measures.getInheritance()
						   +0.5*measures.getPolymorphism();
	}
	
	public double evaluateEffectiveness(){
		return this.flexibility =  0.2*measures.getAbstraction()
						   +0.2*measures.getEncapsulation()
						   +0.2*measures.getComposition()
						   +0.2*measures.getInheritance()
						   +0.2*measures.getPolymorphism();
	}

	@Override
	public String toString() {
		return "QualityFactors [reusability=" + this.reusability + ", flexibility="
				+ this.flexibility + ", extensibility=" + this.extensibility
				+ ", this.effectiveness=" + effectiveness + "]";
	}

	
}

package br.ic.ufal.evaluator;

public class QualityFactors {

	private Measures measures = null;

	/**
	 * 
	 * @param measures
	 */
	public QualityFactors(Measures measures) {
		this.measures = measures;
	}

	/**
	 * @return
	 */
	public double evaluateReusability() {
		return -0.25 * measures.getCoupling() + 0.25 * measures.getCohesion()
				+ 0.5 * measures.getMessaging() + 0.5
				* measures.getDesignsize();
	}

	/**
	 * @return
	 */
	public double evaluateFlexibility() {
		return 0.25 * measures.getEncapsulation() - 0.25
				* measures.getCoupling() + 0.5 * measures.getComposition()
				+ 0.5 * measures.getPolymorphism();
	}

	/**
	 * @return
	 */
	public double evaluateExtendibility() {
		return 0.5 * measures.getAbstraction() - 0.5 * measures.getCoupling()
				+ 0.5 * measures.getInheritance() + 0.5
				* measures.getPolymorphism();
	}

	/**
	 * @return
	 */
	public double evaluateEffectiveness() {
		return 0.2 * measures.getAbstraction() + 0.2
				* measures.getEncapsulation() + 0.2 * measures.getComposition()
				+ 0.2 * measures.getInheritance() + 0.2
				* measures.getPolymorphism();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Reusability = " + evaluateReusability() + "\n Flexibility = "
				+ evaluateFlexibility() + "\n Extensibility = "
				+ evaluateExtendibility() + "\n Effectiveness = "
				+ evaluateEffectiveness() + "\n";
	}

}

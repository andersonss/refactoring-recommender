package br.ic.ufal.refactoring.detections;

public enum BadSmellType {
	DataClass, FragmentsDataClumps, ParametersDataClumps, DivergentChange, ClassDuplication,
	DownFragments, UpFragments, DownMethods, UpMethods, UnrelatedClassesDuplication, FeatureEnvy,
	AbstractSpeculativeGenerality, ParameterSpeculativeGenerality, UnsuedMethods, PublicFields
}

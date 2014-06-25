package org.somox.metrics.structure;

import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.java.Type;
import org.somox.kdmhelper.KDMHelper;
import org.somox.metrics.ClusteringRelation;
import org.somox.metrics.MetricID;
import org.somox.metrics.abstractmetrics.AbstractMetric;

//import de.fzi.gast.types.GASTClass;

/**
 * SliceLayerArchitectureQuality (SLAQ) metric
 * 
 * @author Grischa Liebel
 *
 */
public class SliceLayerArchitectureQuality extends AbstractMetric {
	public static final MetricID METRIC_ID = new MetricID("org.somox.metrics.SliceLayerArchitectureQuality");
	
	//implemented abstract methods from Metric
	
	/**
	 * {@inheritDoc}
	 */
	protected ClusteringRelation internalComputeDirected (
			ClusteringRelation relationToCompute) {

		//removelater
//		java.util.List<Type> type1 = relationToCompute.getComponentA().getImplementingClasses();
//		java.util.List<Type> type2 = relationToCompute.getComponentB().getImplementingClasses();
//		if(type1!= null & type2!=null & type1.size()>0 & type2.size()>0){
//			if(type1.get(0).getName().equals("Store") &
//					type2.get(0).getName().equals("ApplicationEventHandlerImpl")){
//				System.out.println("a");
//			}
//		}
		
		Set<Type> classes1 = this.getComponentToClassHelper().deriveImplementingClasses(relationToCompute.getComponentA());
		Set<Type> classes2 = this.getComponentToClassHelper().deriveImplementingClasses(relationToCompute.getComponentB());
		
		//compute overall prefix
		org.eclipse.gmt.modisco.java.Package prefixPackage = computePrefix(classes1, classes2);
		
		if (prefixPackage == null) {
			relationToCompute.setResultMetric(getMID(), 0.0);
			return relationToCompute;
		}
		EList<org.eclipse.gmt.modisco.java.Package> slices = prefixPackage.getOwnedPackages();
		EList<org.eclipse.gmt.modisco.java.Package> layers = null;

		//compute the maximum number of layers in a slice
		int max = 0;
		for (org.eclipse.gmt.modisco.java.Package current : slices) {
			if (current.getOwnedPackages().size()>=max) {
				layers = current.getOwnedPackages();
				max = layers.size();
			}
		}

		//check how many of the computed layers exist in every slice 
		if (max == 0) {
			relationToCompute.setResultMetric(getMID(), 1.0);
			return relationToCompute;
		} else {
			int expectedSubsystems = slices.size()*layers.size();
			int existingSubsystems = 0;
			
			for (org.eclipse.gmt.modisco.java.Package currentSlice : slices) {
				EList<org.eclipse.gmt.modisco.java.Package> currentLayers = currentSlice.getOwnedPackages();
				for (org.eclipse.gmt.modisco.java.Package currentReferencePackage : layers) {
					for (org.eclipse.gmt.modisco.java.Package currentLayer : currentLayers) {
						if (currentLayer.getName().equals(currentReferencePackage.getName())) {
							existingSubsystems++;
							break;
						}
					}
				}
			}
			
			if (expectedSubsystems == 0) {
				relationToCompute.setResultMetric(getMID(), 1.0);
				return relationToCompute;
			} else {
				relationToCompute.setResultMetric(getMID(), (double)existingSubsystems/(double)expectedSubsystems);
				return relationToCompute;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isCommutative () {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MetricID getMID () {
		return METRIC_ID;
	}
	
	/**
	 * Computes the longest prefix for the given elements
	 * 
	 * @param elements1 first list of elements
	 * @param elements2 second list of elements
	 * @return the last package in the package-hierarchy in which all elements are included
	 */
	private org.eclipse.gmt.modisco.java.Package computePrefix(Set<Type> elements1, Set<Type> elements2) {
		String prefix = "";
		boolean prefixFound = false;
		org.eclipse.gmt.modisco.java.Package currentPackage = null;
		
		for (Type current : elements1) {
			if (KDMHelper.getSurroundingPackage(current) != null) {
				currentPackage = KDMHelper.getSurroundingPackage(current);
				break;
			}
		}

		if (currentPackage == null) {
			for (Type current : elements2) {
				if (KDMHelper.getSurroundingPackage(current) != null) {
					currentPackage = KDMHelper.getSurroundingPackage(current);
					break;
				}
			}
		}
		
		if (currentPackage == null) {
			return null;
		}
		
		prefix = KDMHelper.computeFullQualifiedName(currentPackage);
		
		while(!prefixFound) {
			prefixFound = true;
			
			for (Type current : elements1) {
				if (! KDMHelper.isInnerClass(current)) {
					if (KDMHelper.getSurroundingPackage(current) != null) {
						if (!KDMHelper.computeFullQualifiedName(KDMHelper.getSurroundingPackage(current)).contains(prefix)) {
							prefixFound = false;
							break;
						}
					}
				}
			}
			
			if (!prefixFound) {
				currentPackage = currentPackage.getPackage();
				if (currentPackage == null) {
					return null;
				} else {
					prefix = KDMHelper.computeFullQualifiedName(currentPackage);
				}
			}
		}
		prefixFound = false;
		
		while(!prefixFound) {
			prefixFound = true;
			
			for (Type current : elements2) {
				if (! KDMHelper.isInnerClass(current)) {
					if (KDMHelper.getSurroundingPackage(current)!= null) {
						if (!KDMHelper.computeFullQualifiedName(KDMHelper.getSurroundingPackage(current)).contains(prefix)) {
							prefixFound = false;
							break;
						}
					}
				}
			}

			if (!prefixFound) {
				currentPackage = currentPackage.getPackage();
				if (currentPackage == null) {
					return null;
				} else {
					prefix = KDMHelper.computeFullQualifiedName(currentPackage);
				}
			}
		}
		return currentPackage;
	}

	@Override
	public boolean isNormalised() {
		return true;
	}
}

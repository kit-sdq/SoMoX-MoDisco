package org.somox.metrics.basic;

import java.util.Set;

import org.eclipse.gmt.modisco.java.Type;
import org.somox.metrics.ClusteringRelation;
import org.somox.metrics.MetricID;
import org.somox.metrics.abstractmetrics.AbstractCountingMetric;

//import de.fzi.gast.types.GASTClass;

/**
 * Efferent coupling (Ce): The number of types inside this assembly that depends on types
 * outside this assembly. High efferent coupling indicates that the concerned assembly
 * is dependant.
 * see http://www.ndepend.com/Metrics.aspx#EfferentCoupling
 * 
 * @author Steffen Becker
 */
public class EfferentCoupling extends AbstractCountingMetric {

    public static final MetricID METRIC_ID = new MetricID("org.somox.metric.basic.EfferentCoupling");

    @Override
    protected void internalComputeDirected(
            final ClusteringRelation relationToCompute) {


        //		//removelater
        //		java.util.List<Type> type1 = relationToCompute.getComponentA().getImplementingClasses();
        //		java.util.List<Type> type2 = relationToCompute.getComponentB().getImplementingClasses();
        //		if(type1!= null & type2!=null & type1.size()>0 & type2.size()>0){
        //			if(type1.get(0).getName().equals("CashBox") &
        //					type2.get(0).getName().equals("RefreshButton")){
        //				String fileName = "interfacecount.txt";;
        ////				org.somox.changetest.Helper.writeToFile(fileName, "---" +type1.get(0).getName() + " " + type2.get(0).getName());
        //			}
        //		}

        final Set<Type> internalClasses = calculateUnion(relationToCompute.getSourceComponent(), relationToCompute.getTargetComponent());

        relationToCompute.setResultMetric(METRIC_ID, getAccessGraphCache().calculateNumberOfExternalAccesses(internalClasses));
    }

    @Override
    public MetricID getMID() {
        return METRIC_ID;
    }

    @Override
    public boolean isCommutative() {
        return true;
    }

}

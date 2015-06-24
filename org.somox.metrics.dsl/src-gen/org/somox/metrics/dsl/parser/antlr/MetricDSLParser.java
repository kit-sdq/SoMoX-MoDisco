/*
* generated by Xtext
*/
package org.somox.metrics.dsl.parser.antlr;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.somox.metrics.dsl.services.MetricDSLGrammarAccess;

import com.google.inject.Inject;

public class MetricDSLParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private MetricDSLGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	
	@Override
	protected org.somox.metrics.dsl.parser.antlr.internal.InternalMetricDSLParser createParser(XtextTokenStream stream) {
		return new org.somox.metrics.dsl.parser.antlr.internal.InternalMetricDSLParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "MetricModel";
	}
	
	public MetricDSLGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(MetricDSLGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}

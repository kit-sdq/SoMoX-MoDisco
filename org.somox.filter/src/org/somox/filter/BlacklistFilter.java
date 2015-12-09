package org.somox.filter;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.gmt.modisco.java.ASTNode;
import org.eclipse.gmt.modisco.java.Type;
import org.somox.kdmhelper.KDMHelper;

//import de.fzi.gast.types.GASTClass;

public class BlacklistFilter extends BaseFilter<Type> {

    private static Logger logger = Logger.getLogger(BlacklistFilter.class);

    private Pattern matchPattern = null;

    public BlacklistFilter(final Set<String> blacklist){
        super();

        this.matchPattern = deriveMatchPattern(blacklist);
    }

    @Override
    public boolean passes(final Type object) {
        return !classMatchesBlacklist(object);
    }

    /**
     * Compile a sinlge {@link Pattern} containing all elements of the blacklist
     * @param blacklist The list of blacklist patterns
     * @return A corresponding {@link Pattern} used to match component FQNs
     */
    private static Pattern deriveMatchPattern(final Set<String> blacklist) {
        final StringBuffer sw = new StringBuffer();
        for (final String s : blacklist) {
            sw.append(s);
            sw.append("|");
        }
        if (sw.length() > 0) {
            sw.deleteCharAt(sw.length()-1);
        }
        final Pattern matchPattern = Pattern.compile(sw.toString(),Pattern.CASE_INSENSITIVE);

        logger.debug("Initialised Blacklist filter with pattern "+matchPattern.toString());

        return matchPattern;
    }

    /** Uses Regular Expressions to match FQNs of components
     * @param matchPattern The pattern to match against
     * @param currentClass The class of the component
     * @return true if the FQN of the class matches the given pattern
     */
    private boolean classMatchesBlacklist(final Type currentClass) {

        boolean result = false;

        if(currentClass instanceof ASTNode){
            result = matchPattern.matcher(KDMHelper.computeFullQualifiedName(currentClass)).matches();
            if (logger.isTraceEnabled() && result) {
                logger.trace("Blacklist filter matches " + KDMHelper.computeFullQualifiedName(currentClass));
            }
        }
        return result;
    }

}

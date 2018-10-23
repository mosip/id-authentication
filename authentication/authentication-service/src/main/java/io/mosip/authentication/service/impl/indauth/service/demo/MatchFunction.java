package io.mosip.authentication.service.impl.indauth.service.demo;

/**
 * @author Arun Bose
 * The Interface MatchFunction.
 */
@FunctionalInterface
public interface MatchFunction {
	
   /**
    * Do match.
    *
    * @param object1 the object 1
    * @param object2 the object 2
    * @return the int
    */
   int doMatch(Object object1,Object object2);	
}

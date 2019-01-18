package io.mosip.kernel.core.datamapper.spi;

/**
 * The main runtime interface between a Java application and a Data Mapper. This
 * is the central interface abstracting the service of a Java bean mapping.
 * 
 * The operation of mapping may include : <br>
 * <ul>
 * <li>Creation of new objects : <code>newObject()</code></li>
 * <li>Conversion object to another type: <code>convert()</code></li>
 * </ul>
 * <br>
 * 
 * Example of code to map an instance of <code>Entity</code>(<code>entity</code>
 * ) to <code>DTO</code> class:<br>
 * 
 * <pre>
 * ...
 * DTO newDTO = mapperFacade.map(entity, DTO.class);
 * ...
 * </pre>
 * 
 * @author Neha
 * @since 1.0.0
 * 
 */
public interface DataMapper<S,D> {


	public D map(S source);

    public void map(S source, D destination);

	public void map(S source, D destination, DataConverter<S, D> dataConverter);
}

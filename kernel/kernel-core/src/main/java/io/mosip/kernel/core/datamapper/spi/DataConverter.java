package io.mosip.kernel.core.datamapper.spi;

/**
 * This performs the conversion of source into a new instance of destination type.
 * 
 * The operation of conversion may include : <br>
 * <ul>
 * <li>Creation of new objects : <code>newObject()</code></li>
 * <li>Conversion object to another type: <code>convert()</code></li>
 * </ul>
 * <br>
 * 
 * @author Neha
 * @since 1.0.0
 * 
 * @param <S>
 *            the type of the source object
 * @param <D>
 *            the type of the destination object
 */
public interface DataConverter<S, D> {

	public void convert(S source, D destination);
}

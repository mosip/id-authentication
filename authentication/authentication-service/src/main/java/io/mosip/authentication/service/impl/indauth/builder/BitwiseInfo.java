package io.mosip.authentication.service.impl.indauth.builder;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The BitwiseInfo class to set usage bits for extracting it to a string.
 * 
 * @author Loganathan Sekar
 * 
 */
public class BitwiseInfo {
	
	private static final Integer FOUR = 4;
	/** The bits. */
	private final Boolean[] bits;
	
	/**
	 * Instantiates a new bitwise info.
	 *
	 * @param hexCount the hex count
	 */
	public BitwiseInfo(int hexCount) {
		int size = hexCount * FOUR;
		//Initialize to false
		bits = IntStream.range(0, size).mapToObj(i -> false).toArray(s -> new Boolean[s]);
	}
	
	/**
	 * Sets the bit.
	 *
	 * @param hexNum the hex num
	 * @param bitIndex the bit index
	 */
	public void setBit(int hexNum, int bitIndex) {
		setBit(getIndex(hexNum, bitIndex));
	}

	/**
	 * Gets the index.
	 *
	 * @param hexNum the hex num
	 * @param bitIndex the bit index
	 * @return the index
	 */
	private int getIndex(int hexNum, int bitIndex) {
		return bits.length - ((hexNum ) * FOUR) + bitIndex;
	}
	
	/**
	 * Clear bit.
	 *
	 * @param hexNum the hex num
	 * @param bitIndex the bit index
	 */
	public void clearBit(int hexNum, int bitIndex) {
		clearBit(getIndex(hexNum, bitIndex));
	}
	
	/**
	 * Sets the bit.
	 *
	 * @param index the new bit
	 */
	public void setBit(int index) {
		bits[index] = true;
	}
	
	/**
	 * Clear bit.
	 *
	 * @param index the index
	 */
	public void clearBit(int index) {
		bits[index] = false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getHex();
	}

	/**
	 * Gets the hex as String.
	 *
	 * @return the hex string
	 */
	private String getHex() {
		HexSpliterator hexSpliterator = new HexSpliterator(Stream.of(bits).spliterator());
		Stream<Integer> hexStream = reverse(StreamSupport.stream(hexSpliterator, false));
		return "0x" + hexStream.map(Integer::toHexString)
				.collect(Collectors.joining());
	}
	
	/**
	 * Reverse the stream.
	 *
	 * @param <T> the generic type
	 * @param input the input
	 * @return the stream
	 */
	@SuppressWarnings("unchecked")
	static <T> Stream<T> reverse(Stream<T> input) {
	    Object[] temp = input.toArray();
	    return (Stream<T>) IntStream.iterate(temp.length - 1, i -> i - 1)
	    							.limit(temp.length)
	                                .mapToObj(i -> temp[i]);
	}
	
}

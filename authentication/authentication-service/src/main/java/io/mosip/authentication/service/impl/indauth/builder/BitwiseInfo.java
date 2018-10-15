package io.mosip.authentication.service.impl.indauth.builder;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The Class BitwiseInfo.
 */
public class BitwiseInfo {
	
	/** The bits. */
	private final Boolean[] bits;
	
	/**
	 * Instantiates a new bitwise info.
	 *
	 * @param hexCount the hex count
	 */
	public BitwiseInfo(int hexCount) {
		int size = hexCount * 4;
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
		return bits.length - ((hexNum ) * 4) + bitIndex;
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
	 * Gets the hex.
	 *
	 * @return the hex
	 */
	private String getHex() {
		HexSpliterator hexSpliterator = new HexSpliterator(Stream.of(bits).spliterator());
		Stream<Integer> hexStream = reverse(StreamSupport.stream(hexSpliterator, false));
		return "0x" + hexStream.map(Integer::toHexString)
				.collect(Collectors.joining());
	}
	
	/**
	 * Reverse.
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
	
	/**
	 * The Class HexSpliterator.
	 */
	static class HexSpliterator implements Spliterator<Integer> {
		
		/** The boolean spliterator. */
		private Spliterator<Boolean> booleanSpliterator;

		/**
		 * Instantiates a new hex spliterator.
		 *
		 * @param booleanSpliterator the boolean spliterator
		 */
		private HexSpliterator(Spliterator<Boolean> booleanSpliterator) {
			this.booleanSpliterator = booleanSpliterator;
		}

		/* (non-Javadoc)
		 * @see java.util.Spliterator#tryAdvance(java.util.function.Consumer)
		 */
		@Override
		public boolean tryAdvance(Consumer<? super Integer> action) {
			StringBuilder buffer = new StringBuilder();
			if(IntStream.range(0, 4).allMatch(i -> tryAdvanceBoolean(buffer))) {
				String binaryStr = buffer.toString();
				int intVal = Integer.parseInt(binaryStr, 2);
				action.accept(intVal);
				return true;
			}
			return false;
		}

		/**
		 * Try advance boolean.
		 *
		 * @param buffer the buffer
		 * @return true, if successful
		 */
		private boolean tryAdvanceBoolean(StringBuilder buffer) {
			return booleanSpliterator.tryAdvance(b -> buffer.insert(0, b ? 1 : 0));
		}

		/* (non-Javadoc)
		 * @see java.util.Spliterator#trySplit()
		 */
		@Override
		public Spliterator<Integer> trySplit() {
			return null;
		}

		/* (non-Javadoc)
		 * @see java.util.Spliterator#estimateSize()
		 */
		@Override
		public long estimateSize() {
			return booleanSpliterator.estimateSize() / 4;
		}

		/* (non-Javadoc)
		 * @see java.util.Spliterator#characteristics()
		 */
		@Override
		public int characteristics() {
			return booleanSpliterator.characteristics();
		}
		
	}

}

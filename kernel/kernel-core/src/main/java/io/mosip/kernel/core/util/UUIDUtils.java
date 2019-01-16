package io.mosip.kernel.core.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is used to generate UUID of Type 5.
 * 
 * @author Bal Vikash Sharma
 *
 */
public class UUIDUtils {

	private static final Charset UTF8 = Charset.forName("UTF-8");
	public static final UUID NAMESPACE_DNS = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
	public static final UUID NAMESPACE_URL = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
	public static final UUID NAMESPACE_OID = UUID.fromString("6ba7b812-9dad-11d1-80b4-00c04fd430c8");
	public static final UUID NAMESPACE_X500 = UUID.fromString("6ba7b814-9dad-11d1-80b4-00c04fd430c8");

	private UUIDUtils() {
		super();
	}

	/**
	 * This method takes UUID <code>namespace</code> and a <code>name</code> and
	 * generate Type 5 UUID.
	 * 
	 * @param namespace
	 *            is the {@link UUID}
	 * @param name
	 *            for which UUID needs to be generated.
	 * @return type 5 UUID as per given <code>namespace</code> and <code>name</code>
	 * @throws NullPointerException
	 *             when either <code>namespace</code> or <code>name</code> is null.
	 */
	public static UUID getUUID(UUID namespace, String name) {
		return getUUIDFromBytes(namespace, Objects.requireNonNull(name, "name == null").getBytes(UTF8));
	}

	/**
	 * 
	 * This method takes UUID <code>namespace</code> and a <code>name</code> as a
	 * byte array and generate Type 5 UUID.
	 * 
	 * @param namespace
	 *            is the {@link UUID}
	 * @param name
	 *            is a byte array
	 * @return type 5 UUID as per given <code>namespace</code> and <code>name</code>
	 * 
	 * @throws NullPointerException
	 *             when either <code>namespace</code> or <code>name</code> is null.
	 */
	public static UUID getUUIDFromBytes(UUID namespace, byte[] name) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException nsae) {
			throw new InternalError("SHA-1 not supported");
		}
		md.update(toBytes(Objects.requireNonNull(namespace, "namespace is null")));
		md.update(Objects.requireNonNull(name, "name is null"));
		byte[] sha1Bytes = md.digest();
		sha1Bytes[6] &= 0x0f; /* clear version */
		sha1Bytes[6] |= 0x50; /* set to version 5 */
		sha1Bytes[8] &= 0x3f; /* clear variant */
		sha1Bytes[8] |= 0x80; /* set to IETF variant */
		return fromBytes(sha1Bytes);
	}

	private static UUID fromBytes(byte[] data) {
		// Based on the private UUID(bytes[]) constructor
		long msb = 0;
		long lsb = 0;
		assert data.length >= 16;
		for (int i = 0; i < 8; i++)
			msb = (msb << 8) | (data[i] & 0xff);
		for (int i = 8; i < 16; i++)
			lsb = (lsb << 8) | (data[i] & 0xff);
		return new UUID(msb, lsb);
	}

	private static byte[] toBytes(UUID uuid) {
		// inverted logic of fromBytes()
		byte[] out = new byte[16];
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		for (int i = 0; i < 8; i++)
			out[i] = (byte) ((msb >> ((7 - i) * 8)) & 0xff);
		for (int i = 8; i < 16; i++)
			out[i] = (byte) ((lsb >> ((15 - i) * 8)) & 0xff);
		return out;
	}

}

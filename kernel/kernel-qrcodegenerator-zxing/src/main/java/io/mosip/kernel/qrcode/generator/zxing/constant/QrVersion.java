package io.mosip.kernel.qrcode.generator.zxing.constant;

/**
 * QrCode Version and Module mapping contains {@link #V25} {@link #V26}
 * {@link #V27} {@link #V28} {@link #V29} {@link #V30} {@link #V31} {@link #V32}
 * {@link #V33} {@link #V34} {@link #V35} {@link #V36} {@link #V37} {@link #V38}
 * {@link #V39} {@link #V40}
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public enum QrVersion {

	/**
	 * Version 1
	 */
	V1(1, 58),/**
	 * Version 2
	 */
	V2(2, 66),/**
	 * Version 3
	 */
	V3(3, 74),/**
	 * Version 4
	 */
	V4(4, 82),/**
	 * Version 5
	 */
	V5(5, 90),/**
	 * Version 6
	 */
	V6(6, 98),/**
	 * Version 7
	 */
	V7(7, 106),/**
	 * Version 8
	 */
	V8(8, 114),/**
	 * Version 9
	 */
	V9(9, 122),/**
	 * Version 10
	 */
	V10(10, 130),/**
	 * Version 11
	 */
	V11(11, 138),/**
	 * Version 12
	 */
	V12(12, 146),/**
	 * Version 13
	 */
	V13(13, 154),/**
	 * Version 14
	 */
	V14(14, 162),/**
	 * Version 15
	 */
	V15(15, 170),/**
	 * Version 16
	 */
	V16(16, 178),/**
	 * Version 17
	 */
	V17(17, 186),/**
	 * Version 18
	 */
	V18(18, 194),/**
	 * Version 19
	 */
	V19(19, 202),/**
	 * Version 20
	 */
	V20(20, 210),/**
	 * Version 21
	 */
	V21(21, 218),/**
	 * Version 22
	 */
	V22(22, 226),/**
	 * Version 23
	 */
	V23(23, 234),/**
	 * Version 24
	 */
	V24(24, 242),
	/**
	 * Version 25
	 */
	V25(25, 250),
	/**
	 * Version 26
	 */
	V26(26, 258),
	/**
	 * Version 27
	 */
	V27(27, 266),
	/**
	 * Version 28
	 */
	V28(28, 274),
	/**
	 * Version 29
	 */
	V29(29, 282),
	/**
	 * Version 30
	 */
	V30(30, 290),
	/**
	 * Version 31
	 */
	V31(31, 298),
	/**
	 * Version 32
	 */
	V32(32, 306),
	/**
	 * Version 33
	 */
	V33(33, 314),
	/**
	 * Version 34
	 */
	V34(34, 322),
	/**
	 * Version 35
	 */
	V35(35, 330),
	/**
	 * Version 36
	 */
	V36(36, 338),
	/**
	 * Version 37
	 */
	V37(37, 346),
	/**
	 * Version 38
	 */
	V38(38, 354),
	/**
	 * Version 39
	 */
	V39(39, 362),
	/**
	 * Version 40
	 */
	V40(40, 370);

	/**
	 * No of Version
	 */
	private final int version;

	/**
	 * No of Modules
	 */
	private final int size;

	/**
	 * Constructor for {@link QrVersion}
	 * 
	 * @param version version of QR code
	 * @param size    no of Modules
	 */
	private QrVersion(final int version, final int size) {
		this.version = version;
		this.size = size;
	}

	/**
	 * Getter for No of Modules
	 * 
	 * @return {@link #size}
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Getter for version
	 * 
	 * @return {@link #version}
	 */
	public int getVersion() {
		return version;
	}

}

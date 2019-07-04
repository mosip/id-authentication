package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

/**
 * <p>
 * An Item's type is composed of bits from the WiaItemFlags enumeration. You can test an Item's type by using the AND operation with Item.Properties("Item Flags") and a member from the WiaItemFlags enumeration.
 * </p>
 */
public enum WiaItemFlag implements ComEnum {
  /**
   * <p>
   * The item is uninitialized or has been deleted.
   * </p>
   * <p>
   * The value of this constant is 0
   * </p>
   */
  FreeItemFlag(0),
  /**
   * <p>
   * The item is an image file. Only valid for items that also have the FileItemFlag flag set.
   * </p>
   * <p>
   * The value of this constant is 1
   * </p>
   */
  ImageItemFlag(1),
  /**
   * <p>
   * The item is a file.
   * </p>
   * <p>
   * The value of this constant is 2
   * </p>
   */
  FileItemFlag(2),
  /**
   * <p>
   * The item is a folder.
   * </p>
   * <p>
   * The value of this constant is 4
   * </p>
   */
  FolderItemFlag(4),
  /**
   * <p>
   * Identifies the root item in the device.
   * </p>
   * <p>
   * The value of this constant is 8
   * </p>
   */
  RootItemFlag(8),
  /**
   * <p>
   * This item supports the Analyze method.
   * </p>
   * <p>
   * The value of this constant is 16
   * </p>
   */
  AnalyzeItemFlag(16),
  /**
   * <p>
   * The item is an audio file. Only valid for items that also have the FileItemFlag flag set.
   * </p>
   * <p>
   * The value of this constant is 32
   * </p>
   */
  AudioItemFlag(32),
  /**
   * <p>
   * The item represents a connected device.
   * </p>
   * <p>
   * The value of this constant is 64
   * </p>
   */
  DeviceItemFlag(64),
  /**
   * <p>
   * The item is marked as deleted.
   * </p>
   * <p>
   * The value of this constant is 128
   * </p>
   */
  DeletedItemFlag(128),
  /**
   * <p>
   * The item represents a disconnected device.
   * </p>
   * <p>
   * The value of this constant is 256
   * </p>
   */
  DisconnectedItemFlag(256),
  /**
   * <p>
   * The item represents a horizontal panoramic image.
   * </p>
   * <p>
   * The value of this constant is 512
   * </p>
   */
  HPanoramaItemFlag(512),
  /**
   * <p>
   * The item represents a vertical panoramic image.
   * </p>
   * <p>
   * The value of this constant is 1024
   * </p>
   */
  VPanoramaItemFlag(1024),
  /**
   * <p>
   * Images in this folder were taken in a continuous time sequence. Only valid for items that also have the FolderItemFlag flag set.
   * </p>
   * <p>
   * The value of this constant is 2048
   * </p>
   */
  BurstItemFlag(2048),
  /**
   * <p>
   * The item represents a storage medium.
   * </p>
   * <p>
   * The value of this constant is 4096
   * </p>
   */
  StorageItemFlag(4096),
  /**
   * <p>
   * The item can be transferred.
   * </p>
   * <p>
   * The value of this constant is 8192
   * </p>
   */
  TransferItemFlag(8192),
  /**
   * <p>
   * This item was created, and does not correspond to an item in a device.
   * </p>
   * <p>
   * The value of this constant is 16384
   * </p>
   */
  GeneratedItemFlag(16384),
  /**
   * <p>
   * The item has file attachments.
   * </p>
   * <p>
   * The value of this constant is 32768
   * </p>
   */
  HasAttachmentsItemFlag(32768),
  /**
   * <p>
   * The item represents streaming video.
   * </p>
   * <p>
   * The value of this constant is 65536
   * </p>
   */
  VideoItemFlag(65536),
  /**
   * <p>
   * The item has been removed from the device.
   * </p>
   * <p>
   * The value of this constant is -2147483648
   * </p>
   */
  RemovedItemFlag(-2147483648),
  ;

  private final int value;
  WiaItemFlag(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}

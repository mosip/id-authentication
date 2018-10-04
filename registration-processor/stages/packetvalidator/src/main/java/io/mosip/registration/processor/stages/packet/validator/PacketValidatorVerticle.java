/**
 * 
 */
package io.mosip.registration.processor.stages.packet.validator;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.registration.processor.core.bridge.util.JsonUtil;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

/**
 * @author M1022006
 *
 */
public class PacketValidatorVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(PacketValidatorVerticle.class);

	private FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter = new FilesystemCephAdapterImpl();

	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	public static void main(String args[]) {
		ClusterManager mgr = new IgniteClusterManager();
		VertxOptions options = new VertxOptions().setClusterManager(mgr).setHAEnabled(true).setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(PacketValidatorVerticle.class.getName(),
						new DeploymentOptions().setHa(true));
			} else
				log.error("Failed: " + vertx.cause());
		});
	}

	@Override
	public void start() {
		vertx.eventBus().consumer("structure-bus-in", message -> {
			process((JsonObject) message.body());
		});
	}

	private void process(JsonObject jsonObject) {
		String registrationId = jsonObject.getString("registrationId");

		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PacketMetaInfo);
		try {
			fileManager.put(PacketFiles.PacketMetaInfo.toString(), packetMetaInfoStream, DirectoryPathDto.TEMP);
			PacketInfo packetinfo = (PacketInfo) JsonUtil.jsonFileToJavaObject(PacketInfo.class,
					DirectoryPathDto.TEMP.toString());
			fileManager.cleanUpFile(DirectoryPathDto.TEMP, DirectoryPathDto.TEMP, registrationId);
		} catch (MosipJsonParseException e) {

		} catch (MosipJsonMappingException e) {

		} catch (MosipIOException e) {

		} catch (IOException e) {

		}

	}

}

package pt.util;

import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import pt.dto.encrypted.AuthEntity;
import pt.dto.encrypted.PersonalIdAuthEntity;
import pt.dto.unencrypted.AddressAuthEntity;

public class YMLUtil {

	public YMLUtil() {

	}

	public static AddressAuthEntity loadUnencryptedData() throws Exception {
		Yaml yaml = new Yaml();
		try (InputStream in = YMLUtil.class.getResourceAsStream("/encryption_util/unencrypted_address_data.yml")) {
			AddressAuthEntity entity = yaml.loadAs(in, AddressAuthEntity.class);
			// System.out.println(entity);
			return entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

	public static AuthEntity loadRequestData() throws Exception {
		Yaml yaml = new Yaml();
		try (InputStream in = YMLUtil.class.getResourceAsStream("/auth/address_auth.yml")) {
			AuthEntity entity = yaml.loadAs(in, AuthEntity.class);
			// System.out.println(entity);
			return entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

	public static PersonalIdAuthEntity loadPersonalIdRequestData() throws Exception {
		Yaml yaml = new Yaml();
		try (InputStream in = YMLUtil.class.getResourceAsStream("/auth/personalId_auth.yml")) {
			PersonalIdAuthEntity entity = yaml.loadAs(in, PersonalIdAuthEntity.class);
			// System.out.println(entity);
			return entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

}

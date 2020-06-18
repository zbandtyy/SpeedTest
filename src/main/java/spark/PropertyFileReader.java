package spark;

import org.apache.log4j.Logger;
import spark.config.AppConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read property file
 * 
 * @author abaghel
 *
 */
public class PropertyFileReader {
	private static final Logger logger = Logger.getLogger(PropertyFileReader.class);
	private static Properties prop = new Properties();
	public static Properties readPropertyFile() throws Exception {
		if (prop.isEmpty()) {
			InputStream input = new FileInputStream(AppConfig.KAFKA_CONFIG_FILE);
			try {
				prop.load(input);
			} catch (IOException ex) {
				logger.error(ex);
				throw ex;
			} finally {
				if (input != null) {
					input.close();
				}
			}
		}
		return prop;
	}
}

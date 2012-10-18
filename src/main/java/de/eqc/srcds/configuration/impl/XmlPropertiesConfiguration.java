/**
 * This file is part of the Source Dedicated Server Controller project.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or
 * combining it with srcds-controller (or a modified version of that library),
 * containing parts covered by the terms of GNU General Public License,
 * the licensors of this Program grant you additional permission to convey
 * the resulting work. {Corresponding Source for a non-source form of such a
 * combination shall include the source code for the parts of srcds-controller
 * used as well as that of the covered work.}
 *
 * For more information, please consult:
 *    <http://www.earthquake-clan.de/srcds/>
 *    <http://code.google.com/p/srcds-controller/>
 */
package de.eqc.srcds.configuration.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import de.eqc.srcds.configuration.Configuration;
import de.eqc.srcds.configuration.ConfigurationKey;
import de.eqc.srcds.configuration.ConfigurationRegistry;
import de.eqc.srcds.configuration.datatypes.Password;
import de.eqc.srcds.configuration.exceptions.ConfigurationException;
import de.eqc.srcds.core.CryptoUtil;
import de.eqc.srcds.core.SourceDServerController;
import de.eqc.srcds.exceptions.CryptoException;
import de.eqc.srcds.xmlbeans.impl.ControllerConfiguration;

public final class XmlPropertiesConfiguration implements Configuration {

    private static Logger log = Logger.getLogger(SourceDServerController.class.getSimpleName());
    private final File propertiesFile;
    private Properties properties;

    public XmlPropertiesConfiguration(final File propertiesFile) throws ConfigurationException {

	this.propertiesFile = propertiesFile;

	if (propertiesFile.exists()) {
	    loadConfiguration();
	} else {
	    createDefaultConfiguration();
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(final String key, final Class<T> dataType) throws ConfigurationException {

	if (!ConfigurationRegistry.matchesDataType(key, dataType)) {
	    throw new ConfigurationException(String.format("Invalid data type for configuration key %s",
							   key));
	}

	T value = null;

	if (String.class.isAssignableFrom(dataType)) {
	    value = (T) properties.getProperty(key);
	} else {
	    try {
		final Method conversionMethod = dataType.getDeclaredMethod("valueOf", String.class);
		value = (T) conversionMethod.invoke(dataType, properties.getProperty(key));
	    } catch (Exception e) {
		throw new ConfigurationException(String.format("Conversion to datatype %s failed for %s",
							       dataType.getName(),
							       key),
						 e);
	    }
	}
	return value;
    }

    @Override
    public <T> void setValue(final String key, final T value) throws ConfigurationException {

	// if (!ConfigurationRegistry.matchesDataType(key, value.getClass())) {
	// throw new ConfigurationException(String.format(
	// "Invalid data type for configuration key %s", key));
	// }

	if (isValidKey(key)) {
	    properties.setProperty(key, value.toString());
	    store();
	    log.finest(String.format("%s = %s", key, value));
	} else {
	    throw new ConfigurationException(String.format("Configuration key %s is not a valid key",
							   key));
	}
    }

    @Override
    public void removeValue(final String key) throws ConfigurationException {

	properties.remove(key);
	store();
	log.finest(String.format("Removed key %s", key));
    }

    private void loadConfiguration() throws ConfigurationException {

	properties = new Properties();

	try {
	    final FileInputStream fis = new FileInputStream(propertiesFile);
	    final Properties decryptedProperties = new Properties();
	    decryptedProperties.loadFromXML(fis);
	    process(CryptoUtil.Action.DECRYPT, decryptedProperties, this.properties);
	    fis.close();
	    validateConfiguration();
	} catch (InvalidPropertiesFormatException e) {
	    log.warning("Configuration file seems to be corrupted - creating default");
	    createDefaultConfiguration();
	} catch (Exception e) {
	    throw new ConfigurationException(String.format("Unable to load configuration file: %s",
							   e.getLocalizedMessage()), e);
	}
    }

    private void validateConfiguration() throws ConfigurationException {

	final List<Object> keysToRemove = new LinkedList<Object>();
	for (Entry<Object, Object> entry : properties.entrySet()) {
	    if (!isValidKey(entry.getKey().toString())) {
		keysToRemove.add(entry.getKey());
	    }
	}
	for (Object keyToRemove : keysToRemove) {
	    removeValue(keyToRemove.toString());
	    log.info(String.format("Configuration entry %s is not an allowed entry - thus removed",
				   keyToRemove.toString()));
	}
	for (ConfigurationKey<?> registryEntry : ConfigurationRegistry.getEntries()) {
	    if (!containsKey(registryEntry.getKey())) {
		setValue(registryEntry.getKey(), registryEntry.getDefaultValue());
		log.info(String.format("Configuration entry %s is missing in configuration - thus added with default value",
				       registryEntry.getKey()));
	    }
	}
    }

    private boolean isValidKey(final String key) {

	return ConfigurationRegistry.getEntryByKey(key) != null;
    }

    private boolean containsKey(final String key) {

	return properties.get(key) != null;
    }

    private void createDefaultConfiguration() throws ConfigurationException {

	if (propertiesFile.exists()) {
	    propertiesFile.delete();
	}

	properties = new Properties();
	for (ConfigurationKey<?> registryEntry : ConfigurationRegistry.getEntries()) {
	    setValue(registryEntry.getKey(), registryEntry.getDefaultValue());
	}

	store();
    }

    private void store() throws ConfigurationException {

	try {
	    final FileOutputStream fos = new FileOutputStream(propertiesFile);
	    final Properties encryptedProperties = new Properties();
	    process(CryptoUtil.Action.ENCRYPT, properties, encryptedProperties);
	    encryptedProperties.storeToXML(fos, null);
	    fos.flush();
	    fos.close();
	} catch (Exception e) {
	    throw new ConfigurationException("Unable to store configuration to file", e);
	}

	log.finest("Configuration stored to file.");
    }

    private static void process(final CryptoUtil.Action action,
				final Properties src,
				final Properties target) throws CryptoException {

	for (Entry<Object, Object> entry : src.entrySet()) {
	    final ConfigurationKey<?> configKey =
		    ConfigurationRegistry.getEntryByKey(entry.getKey().toString());

	    String value = entry.getValue().toString();
	    if (configKey.getDataType() == Password.class) {
		value = CryptoUtil.process(action, value);
	    }
	    target.put(configKey.getKey(), value);
	}
    }

    @Override
    public String toXml() {

	return new ControllerConfiguration(this).toXml();
    }

    @Override
    public Map<ConfigurationKey<?>, String> getData() {

	final Map<ConfigurationKey<?>, String> data = new TreeMap<ConfigurationKey<?>, String>();

	for (Entry<Object, Object> entry : properties.entrySet()) {
	    final ConfigurationKey<?> configKey =
		    ConfigurationRegistry.getEntryByKey(entry.getKey().toString());
	    data.put(configKey, entry.getValue().toString());
	}

	return data;
    }

}
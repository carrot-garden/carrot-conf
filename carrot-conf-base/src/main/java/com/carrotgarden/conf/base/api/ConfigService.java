package com.carrotgarden.conf.base.api;

import java.io.File;

import com.typesafe.config.Config;

public interface ConfigService {

	boolean isConfigAvailable();

	void updateConfig();

	Config getInstanceConfig();

	File getConfigFolder();

}

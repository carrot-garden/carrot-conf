package com.carrotgarden.conf.sync.api;

import com.typesafe.config.Config;

public interface ConfigHandler {

	void handleConfig(final Config config);

}

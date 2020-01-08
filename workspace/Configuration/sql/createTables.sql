-- Copyright 2019. Charles Coughlin. All rights reserved.
-- These tables are used by the application to hold layer definitions
-- and other configuration information.
--

-- The Layer table holds configuration information for the may overlay
-- layers. The data files must be read to actually populate the diagram.
-- Each row in the table represents an actioun at an instant of time.
-- The time increment between rows is unspecified, by default 1 second.
DROP TABLE IF EXISTS Layer;
CREATE TABLE Layer (
	id		INTEGER  PRIMARY KEY,
	name	text NOT NULL,
	description text NULL,
	shapeFilePath text NULL,
	role text NULL,
	UNIQUE (name)
);
-- The LayerFeature table holds the latest known Features for a layer.
-- An attempt is made to retain existing entries when a layer is refreshed.
DROP TABLE IF EXISTS LayerFeature;
CREATE TABLE LayerFeature (
	layerId	INTEGER  NOT NULL,
	featureAlias text NOT NULL,
	featureName text NOT NULL,
	type text NOT null,
	isHidden integer DEFAULT 0,
	PRIMARY KEY(layerId,featureAlias),
	FOREIGN KEY (layerId) references Layer(id) ON DELETE CASCADE
);
-- The Plan table holds configuration information for the may overlay
-- layers. The data files must be read to actually populate the diagram.
-- Each row in the table represents an actioun at an instant of time.
-- The time increment between rows is unspecified, by default 1 second.
DROP TABLE IF EXISTS Plan;
CREATE TABLE Plan (
	id		INTEGER  PRIMARY KEY,
	name	text NOT NULL,
	description text NULL,
	shapeFilePath text NULL,
	role text NULL,
	UNIQUE (name)
);



-- Copyright 2019-2020. Charles Coughlin. All rights reserved.
-- These tables are used by the application to hold layer definitions
-- and other configuration information.
--
-- The AttributeAlias table provides common names for
-- Feature attributes. The aliases listed here automatically applied
-- as appropriate when the attributes are created. Not all features
-- have aliases.
DROP TABLE IF EXISTS AttributeAlias;
CREATE TABLE AttributeAlias (
	name text NOT NULL PRIMARY KEY,
	alias text NOT NULL
);
-- The Layer table holds configuration information for overlay layers. 
-- The data files must be read each time the application is started
-- to actually populate the diagram.
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
DROP TABLE IF EXISTS FeatureAttribute;
CREATE TABLE FeatureAttribute (
	layerId	INTEGER  NOT NULL,
	name text NOT NULL,
	alias text NOT NULL,
	type text NOT null,
	visible integer DEFAULT 1,
	background integer DEFAULT 0,
	rank integer DEFAULT 1,
	PRIMARY KEY(layerId,name),
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
	UNIQUE (name)
);
-- The PlanLayer table maps layers to a plan. The layers have roles
-- within the plan.
DROP TABLE IF EXISTS PlanLayer;
CREATE TABLE PlanLayer (
	planId		INTEGER  NOT NULL,
	layerId		INTEGER  NOT NULL,
	role		text NOT NULL,
	PRIMARY KEY(planId,layerId),
	FOREIGN KEY (planId) references Plan(id) ON DELETE CASCADE,
	FOREIGN KEY (layerId) references Layer(id) ON DELETE CASCADE
);



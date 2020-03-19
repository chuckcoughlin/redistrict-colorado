-- Copyright 2019-2020. Charles Coughlin. All rights reserved.
-- These tables are used by the application to hold layer definitions
-- and other configuration information.
--
-- The AttributeAlias table provides common names for
-- Feature attributes. The aliases listed here automatically applied
-- as appropriate when the attributes are created. Not all features
-- have aliases. Redistricting plans rely on standard aliases to 
-- access the proper features.
DROP TABLE IF EXISTS AttributeAlias;
CREATE TABLE AttributeAlias (
	datasetId  INTEGER NOT NULL,
	name text NOT NULL,
	alias text NOT NULL,
	PRIMARY KEY(datasetId,name)
);
-- The Dataset table holds configuration information for overlay layers. 
-- The data files must be read each time the application is started
-- to actually populate the diagram.
DROP TABLE IF EXISTS Dataset;
CREATE TABLE Dataset (
	id		INTEGER  PRIMARY KEY,
	name	text NOT NULL,
	description text NULL,
	shapeFilePath text NULL,
	role text NULL,
	UNIQUE (name)
);
-- The FeatureAttribute table holds the latest known Features for a layer.
-- An attempt is made to retain existing entries when a layer is refreshed.
DROP TABLE IF EXISTS FeatureAttribute;
CREATE TABLE FeatureAttribute (
	datasetId	INTEGER  NOT NULL,
	name text NOT NULL,
	alias text NOT NULL,
	type text NOT null,
	visible integer DEFAULT 0,
	background integer DEFAULT 0,
	rank integer DEFAULT 10,
	PRIMARY KEY(datasetId,name),
	FOREIGN KEY (datasetId) references Dataset(id) ON DELETE CASCADE
);
-- The Plan table links a plan to a dataset. The dataset must
-- have a role of BOUNDARIES. We compute metrics on plans.
DROP TABLE IF EXISTS Plan;
CREATE TABLE Plan (
	id		INTEGER  PRIMARY KEY,
	name TEXT NOT NULL,
	datasetId INTEGER NULL,
	active integer DEFAULT 1,
	UNIQUE(name),
	FOREIGN KEY (datasetId) references Dataset(id) ON DELETE CASCADE
);
-- The PlanFeature table caches aggregated feature values for a plan.
-- A feature corresponds to a geographic area. The values are standardized
-- input for the comparison metrics. 
DROP TABLE IF EXISTS PlanFeature;
CREATE TABLE PlanFeature (
	planId		INTEGER  NOT NULL,
	featureId	text NOT NULL,
	area		real DEFAULT 0.,
	perimeter	real DEFAULT 0.,
	population	real DEFAULT 0.,
	democrat	real DEFAULT 0.,
	republican	real DEFAULT 0.,
	black		real DEFAULT 0.,
	hispanic	real DEFAULT 0.,
	white		real DEFAULT 0.,
	PRIMARY KEY(planId,featureId),
	FOREIGN KEY (planId) references Plan(id)
);
-- Store application constants.
DROP TABLE IF EXISTS Preferences;
CREATE TABLE Preferences (
	name text NOT NULL,
	value text NOT NULL,
	PRIMARY KEY(name)
);


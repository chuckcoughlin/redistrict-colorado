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
	id		integer   PRAMARY KEY AUTOINCREMENT,
	name	text NOT NULL,
	description text NULL,
	displayOrder integer default(0),
	shapeFilePath text NULL,
	role text NULL,
	UNIQUE (name)
);

-- The Plan table holds configuration information for the may overlay
-- layers. The data files must be read to actually populate the diagram.
-- Each row in the table represents an actioun at an instant of time.
-- The time increment between rows is unspecified, by default 1 second.
DROP TABLE IF EXISTS Plan;
CREATE TABLE Plan (
	id		integer   PRAMARY KEY AUTOINCREMENT,
	name	text NOT NULL,
	description text NULL,
	displayOrder integer default(0),
	shapeFilePath text NULL,
	role text NULL,
	UNIQUE (name)
);


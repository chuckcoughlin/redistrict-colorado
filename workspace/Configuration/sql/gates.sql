-- Copyright 2019-2020. Charles Coughlin. All rights reserved.
-- Completely replace the contents of the preferences table
--
DELETE FROM GateProperties;
REPLACE INTO GateProperties(Name,Weight,Fair,Unfair) VALUES('COMPACTNESS',1.0,0,1.0);
REPLACE INTO GateProperties(Name,Weight,Fair,Unfair) VALUES('COMPETIVENESS',1.0,7,0);
REPLACE INTO GateProperties(Name,Weight,Fair,Unfair) VALUES('COMPOSITE',1.0,10.,0.0);
REPLACE INTO GateProperties(Name,Weight,Fair,Unfair) VALUES('COUNTY_CROSSINGS',1.0,0,100.0);
REPLACE INTO GateProperties(Name,Weight,Fair,Unfair) VALUES('POPULATION_BALANCE',1.0,0.,5.0);
REPLACE INTO GateProperties(Name,Weight,Fair,Unfair) VALUES('PARTISAN_ASYMMETRY',1.0,0.0,15.0);
REPLACE INTO GateProperties(Name,Weight,Fair,Unfair) VALUES('PROPORTIONALITY',1.0,0.,1.0);
REPLACE INTO GateProperties(Name,Weight,Fair,Unfair) VALUES('VOTING_POWER',1.0,0.1,0.0);
-- Copyright 2019-2020. Charles Coughlin. All rights reserved.
-- Completely replace the contents of the preferences table
--
DELETE FROM Preferences;
REPLACE INTO Preferences(Name,Value) VALUES('GoogleKey','xxxx');
REPLACE INTO Preferences(Name,Value) VALUES('AffiliationId','-1');
REPLACE INTO Preferences(Name,Value) VALUES('CountyBoundariesId','-1');
REPLACE INTO Preferences(Name,Value) VALUES('DemographicId','-1');
REPLACE INTO Preferences(Name,Value) VALUES('CompetitivenessThreshold','15');

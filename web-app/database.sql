/* We need a separate dateTimeSpecified table that links to the Task table
    and a boolean in Task that lets you choose whether or not to use the taskInterval
    If not using the interval, go to the dateTimeSpecified table and scrape at those points */
CREATE TABLE `Task` ( `taskID` INT NOT NULL AUTO_INCREMENT , `taskName` VARCHAR(100) NOT NULL , `taskDescription` VARCHAR(500) NULL , `taskURL` VARCHAR(100) NOT NULL , `taskCreationTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , `taskInterval` INT NOT NULL , PRIMARY KEY (`taskID`)) ENGINE = InnoDB;

CREATE TABLE `Scrape` ( `scrapeID` INT NOT NULL AUTO_INCREMENT , `scrapeName` VARCHAR(100) NOT NULL , `taskID` INT NOT NULL , `Element` VARCHAR(500) NOT NULL , PRIMARY KEY (`scrapeID`), FOREIGN KEY (taskID) REFERENCES Task(taskID)) ENGINE = InnoDB;

CREATE TABLE `Result` ( `resultID` INT NOT NULL AUTO_INCREMENT , `scrapeID` INT NOT NULL , `resultTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , `resultValue` VARCHAR(500) NOT NULL , PRIMARY KEY (`resultID`), FOREIGN KEY (scrapeID) REFERENCES Scrape(scrapeID)) ENGINE = InnoDB;

CREATE TABLE `User` ( `userID` INT NOT NULL AUTO_INCREMENT , `userName` VARCHAR(100) NOT NULL , `userAdmin` BOOLEAN NOT NULL DEFAULT FALSE , `userBio` VARCHAR(500) NULL , PRIMARY KEY (`userID`)) ENGINE = InnoDB;

CREATE TABLE `MyGroup` ( `groupID` INT NOT NULL AUTO_INCREMENT , `groupName` VARCHAR(100) NOT NULL , `groupDescription` VARCHAR(500) NULL , PRIMARY KEY (`groupID`)) ENGINE = InnoDB;

CREATE TABLE `UserAuthorisation` ( `userAuthorisationID` INT NOT NULL AUTO_INCREMENT , `userID` INT NOT NULL , `taskID` INT NOT NULL , PRIMARY KEY (`userAuthorisationID`), FOREIGN KEY (userID) REFERENCES User(userID), FOREIGN KEY (taskID) REFERENCES Task(taskID)) ENGINE = InnoDB;

CREATE TABLE `UserGroup` ( `UserGroupId` INT NOT NULL AUTO_INCREMENT , `userID` INT NOT NULL , `groupID` INT NOT NULL , PRIMARY KEY (`UserGroupId`), FOREIGN KEY (userID) REFERENCES User(userID), FOREIGN KEY (groupID) REFERENCES MyGroup(groupID)) ENGINE = InnoDB;

CREATE TABLE `GroupAuthorisation` ( `groupAuthorisationID` INT NOT NULL AUTO_INCREMENT , `groupID` INT NOT NULL , `taskID` INT NOT NULL , PRIMARY KEY (`groupAuthorisationID`), FOREIGN KEY (groupID) REFERENCES MyGroup(groupID), FOREIGN KEY (taskID) REFERENCES Task(taskID)) ENGINE = InnoDB;

/* Complete the Login table below */
--CREATE TABLE `Login` ( `loginID` INT NOT NULL AUTO_INCREMENT , `userID` INT NOT NULL , `userSaltedHash` VARCHAR(255) NOT NULL , PRIMARY KEY (`loginID`)) ENGINE = InnoDB

/* Complete the example record inserts */
--INSERT INTO `User` (`groupAuthorisationIDINSERT INTO `psyhh1`.`GroupAuthorisation` (`groupAuthorisationID`, `groupID`, `taskID`) VALUES ('1', '1', '1'), ('1', '1', '1')
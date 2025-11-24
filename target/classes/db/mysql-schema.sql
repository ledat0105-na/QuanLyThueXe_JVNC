DROP DATABASE IF EXISTS FUCarRentingSystemDB;
CREATE DATABASE FUCarRentingSystemDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE FUCarRentingSystemDB;

CREATE TABLE Account (
  AccountID      BIGINT       NOT NULL AUTO_INCREMENT,
  AccountName    VARCHAR(100) NOT NULL,
  Password       VARCHAR(255) NOT NULL,
  Role           VARCHAR(50)  NOT NULL,
  PRIMARY KEY (AccountID),
  CONSTRAINT uq_account_name UNIQUE (AccountName)
);

CREATE TABLE CarProducer (
  ProducerID   BIGINT        NOT NULL AUTO_INCREMENT,
  ProducerName VARCHAR(150)  NOT NULL,
  Address      VARCHAR(255)  NOT NULL,
  Country      VARCHAR(100)  NOT NULL,
  PRIMARY KEY (ProducerID)
);

CREATE TABLE Car (
  CarID         BIGINT        NOT NULL AUTO_INCREMENT,
  CarName       VARCHAR(150)  NOT NULL,
  CarModelYear  INT           NOT NULL,
  Color         VARCHAR(50)   NOT NULL,
  Capacity      INT           NOT NULL,
  Description   TEXT          NOT NULL,
  ImportDate    DATE          NOT NULL,
  ProducerID    BIGINT        NOT NULL,
  RentPrice     DECIMAL(10,2) NOT NULL,
  Status        VARCHAR(50)   NOT NULL,
  PRIMARY KEY (CarID),
  CONSTRAINT fk_car_producer FOREIGN KEY (ProducerID)
    REFERENCES CarProducer (ProducerID)
    ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE Customer (
  CustomerID    BIGINT        NOT NULL AUTO_INCREMENT,
  CustomerName  VARCHAR(150)  NOT NULL,
  Mobile        VARCHAR(30)   NOT NULL,
  Birthday      DATE          NOT NULL,
  IdentityCard  VARCHAR(50)   NOT NULL,
  Email         VARCHAR(150)  NOT NULL,
  Password      VARCHAR(255)  NOT NULL,
  AccountID     BIGINT        NOT NULL,
  PRIMARY KEY (CustomerID),
  CONSTRAINT fk_customer_account FOREIGN KEY (AccountID)
    REFERENCES Account (AccountID)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT uq_customer_identity UNIQUE (IdentityCard),
  CONSTRAINT uq_customer_email UNIQUE (Email)
);

CREATE TABLE CarRental (
  CustomerID BIGINT        NOT NULL,
  CarID      BIGINT        NOT NULL,
  PickupDate DATE          NOT NULL,
  ReturnDate DATE          NOT NULL,
  RentPrice  DECIMAL(10,2) NOT NULL,
  Status     VARCHAR(50)   NOT NULL,
  PRIMARY KEY (CustomerID, CarID, PickupDate),
  CONSTRAINT ck_pickup_return CHECK (PickupDate < ReturnDate),
  CONSTRAINT fk_rental_customer FOREIGN KEY (CustomerID)
    REFERENCES Customer (CustomerID)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_rental_car FOREIGN KEY (CarID)
    REFERENCES Car (CarID)
    ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE Review (
  CustomerID BIGINT       NOT NULL,
  CarID      BIGINT       NOT NULL,
  ReviewStar INT          NOT NULL,
  Comment    VARCHAR(500) NOT NULL,
  PRIMARY KEY (CustomerID, CarID),
  CONSTRAINT fk_review_customer FOREIGN KEY (CustomerID)
    REFERENCES Customer (CustomerID)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_review_car FOREIGN KEY (CarID)
    REFERENCES Car (CarID)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT ck_review_star CHECK (ReviewStar BETWEEN 1 AND 5)
);


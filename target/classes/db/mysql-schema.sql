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

-- Sample data for Account & Customer
INSERT INTO Account (AccountID, AccountName, Password, Role) VALUES
(1, 'alice.nguyen', 'password123', 'Customer'),
(2, 'bao.tran', 'password123', 'Customer'),
(3, 'chi.le', 'password123', 'Customer'),
(4, 'duy.pham', 'password123', 'Customer'),
(5, 'emily.vo', 'password123', 'Customer');

INSERT INTO Customer (CustomerID, CustomerName, Mobile, Birthday, IdentityCard, Email, Password, AccountID) VALUES
(1, 'Alice Nguyen', '0901234567', STR_TO_DATE('12-05-1994', '%d-%m-%Y'), '012345678901', 'alice.nguyen@example.com', 'password123', 1),
(2, 'Bao Tran', '0912345678', STR_TO_DATE('23-11-1992', '%d-%m-%Y'), '023456789012', 'bao.tran@example.com', 'password123', 2),
(3, 'Chi Le', '0923456789', STR_TO_DATE('15-02-1990', '%d-%m-%Y'), '034567890123', 'chi.le@example.com', 'password123', 3),
(4, 'Duy Pham', '0934567890', STR_TO_DATE('30-07-1988', '%d-%m-%Y'), '045678901234', 'duy.pham@example.com', 'password123', 4),
(5, 'Emily Vo', '0945678901', STR_TO_DATE('05-09-1996', '%d-%m-%Y'), '056789012345', 'emily.vo@example.com', 'password123', 5);

INSERT INTO Account (AccountID, AccountName, Password, Role) VALUES
(6, 'admin', 'admin123', 'Admin');

INSERT INTO CarProducer (ProducerID, ProducerName, Address, Country) VALUES
(1, 'Toyota Vietnam', '123 Le Van Viet, Thu Duc, Ho Chi Minh City', 'Vietnam'),
(2, 'Honda Vietnam', '456 Nguyen Van Linh, District 7, Ho Chi Minh City', 'Vietnam'),
(3, 'Ford Vietnam', '789 Vo Van Tan, District 3, Ho Chi Minh City', 'Vietnam'),
(4, 'Mercedes-Benz Vietnam', '321 Dong Khoi, District 1, Ho Chi Minh City', 'Vietnam');

INSERT INTO Car (CarID, CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status) VALUES
(1, 'Toyota Camry 2023', 2023, 'Xanh dương', 5, 'Xe sedan cao cấp, tiết kiệm nhiên liệu', STR_TO_DATE('15-01-2023', '%d-%m-%Y'), 1, 2500000.00, 'Available'),
(2, 'Honda Civic 2022', 2022, 'Trắng', 5, 'Xe sedan thể thao, động cơ mạnh mẽ', STR_TO_DATE('20-03-2022', '%d-%m-%Y'), 2, 2200000.00, 'Available'),
(3, 'Ford Ranger 2023', 2023, 'Đen', 5, 'Xe bán tải, phù hợp đường địa hình', STR_TO_DATE('10-05-2023', '%d-%m-%Y'), 3, 3000000.00, 'Rented'),
(4, 'Mercedes-Benz C200 2023', 2023, 'Bạc', 5, 'Xe sang trọng, đầy đủ tính năng', STR_TO_DATE('25-02-2023', '%d-%m-%Y'), 4, 5000000.00, 'Available'),
(5, 'Toyota Vios 2022', 2022, 'Đỏ', 5, 'Xe sedan phổ biến, giá hợp lý', STR_TO_DATE('18-06-2022', '%d-%m-%Y'), 1, 1500000.00, 'Available'),
(6, 'Honda CR-V 2023', 2023, 'Xám', 7, 'SUV 7 chỗ, không gian rộng rãi', STR_TO_DATE('12-04-2023', '%d-%m-%Y'), 2, 3500000.00, 'Maintenance'),
(7, 'Ford Explorer 2022', 2022, 'Xanh lá', 7, 'SUV lớn, phù hợp gia đình', STR_TO_DATE('08-08-2022', '%d-%m-%Y'), 3, 4000000.00, 'Available');

INSERT INTO CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status) VALUES
(1, 1, STR_TO_DATE('01-11-2024', '%d-%m-%Y'), STR_TO_DATE('05-11-2024', '%d-%m-%Y'), 10000000.00, 'Đã hoàn thành'),
(2, 2, STR_TO_DATE('10-11-2024', '%d-%m-%Y'), STR_TO_DATE('15-11-2024', '%d-%m-%Y'), 11000000.00, 'Đã hoàn thành'),
(1, 4, STR_TO_DATE('20-11-2024', '%d-%m-%Y'), STR_TO_DATE('25-11-2024', '%d-%m-%Y'), 25000000.00, 'Đang thuê'),
(3, 2, STR_TO_DATE('15-12-2024', '%d-%m-%Y'), STR_TO_DATE('20-12-2024', '%d-%m-%Y'), 11000000.00, 'Đang thuê'),
(3, 5, STR_TO_DATE('05-10-2024', '%d-%m-%Y'), STR_TO_DATE('10-10-2024', '%d-%m-%Y'), 7500000.00, 'Đã hoàn thành'),
(4, 7, STR_TO_DATE('01-09-2024', '%d-%m-%Y'), STR_TO_DATE('07-09-2024', '%d-%m-%Y'), 24000000.00, 'Đã hoàn thành'),
(5, 1, STR_TO_DATE('25-11-2024', '%d-%m-%Y'), STR_TO_DATE('30-11-2024', '%d-%m-%Y'), 12500000.00, 'Đã hủy');

INSERT INTO Review (CustomerID, CarID, ReviewStar, Comment) VALUES
(1, 1, 5, 'Xe rất đẹp và tiết kiệm nhiên liệu. Nhân viên phục vụ tận tình. Sẽ thuê lại lần sau!'),
(2, 2, 4, 'Xe động cơ mạnh, lái rất êm. Chỉ có điều giá hơi cao một chút.'),
(3, 5, 5, 'Xe phù hợp với nhu cầu của tôi. Giá cả hợp lý và dịch vụ tốt.'),
(4, 7, 4, 'Xe rộng rãi, phù hợp cho gia đình. Chất lượng tốt nhưng giá hơi cao.');


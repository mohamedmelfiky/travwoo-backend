CREATE TABLE IF NOT EXISTS airports
(
    icao         VARCHAR(4) PRIMARY KEY,
    iata         VARCHAR(3),
    name         VARCHAR(255),
    city         VARCHAR(255),
    state        VARCHAR(255),
    country_code VARCHAR(2),
    country      VARCHAR(255),
    tz           VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS airlines
(
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255),
    logo VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS aircrafts
(
    iata VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS payments
(
    id         VARCHAR2(36) PRIMARY KEY,
    data       VARCHAR2(4000),
    status     VARCHAR2(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
DROP TABLE IF EXISTS users, categories, event_location, events, requests, compilations, event_compilation;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_location (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT,
    confirmed_requests INT,
    created_on TIMESTAMP,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP,
    initiator_id BIGINT,
    location_id BIGINT,
    paid BOOLEAN,
    participant_limit INT,
    published_on TIMESTAMP,
    request_moderation BOOLEAN,
    state VARCHAR(255),
    views BIGINT,
    FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES event_location (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS requests (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    event_id BIGINT,
    requester_id BIGINT,
    created TIMESTAMP,
    status VARCHAR(255),
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_compilation (
    event_id BIGINT,
    compilation_id BIGINT,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE
);
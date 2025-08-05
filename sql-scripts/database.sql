CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(255),
                      first_name VARCHAR(255),
                      last_name VARCHAR(255),
                      email VARCHAR(255) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      phone_number VARCHAR(255) NOT NULL UNIQUE,
                      role VARCHAR(50) NOT NULL,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hotel (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       description TEXT,
                       rating DOUBLE,
                       owner_id BIGINT NOT NULL,
                       FOREIGN KEY (owner_id) REFERENCES user(id)
);

CREATE TABLE amenity (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(255) NOT NULL,
                         description TEXT
);

CREATE TABLE hotel_amenities (
                                 hotel_id BIGINT NOT NULL,
                                 amenity_id BIGINT NOT NULL,
                                 PRIMARY KEY (hotel_id, amenity_id),
                                 FOREIGN KEY (hotel_id) REFERENCES hotel(id),
                                 FOREIGN KEY (amenity_id) REFERENCES amenity(id)
);

CREATE TABLE room (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      hotel BIGINT NOT NULL,
                      type VARCHAR(50) NOT NULL,
                      capacity INT NOT NULL,
                      price DOUBLE NOT NULL,
                      description TEXT,
                      available BOOLEAN NOT NULL,
                      FOREIGN KEY (hotel) REFERENCES hotel(id)
);

CREATE TABLE room_amenities (
                                room_id BIGINT NOT NULL,
                                amenity_id BIGINT NOT NULL,
                                PRIMARY KEY (room_id, amenity_id),
                                FOREIGN KEY (room_id) REFERENCES room(id),
                                FOREIGN KEY (amenity_id) REFERENCES amenity(id)
);

CREATE TABLE booking (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         room_id BIGINT NOT NULL,
                         check_in_date DATE NOT NULL,
                         check_out_date DATE NOT NULL,
                         number_of_guests INT NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         total_price DOUBLE NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES user(id),
                         FOREIGN KEY (room_id) REFERENCES room(id)
);
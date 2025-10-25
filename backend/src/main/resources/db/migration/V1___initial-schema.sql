-- game
CREATE SEQUENCE game_seq;
CREATE TABLE game
(
    id          BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('game_seq'::regclass),
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,

    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated     TIMESTAMP WITHOUT TIME ZONE
);
-- player
CREATE SEQUENCE player_seq;
CREATE TABLE player
(
    id      BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('player_seq'::regclass),
    name    VARCHAR(255) NOT NULL,

    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated TIMESTAMP WITHOUT TIME ZONE
);
-- unit
CREATE SEQUENCE unit_type_seq;
CREATE TABLE unit_type
(
    id                BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('player_seq'::regclass),
    name              VARCHAR(255) NOT NULL,
    unit_class        VARCHAR(255) NOT NULL,
    counter_class     VARCHAR(255),
    min_damage_points INT          NOT NULL,
    max_damage_points INT          NOT NULL,
    min_health_points INT          NOT NULL,
    max_health_points INT          NOT NULL,
    max_level         INT          NOT NULL,

    created           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated           TIMESTAMP WITHOUT TIME ZONE
);
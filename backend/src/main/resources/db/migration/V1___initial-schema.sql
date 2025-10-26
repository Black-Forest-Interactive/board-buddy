-- unit
CREATE SEQUENCE unit_type_seq;
CREATE TABLE unit_type
(
    id                BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('unit_type_seq'::regclass),
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

-- rule set
CREATE SEQUENCE rule_set_seq;
CREATE TABLE rule_set
(
    id      BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('rule_set_seq'::regclass),
    name    VARCHAR(255) NOT NULL,

    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE ruleset_unit_type
(
    rule_set_id   BIGINT REFERENCES rule_set (id),
    unit_type_id BIGINT REFERENCES unit_type (id),
    PRIMARY KEY (rule_set_id, unit_type_id)
);

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

CREATE TABLE game_ruleset
(
    game_id    BIGINT REFERENCES game (id),
    rule_set_id BIGINT REFERENCES rule_set (id),
    PRIMARY KEY (game_id, rule_set_id)
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

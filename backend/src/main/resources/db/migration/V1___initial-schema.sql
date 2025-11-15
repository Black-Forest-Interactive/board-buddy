-- unit
CREATE SEQUENCE unit_definition_seq;
CREATE TABLE unit_definition
(
    id                BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('unit_definition_seq'::regclass),
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

CREATE SEQUENCE unit_instance_seq;
CREATE TABLE unit_instance
(
    id                 BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('unit_instance_seq'::regclass),
    damage             INT    NOT NULL,
    health             INT    NOT NULL,
    level              INT    NOT NULL,

    unit_definition_id BIGINT REFERENCES unit_definition (id),

    created            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated            TIMESTAMP WITHOUT TIME ZONE
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

CREATE TABLE rule_set_unit_definition
(
    rule_set_id        BIGINT REFERENCES rule_set (id),
    unit_definition_id BIGINT REFERENCES unit_definition (id),
    PRIMARY KEY (rule_set_id, unit_definition_id)
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

CREATE TABLE game_rule_set
(
    game_id     BIGINT REFERENCES game (id),
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

-- session
CREATE SEQUENCE game_session_seq;
CREATE TABLE game_session
(
    id          BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('game_session_seq'::regclass),
    key         VARCHAR(40)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    host_id     BIGINT REFERENCES player (id),
    game_id     BIGINT REFERENCES game (id),
    rule_set_id BIGINT REFERENCES rule_set (id),

    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated     TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE game_session_player
(
    game_session_id BIGINT REFERENCES game_session (id),
    player_id       BIGINT REFERENCES player (id),
    PRIMARY KEY (game_session_id, player_id)
);


CREATE TABLE game_session_unit
(
    game_session_id  BIGINT REFERENCES game_session (id),
    player_id        BIGINT REFERENCES player (id),
    unit_instance_id BIGINT REFERENCES unit_instance (id),
    PRIMARY KEY (game_session_id, player_id, unit_instance_id)
);

-- engine

CREATE SEQUENCE game_entity_seq;
CREATE TABLE game_entity
(
    id      BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('game_entity_seq'::regclass),
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE component_counter_type
(
    entity_id      BIGINT      NOT NULL PRIMARY KEY references game_entity (id),

    kind    VARCHAR(50) NOT NULL,

    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE component_damage
(
    entity_id      BIGINT NOT NULL PRIMARY KEY references game_entity (id),

    amount  INT    NOT NULL,

    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated TIMESTAMP WITHOUT TIME ZONE
);


CREATE TABLE component_health
(
    entity_id      BIGINT NOT NULL PRIMARY KEY references game_entity (id),

    amount  INT    NOT NULL,

    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE component_level
(
    entity_id      BIGINT      NOT NULL PRIMARY KEY references game_entity (id),

    value    INT    NOT NULL,

    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated TIMESTAMP WITHOUT TIME ZONE
);



CREATE TABLE component_type
(
    entity_id      BIGINT      NOT NULL PRIMARY KEY references game_entity (id),

    kind    VARCHAR(50) NOT NULL,

    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated TIMESTAMP WITHOUT TIME ZONE
);
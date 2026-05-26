-- unit
CREATE SEQUENCE unit_definition_seq;
CREATE TABLE unit_definition
(
    id                BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('unit_definition_seq'::regclass),
    name              VARCHAR(255) NOT NULL,
    unit_type         VARCHAR(255) NOT NULL,
    counter_type      VARCHAR(255),
    min_damage_points INT          NOT NULL,
    max_damage_points INT          NOT NULL,
    min_health_points INT          NOT NULL,
    max_health_points INT          NOT NULL,
    max_level         INT          NOT NULL,

    created           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated           TIMESTAMP WITHOUT TIME ZONE
);

-- technology
CREATE SEQUENCE technology_seq;
CREATE TABLE technology
(
    id          BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('technology_seq'::regclass),
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    image_url   VARCHAR(255) NOT NULL,
    tier        INT          NOT NULL,

    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated     TIMESTAMP WITHOUT TIME ZONE
);

CREATE SEQUENCE technology_effect_unit_unlock_seq;
CREATE TABLE technology_effect_unit_unlock
(
    id            BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('technology_effect_unit_unlock_seq'::regclass),
    technology_id BIGINT       NOT NULL REFERENCES technology (id),
    unit_type     VARCHAR(255) NOT NULL,
    unit_level    INT          NOT NULL,

    created       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated       TIMESTAMP WITHOUT TIME ZONE
);

-- nation
CREATE SEQUENCE nation_seq;
CREATE TABLE nation
(
    id          BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('nation_seq'::regclass),
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    image_url   VARCHAR(255) NOT NULL,

    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated     TIMESTAMP WITHOUT TIME ZONE
);

CREATE SEQUENCE nation_effect_initial_government_seq;
CREATE TABLE nation_effect_initial_government
(
    id        BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('nation_effect_initial_government_seq'::regclass),
    nation_id BIGINT       NOT NULL REFERENCES nation (id),
    type      VARCHAR(255) NOT NULL,

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
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

CREATE TABLE rule_set_technology
(
    rule_set_id   BIGINT REFERENCES rule_set (id),
    technology_id BIGINT REFERENCES technology (id),
    PRIMARY KEY (rule_set_id, technology_id)
);

CREATE TABLE rule_set_nation
(
    rule_set_id   BIGINT REFERENCES rule_set (id),
    nation_id BIGINT REFERENCES nation (id),
    PRIMARY KEY (rule_set_id, nation_id)
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


-- engine

CREATE SEQUENCE game_entity_seq;
CREATE TABLE game_entity
(
    id      BIGINT      NOT NULL PRIMARY KEY DEFAULT nextval('game_entity_seq'::regclass),
    type    VARCHAR(50) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE component_counter_type
(
    entity_id BIGINT      NOT NULL PRIMARY KEY references game_entity (id),

    kind      VARCHAR(50) NOT NULL,

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE component_damage
(
    entity_id BIGINT NOT NULL PRIMARY KEY references game_entity (id),

    amount    INT    NOT NULL,

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
);


CREATE TABLE component_health
(
    entity_id BIGINT NOT NULL PRIMARY KEY references game_entity (id),

    amount    INT    NOT NULL,

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE component_level
(
    entity_id BIGINT NOT NULL PRIMARY KEY references game_entity (id),

    value     INT    NOT NULL,

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
);



CREATE TABLE component_type
(
    entity_id BIGINT      NOT NULL PRIMARY KEY references game_entity (id),

    kind      VARCHAR(50) NOT NULL,

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE component_government
(
    entity_id BIGINT      NOT NULL PRIMARY KEY references game_entity (id),

    type      VARCHAR(50) NOT NULL,

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE component_nation
(
    entity_id BIGINT NOT NULL PRIMARY KEY references game_entity (id),

    nation_id BIGINT NOT NULL REFERENCES nation (id),

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
);


CREATE TABLE component_technology
(
    entity_id BIGINT      NOT NULL PRIMARY KEY references game_entity (id),

    technologies JSONB                       NOT NULL,

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE component_unit_progress
(
    entity_id BIGINT NOT NULL PRIMARY KEY references game_entity (id),

    levels    JSONB                       NOT NULL,

    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated   TIMESTAMP WITHOUT TIME ZONE
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
    entity_id       BIGINT REFERENCES game_entity (id),
    PRIMARY KEY (game_session_id, player_id)
);


CREATE TABLE game_session_entity
(
    game_session_id BIGINT REFERENCES game_session (id),
    player_id       BIGINT REFERENCES player (id),
    entity_id       BIGINT REFERENCES game_entity (id),
    PRIMARY KEY (game_session_id, player_id, entity_id)
);

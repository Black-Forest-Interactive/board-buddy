package de.sambalmueslie.boardbuddy.engine.api

enum class TechnologyType(val tier: Int) {

    // Tier I
    ANIMAL_HUSBANDRY(1),
    AGRICULTURE(1),
    CODE_OF_LAWS(1),
    HORSEBACK_RIDING(1),
    MASONRY(1),
    METAL_WORKING(1),
    NAVIGATION(1),
    PHILOSOPHY(1),
    POTTERY(1),
    WRITING(1),


    // Tier II
    CIVIL_SERVICE(2),
    CHIVALRY(2),
    CONSTRUCTION(2),
    DEMOCRACY(2),
    ENGINEERING(2),
    IRRIGATION(2),
    MATHEMATICS(2),
    MONARCHY(2),
    MYSTICISM(2),
    PRINTING_PRESS(2),
    SAILING(2),

    // Tier III
    BANKING(3),
    BIOLOGY(3),
    COMMUNISM(3),
    ECOLOGY(3),
    GUNPOWDER(3),
    METAL_CASTING(3),
    MILITARY_SCIENCE(3),
    RAILROAD(3),
    STEAM_POWER(3),
    THEOLOGY(3),

    // Tier IV
    ATOMIC_THEORY(4),
    BALLISTICS(4),
    COMBUSTION(4),
    COMPUTERS(4),
    FLIGHT(4),
    MASS_MEDIA(4),
    PLASTICS(4),
    REPLACEABLE_PARTS(4),

    // Tier V
    SPACE_FLIGHT(5)
}
package com.codecool.dungeoncrawl.logic.gameobject.actors.player.classes;

import com.codecool.dungeoncrawl.logic.gameobject.actors.attributes.Dexterity;
import com.codecool.dungeoncrawl.logic.gameobject.actors.attributes.Health;
import com.codecool.dungeoncrawl.logic.gameobject.actors.attributes.Strength;

public class WarriorHeroClass extends HeroClass {

    private static Health HEALTH = new Health(10);
    private static Strength STRENGTH = new Strength();
    private static Dexterity DEXTERITY = new Dexterity();

    @Override
    public String getTileName() {
        return "player-warrior";
    }
}

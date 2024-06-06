package nebpoints.nebpoints.dataFiles;

import java.util.Random;

public class Disasters {
    public String[] disasterId = {//do not change, just add
            "zombie",
            "slime",
            "levitation",
            "skeleton",
            "chillbreak",
            "toggleplatform",
            "creepers",
            "fireball",
            "togglemainplatform",
            "blindness",
            "toggle3platforms",
    };
    public String[] disasterRequirements = {
            "_default_",
            "_default_",
            "_default_",
            "_default_",
            "_default_",
            "_2MainPlatformLevels_",
            "_default_",
            "_default_",
            "_default_",
            "_default_",
            "_3MainPlatformLevels_",
    };
    public String[] disasterNames = {
            "Zom-bounce Squad",
            "King Slime Dan",
            "Up you go!",
            "Skeleton Trap",
            "Chill Break Time",
            "Mini Platform",
            "Aw man",
            "Fireball!",
            "Deteriorating Platform",
            "I don't see any problem here",
            "Platform Resize (ERROR)",//here
    };
    public int[] disasterTimes = {
            7*20,//zomb
            7*20,//slime
            1*20,//levi
            6*20,//skele
            5*20,//chill
            1*20,//mini plat
            3*20,//awwman
            2*20,//fireball
            6*20,//holes (2)
            1*20,//blideness
            6*20,//holes (3)
    };
    public int[] weight = {
            6,//zomb
            3,//slime
            6,//levi
            5,//skele
            1,//chill
            5,//mini plat
            5,//awwman
            8,//fireball
            3,//deteriorating platform (2)
            3,//blindness
            0,//deteriorating platform (3)
    };

    public Integer getRandomDisasterId() {
        int max = 0;
        for (int w : weight) {max += w;}
        int choice = new Random(System.currentTimeMillis()).nextInt(max);
        int index = 0;
        for (int w : weight) {
            if (choice > w) {
                choice-=w;
                index++;
            } else {
                break;
            }
        }
        return index;
        //return new Random().nextInt(disasterNames.length);
    }
}

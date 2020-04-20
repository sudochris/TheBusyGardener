package de.elektropapst.ld46.assets;

import de.elektropapst.ld46.Statics;
import de.elektropapst.ld46.assets.enums.Sounds;

public class SoundManager {
    public void stopSound(Sounds sound, long id) {
        Statics.assets.getSound(sound).stop(id);
    }

    public long loopSound(Sounds sound) {
        return loopSound(sound, Statics.settings.SOUND_VOLUME);
    }

    public long loopSound(Sounds sound, float volume) {
        return Statics.assets.getSound(sound).loop(volume);
    }

    public long playSound(Sounds sound) {
        return playSound(sound, Statics.settings.SOUND_VOLUME);
    }

    public long playSound(Sounds sound, float volume) {
        return Statics.assets.getSound(sound).play(volume);
    }

}
package me.krizzdawg.wildpvp.queue;

import lombok.Getter;

@Getter
public enum QueueState {

    IDLE(true, false), INVINCIBILITY(false, true), END(false, true);

    private boolean joinable, teleported;

    QueueState(boolean joinable, boolean teleported) {
        this.joinable = joinable;
        this.teleported = teleported;
    }

}

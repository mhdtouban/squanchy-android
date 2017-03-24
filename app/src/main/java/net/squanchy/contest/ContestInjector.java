package net.squanchy.contest;

import net.squanchy.injection.ApplicationInjector;
import net.squanchy.speaker.DaggerSpeakerDetailsComponent;

public class ContestInjector {

    private ContestInjector() {}

    public static ContestComponent obtain(ContestActivity activity) {
        return DaggerContestComponent.builder()
                .applicationComponent(ApplicationInjector.obtain(activity))
                .contestModule(new ContestModule())
                .build();
    }

}

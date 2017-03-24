package net.squanchy.contest;

import net.squanchy.injection.ActivityLifecycle;
import net.squanchy.injection.ApplicationComponent;
import net.squanchy.remoteconfig.RemoteConfig;

import dagger.Component;

@ActivityLifecycle
@Component(modules = {ContestModule.class}, dependencies = ApplicationComponent.class)
public interface ContestComponent {

    ContestService contestService();

}

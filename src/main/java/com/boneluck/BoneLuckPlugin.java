package com.boneluck;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
				name = "Bone Luck",
				description = "Shows how lucky you've gotten offering your bones.",
				tags = {"prayer", "bone", "luck"},
				loadWhenOutdated = true,
				enabledByDefault = false
)

@Slf4j
public class BoneLuckPlugin extends Plugin
{
	int CHAOS_ALTAR_REGION = 11835;
	Skill PRAYER = Skill.PRAYER;
	private float used, saved, percent, actualXpGained, expectedXpGained;
	private Instant lastUsedBone;
	private boolean inRegion, wasInRegion;
	private int previousXp;

	private static final Pattern BONE_SAVED_CHECK = Pattern.compile(
					"The Dark Lord spares your sacrifice but still rewards you for your efforts\\.");
	@Inject
	private Client client;

	@Inject
	private BoneLuckOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private BoneLuckConfig config;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		resetRate();
	}
	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}
	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if(chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM)
		{
			checkSaved(chatMessage);
			calculatePercent();
		}
	}
	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		Skill skill = statChanged.getSkill();
		int currentXp = statChanged.getXp();

		if (skill == PRAYER && previousXp == 0) {
			previousXp = currentXp;
			return;
		}

		// StatChanged event occurs when stats drain/boost; check we have an increase to xp
		if (skill == PRAYER && inRegion && currentXp > previousXp)
		{
			actualXpGained += currentXp - previousXp;
			lastUsedBone = Instant.now();
			used++;
			previousXp = currentXp;
			calculatePercent();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!(client.getLocalPlayer().getWorldLocation().getRegionID() == CHAOS_ALTAR_REGION))
		{
			if (inRegion)
			{
				inRegion = false;
			}
			return;
		}

		if (!inRegion)
		{
			wasInRegion = true;
			inRegion = true;
		}
	}

	@Provides
	BoneLuckConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BoneLuckConfig.class);
	}

	private void resetRate()
	{
		used = 0.0f;
		saved = 0.0f;
		percent = 0.0f;
		actualXpGained = 0.0f;
		expectedXpGained = 0.0f;
	}

	private void checkSaved(ChatMessage chatMessage)
	{
		Matcher savedCheck = BONE_SAVED_CHECK.matcher(chatMessage.getMessage());
		if (savedCheck.find())
		{
			saved++;
		}
	}

	private void calculatePercent() {
		percent = (saved / used) * 100;
		float bonesReallyUsed = used - saved;
		float avgXpPerBone = actualXpGained / used;
		expectedXpGained = bonesReallyUsed * 2 * avgXpPerBone;
	}

	public float getSaved() {
		return saved;
	}

	public float getUsed() {
		return used;
	}

	public float getPercent() {
		return percent;
	}

	public float getActualXpGained() {
		return actualXpGained;
	}

	public float getExpectedXpGained() {
		return expectedXpGained;
	}

	public Instant getLastUsedBone() {
		return lastUsedBone;
	}

	public boolean isInRegion() {
		return inRegion;
	}

	public boolean wasInRegion() {
		return wasInRegion;
	}
}
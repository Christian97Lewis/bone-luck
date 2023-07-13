package com.boneluck;

import net.runelite.client.config.*;

@ConfigGroup("BoneLuck")
public interface BoneLuckConfig extends Config
{
	@Range(
					max = 60
	)
	@ConfigItem(
					keyName = "overlayDuration",
					name = "Overlay Duration",
					description = "How long the overlay lasts between bones offered. Zero means overlay will never go away.",
					position = 1
	)
	default int overlayDuration(){ return 30;}

	@Range(
					max = 60
	)
	@ConfigItem(
					keyName = "showActualXpGained",
					name = "Show Actual XP Gained",
					description = "Show how much prayer you've gained during this session.",
					position = 1
	)
	default boolean showActualXpGained(){
		return false;
	}

	@Range(
					max = 60
	)
	@ConfigItem(
					keyName = "showExpectedXpGained",
					name = "Show Expected XP Gained",
					description = "Show how much prayer you've should've gained based on average odds during this session.",
					position = 1
	)
	default boolean showExpectedXpGained(){
		return false;
	}

}


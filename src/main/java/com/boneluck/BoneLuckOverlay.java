package com.boneluck;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class BoneLuckOverlay extends OverlayPanel
{

  private final Client client;
  private final BoneLuckPlugin plugin;
  private final BoneLuckConfig config;

  @Inject
  BoneLuckOverlay(Client client, BoneLuckPlugin plugin, BoneLuckConfig config)
  {
    super(plugin);
    this.client = client;
    this.plugin = plugin;
    this.config = config;
    setPosition(OverlayPosition.TOP_LEFT);
    getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "PP overlay"));
  }

  @Override
  public Dimension render(Graphics2D graphics)
  {
    if(plugin.getLastUsedBone() != null && plugin.wasInRegion() && (config.overlayDuration() <1  || Duration.between(plugin.getLastUsedBone(), Instant.now()).getSeconds() < config.overlayDuration()))
    {
      panelComponent.getChildren().add(TitleComponent.builder()
              .text("Bone Luck info")
              .color(Color.WHITE)
              .build());
      panelComponent.getChildren().add(LineComponent.builder()
              .left("Bones Used")
              .right(String.format("%.0f",plugin.getUsed()))
              .build());
      panelComponent.getChildren().add(LineComponent.builder()
              .left("Bones Saved")
              .right(String.format("%.0f",plugin.getSaved()))
              .build());
      panelComponent.getChildren().add(LineComponent.builder()
              .left("% Saved")
              .right(String.format("%.1f",plugin.getPercent())+"%")
              .build());
      if(config.showActualXpGained())
      {
        panelComponent.getChildren().add(LineComponent.builder()
                .left("XP Gained")
                .right(String.format("%.0f",plugin.getActualXpGained()))
                .build());
      }
      if(config.showExpectedXpGained())
      {
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Expected XP")
                .right(String.format("%.0f",plugin.getExpectedXpGained()))
                .build());
      }
    }
    return super.render(graphics);
  }
}
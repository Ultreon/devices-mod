package dev.ultreon.devices.object.tiles;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.object.Game;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class TileCactus extends Tile
{
	public TileCactus(int id)
	{
		super(id, 3, 2);
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, Game game, int x, int y, Game.Layer layer)
	{
		if(game.getTile(layer.up(), x, y - 1) != this || layer == Game.Layer.FOREGROUND)
		{
			RenderUtil.drawRectWithTexture(null, graphics,game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 5.5, layer.zLevel, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);
			RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * WIDTH + 0.5, game.yPosition + y * HEIGHT - 5.5, layer.zLevel, (this.x + 1) * 16 + 1, this.y * 16 + 1, WIDTH - 1, HEIGHT - 1, 14, 14);
		}

		RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 0.5, layer.zLevel, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16, 0xffa0a0a0);
	}

	@Override
	public boolean isFullTile()
	{
		return false;
	}
}

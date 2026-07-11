package dev.ultreon.devices.block.entity.renderer;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public record TextureInfo(
        Identifier id,
        DynamicTexture texture
) {
}

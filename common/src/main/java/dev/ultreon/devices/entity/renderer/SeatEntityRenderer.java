package dev.ultreon.devices.entity.renderer;

import dev.ultreon.devices.entity.SeatEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class SeatEntityRenderer
        extends EntityRenderer<SeatEntity, SeatRenderState> {

    public SeatEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NonNull SeatRenderState createRenderState() {
        return new SeatRenderState();
    }
}